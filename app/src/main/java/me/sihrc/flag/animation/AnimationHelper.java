package me.sihrc.flag.animation;

import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * Created by Chris on 5/25/15.
 */
public class AnimationHelper {
    static AnimationHelper instance;
    Context context;

    public static AnimationHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AnimationHelper(context);
        }

        return instance;
    }

    public AnimationHelper(Context context) {
        this.context = context;
    }
    public static void linearAnimation(final View view, final int deltaX, final int deltaY, long duration, final Runnable callback) {
        linearAnimation(view, 0, 0, deltaX, deltaY, duration, callback);
    }
    public static void linearAnimation(final View view, final int startX, final int startY, final int endX, final int endY, long duration, final Runnable callback) {
        Animation anim = new TranslateAnimation(startX, endX, startY, endY);
        anim.setDuration(duration);
        anim.setInterpolator(new DecelerateInterpolator());
        view.startAnimation(anim);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                view.setX(view.getX() + endX);
                view.setY(view.getY() + endY);

                if (callback != null)
                    callback.run();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    public static void fadeAnimation(final View view, final float fromAlpha, final float toAlpha, long duration, final Runnable callback) {
        Animation anim = new AlphaAnimation(fromAlpha, toAlpha);
        anim.setDuration(duration);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setAlpha(toAlpha);
                if (toAlpha > 0)
                    view.setVisibility(View.VISIBLE);
                else
                    view.setVisibility(View.GONE);

                if (callback != null)
                    callback.run();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        view.setVisibility(View.VISIBLE);
        view.startAnimation(anim);
    }
}
