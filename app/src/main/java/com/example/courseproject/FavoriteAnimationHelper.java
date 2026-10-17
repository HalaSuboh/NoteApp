package com.example.courseproject;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

class FavoriteAnimationHelper {

    private FavoriteAnimationHelper() {
    }

    public static void pulse(View heartView) {
        Animation scaleAnimation = AnimationUtils.loadAnimation(heartView.getContext(), R.anim.heartscale);
        heartView.startAnimation(scaleAnimation);
    }
}