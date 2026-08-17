package com.brouken.player.core.audiodsp;

import androidx.media3.common.C;
import androidx.media3.common.audio.AudioProcessor;
import androidx.media3.common.audio.AudioProcessor.AudioFormat;
import androidx.media3.common.audio.AudioProcessor.UnhandledAudioFormatException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 10-band graphic equalizer, implemented as a Media3 {@link AudioProcessor}: a cascade of
 * {@link BiquadFilter} peaking-EQ bands per channel, applied to 16-bit PCM audio in real time.
 *
 * <p><b>Risk/verification note</b>, written deliberately rather than left implicit: this file's
 * correctness rests on two different kinds of claim with very different confidence levels.
 * <ul>
 *   <li>The DSP math ({@link BiquadCoefficients}, {@link BiquadFilter}) is standard, published,
 *       and unit-tested against hand-computable cases (see the test suite) — high confidence.
 *   <li>The exact shape of Media3's {@link AudioProcessor} interface used here — the
 *       {@code AudioFormat} constructor/field names, the {@code NOT_SET} sentinel,
 *       {@code UnhandledAudioFormatException}'s constructor, and the queueInput/getOutput
 *       buffer-ownership contract — is written from well-established public usage patterns, not
 *       verified against this exact compiled artifact. Unlike a missing Gradle dependency (which
 *       fails loudly and immediately at compile time), a subtly wrong buffer-handling assumption
 *       here could compile fine and only surface as audio corruption — which is exactly why this
 *       processor defaults to {@link #setEnabled disabled} and is not wired into playback by any
 *       default-on path in this milestone.
 * </ul>
 */
public final class GraphicEqualizerAudioProcessor implements AudioProcessor {

    private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0).order(ByteOrder.nativeOrder());

    private AudioFormat inputAudioFormat = AudioFormat.NOT_SET;
    private AudioFormat outputAudioFormat = AudioFormat.NOT_SET;

    private ByteBuffer outputBuffer = EMPTY_BUFFER;
    private boolean inputEnded;

    private final float[] bandGainsDb = new float[GraphicEqualizerBands.BAND_COUNT];
    private BiquadFilter[][] filtersByChannel = new BiquadFilter[0][];
    private volatile boolean enabled = false;
    private boolean coefficientsDirty = true;

    /** Master on/off. When off, {@link #isActive()} returns false and ExoPlayer bypasses this processor entirely. */
    public synchronized void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public synchronized boolean isEnabled() {
        return enabled;
    }

    /** @param gainsDb one gain-in-dB value per band, in {@link GraphicEqualizerBands#FREQUENCIES_HZ} order */
    public synchronized void setBandGainsDb(float[] gainsDb) {
        int n = Math.min(gainsDb.length, bandGainsDb.length);
        for (int i = 0; i < n; i++) {
            bandGainsDb[i] = (float) GraphicEqualizerBands.clampGainDb(gainsDb[i]);
        }
        coefficientsDirty = true;
    }

    public synchronized void setBandGainDb(int bandIndex, float gainDb) {
        if (bandIndex < 0 || bandIndex >= bandGainsDb.length) {
            return;
        }
        bandGainsDb[bandIndex] = (float) GraphicEqualizerBands.clampGainDb(gainDb);
        coefficientsDirty = true;
    }

    public synchronized float[] getBandGainsDb() {
        return bandGainsDb.clone();
    }

    @Override
    public AudioFormat configure(AudioFormat inputAudioFormat) throws UnhandledAudioFormatException {
        if (inputAudioFormat.encoding != C.ENCODING_PCM_16BIT) {
            throw new UnhandledAudioFormatException(inputAudioFormat);
        }
        this.inputAudioFormat = inputAudioFormat;
        this.outputAudioFormat = inputAudioFormat; // same format out: no resampling/remixing, filtering only
        allocateFilters(inputAudioFormat.channelCount, inputAudioFormat.sampleRate);
        return outputAudioFormat;
    }

    private void allocateFilters(int channelCount, int sampleRate) {
        BiquadFilter[][] newFilters = new BiquadFilter[channelCount][GraphicEqualizerBands.BAND_COUNT];
        for (int ch = 0; ch < channelCount; ch++) {
            for (int band = 0; band < GraphicEqualizerBands.BAND_COUNT; band++) {
                BiquadCoefficients coeffs = BiquadCoefficients.peakingEq(
                        sampleRate, GraphicEqualizerBands.FREQUENCIES_HZ[band], bandGainsDb[band], GraphicEqualizerBands.DEFAULT_Q);
                newFilters[ch][band] = new BiquadFilter(coeffs);
            }
        }
        filtersByChannel = newFilters;
        coefficientsDirty = false;
    }

    private void refreshCoefficientsIfNeeded() {
        if (!coefficientsDirty || filtersByChannel.length == 0) {
            return;
        }
        int sampleRate = inputAudioFormat.sampleRate;
        for (BiquadFilter[] channelFilters : filtersByChannel) {
            for (int band = 0; band < channelFilters.length; band++) {
                BiquadCoefficients coeffs = BiquadCoefficients.peakingEq(
                        sampleRate, GraphicEqualizerBands.FREQUENCIES_HZ[band], bandGainsDb[band], GraphicEqualizerBands.DEFAULT_Q);
                channelFilters[band].setCoefficients(coeffs);
            }
        }
        coefficientsDirty = false;
    }

    @Override
    public boolean isActive() {
        return enabled && outputAudioFormat.encoding == C.ENCODING_PCM_16BIT;
    }

    @Override
    public void queueInput(ByteBuffer inputBuffer) {
        int channelCount = inputAudioFormat.channelCount;
        int frameSizeBytes = channelCount * 2; // 16-bit PCM = 2 bytes/sample
        int frameCount = inputBuffer.remaining() / frameSizeBytes;
        int usableBytes = frameCount * frameSizeBytes;
        if (frameCount == 0) {
            return;
        }

        if (!enabled) {
            // Bypass, but still must copy into our own buffer — the caller reclaims
            // `inputBuffer` for reuse once this call returns, so we can't just alias it.
            outputBuffer = allocateOutputBuffer(usableBytes);
            int originalLimit = inputBuffer.limit();
            inputBuffer.limit(inputBuffer.position() + usableBytes);
            outputBuffer.put(inputBuffer);
            inputBuffer.limit(originalLimit);
            outputBuffer.flip();
            return;
        }

        refreshCoefficientsIfNeeded();

        outputBuffer = allocateOutputBuffer(usableBytes);
        ByteBuffer in = inputBuffer.duplicate();
        in.order(ByteOrder.LITTLE_ENDIAN); // Android PCM is little-endian
        outputBuffer.order(ByteOrder.LITTLE_ENDIAN);

        for (int frame = 0; frame < frameCount; frame++) {
            for (int ch = 0; ch < channelCount; ch++) {
                short sample = in.getShort();
                double x = sample / 32768.0;
                double y = x;
                for (BiquadFilter band : filtersByChannel[ch]) {
                    y = band.process(y);
                }
                // Clamp: boosted bands can push a sample past full scale, which would otherwise
                // wrap around (very audible digital distortion) rather than just clip cleanly.
                double clamped = Math.max(-1.0, Math.min(1.0, y));
                outputBuffer.putShort((short) Math.round(clamped * 32767.0));
            }
        }

        inputBuffer.position(inputBuffer.position() + usableBytes);
        outputBuffer.flip();
    }

    private ByteBuffer allocateOutputBuffer(int requiredBytes) {
        return ByteBuffer.allocateDirect(requiredBytes).order(ByteOrder.nativeOrder());
    }

    @Override
    public ByteBuffer getOutput() {
        ByteBuffer result = outputBuffer;
        outputBuffer = EMPTY_BUFFER;
        return result;
    }

    @Override
    public void queueEndOfStream() {
        inputEnded = true;
    }

    @Override
    public boolean isEnded() {
        return inputEnded && outputBuffer == EMPTY_BUFFER;
    }

    @Override
    public void flush() {
        outputBuffer = EMPTY_BUFFER;
        inputEnded = false;
        // Reset filter history (not coefficients) to avoid an audible click from stale state
        // after a seek.
        for (BiquadFilter[] channelFilters : filtersByChannel) {
            for (BiquadFilter filter : channelFilters) {
                filter.reset();
            }
        }
    }

    @Override
    public void reset() {
        flush();
        filtersByChannel = new BiquadFilter[0][];
        inputAudioFormat = AudioFormat.NOT_SET;
        outputAudioFormat = AudioFormat.NOT_SET;
    }
}
