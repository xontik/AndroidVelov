package fr.iutlyon1.androidvelov.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import fr.iutlyon1.androidvelov.R;

public final class AnimUtils {
    private AnimUtils() {}

    public static void slideIn(View view) {
        if (view.getVisibility() == View.VISIBLE)
            return;

        Animation slideIn = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_in_bot);
        view.startAnimation(slideIn);
        view.setVisibility(View.VISIBLE);
    }

    public static void slideOut(View view) {
        if (view.getVisibility() == View.GONE)
            return;

        Animation slideOut = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_out_bot);
        view.startAnimation(slideOut);
        view.setVisibility(View.GONE);
    }
}
