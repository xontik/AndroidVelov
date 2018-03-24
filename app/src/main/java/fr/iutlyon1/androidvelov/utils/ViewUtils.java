package fr.iutlyon1.androidvelov.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import fr.iutlyon1.androidvelov.R;

public final class ViewUtils {
    private ViewUtils() {}

    public static void animateVisibility(View view, boolean visible) {
        if ((view.getVisibility() == View.VISIBLE) == visible) {
            return;
        }

        if (visible) {
            Animation fadeIn = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_in);
            view.startAnimation(fadeIn);
            view.setVisibility(View.VISIBLE);
        } else {
            Animation fadeOut = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_out);
            view.startAnimation(fadeOut);
            view.setVisibility(View.INVISIBLE);
        }
    }
}
