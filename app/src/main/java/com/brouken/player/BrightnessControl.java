package com.brouken.player;

import android.app.Activity;
import android.view.WindowManager;

import com.brouken.player.core.gestures.BrightnessCurve;

class BrightnessControl {

    private final Activity activity;

    public int currentBrightnessLevel = -1;

    public BrightnessControl(Activity activity) {
        this.activity = activity;
    }

    public float getScreenBrightness() {
        return activity.getWindow().getAttributes().screenBrightness;
    }

    public void setScreenBrightness(final float brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = brightness;
        activity.getWindow().setAttributes(lp);
    }

    public void changeBrightness(final CustomPlayerView playerView, final boolean increase, final boolean canSetAuto) {
        currentBrightnessLevel = BrightnessCurve.nextLevel(currentBrightnessLevel, increase, canSetAuto);

        if (currentBrightnessLevel == BrightnessCurve.LEVEL_AUTO && canSetAuto)
            setScreenBrightness(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE);
        else if (currentBrightnessLevel != BrightnessCurve.LEVEL_AUTO)
            setScreenBrightness(levelToBrightness(currentBrightnessLevel));

        playerView.setHighlight(false);

        if (currentBrightnessLevel == BrightnessCurve.LEVEL_AUTO && canSetAuto) {
            playerView.setIconBrightnessAuto();
            playerView.setCustomErrorMessage("");
        } else {
            playerView.setIconBrightness();
            playerView.setCustomErrorMessage(" " + currentBrightnessLevel);
        }
    }

    float levelToBrightness(final int level) {
        return BrightnessCurve.levelToBrightness(level);
    }
}

