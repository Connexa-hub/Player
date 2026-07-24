package com.brouken.player.core.playback;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

/**
 * Builds a {@link MediaItem} from already-resolved {@link MediaItemParams}, extracted
 * (behaviorally intact) from the inline {@code MediaItem.Builder} block in
 * {@code PlayerActivity#initializePlayer()}.
 */
public final class MediaItemFactory {

    private MediaItemFactory() {}

    @NonNull
    public static MediaItem create(@NonNull MediaItemParams params) {
        MediaItem.Builder builder = new MediaItem.Builder()
                .setUri(params.uri)
                .setMimeType(params.mimeType);

        if (params.title != null) {
            final MediaMetadata mediaMetadata = new MediaMetadata.Builder()
                    .setTitle(params.title)
                    .setDisplayTitle(params.title)
                    .build();
            builder.setMediaMetadata(mediaMetadata);
        }

        if (!params.subtitleConfigurations.isEmpty()) {
            builder.setSubtitleConfigurations(params.subtitleConfigurations);
        }

        return builder.build();
    }
}
