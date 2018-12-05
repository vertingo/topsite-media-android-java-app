package com.vertin_go.topsiteapp.ui;

/**
 * Created by VertinGo on 13/09/2017.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * A view which flips from one ImageView to another view using a 3D flip animation.
 */
public class FlippingView extends FrameLayout {

    private final ImageView flipOutView;
    private final ImageView flipInView;
    private final AnimatorSet animations;
    private final Listener listener;

    /**
     * Defines an interface to enable listening to flip events.
     */
    public interface Listener {

        /**
         * Called when the FlippingView has completed a flip.
         *
         * @param view The FlippingView which has completed the flip.
         */
        void onFlipped(FlippingView view);

    }

    /**
     * Create a flipping view which performs a 3D flip animation from one view to another.
     *
     * @param context The context associated with this View.
     * @param listener A listener which is informed of when the view has completed flipping.
     * @param width The view's width.
     * @param height The view's height.
     */
    public FlippingView(Context context, Listener listener, int width, int height) {
        super(context);

        this.listener = listener;
        this.flipOutView = new ImageView(context);
        this.flipInView = new ImageView(context);

        addView(flipOutView, width, height);
        addView(flipInView, width, height);

        flipInView.setRotationY(-90);

        ObjectAnimator flipOutAnimator = ObjectAnimator.ofFloat(flipOutView, "rotationY", 0, 90);
        flipOutAnimator.setInterpolator(new AccelerateInterpolator());
        Animator flipInAnimator = ObjectAnimator.ofFloat(flipInView, "rotationY", -90, 0);
        flipInAnimator.setInterpolator(new DecelerateInterpolator());
        animations = new AnimatorSet();
        animations.playSequentially(flipOutAnimator, flipInAnimator);
        animations.addListener(new AnimationListener());
    }

    public void setFlipInDrawable(Drawable drawable) {
        flipInView.setImageDrawable(drawable);
    }

    public void setFlipOutDrawable(Drawable drawable) {
        flipOutView.setImageDrawable(drawable);
    }

    public void setFlipDuration(int flipDuration) {
        animations.setDuration(flipDuration);
    }

    public void flip() {
        animations.start();
    }

    /**
     * Listens to the end of the flip animation to signal to listeners that the flip is complete
     */
    public final class AnimationListener extends AnimatorListenerAdapter {

        @Override
        public void onAnimationEnd(Animator animation) {
            flipOutView.setRotationY(0);
            flipInView.setRotationY(-90);
            listener.onFlipped(FlippingView.this);
        }
    }

}

