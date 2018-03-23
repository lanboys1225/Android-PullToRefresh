package com.handmark.pulltorefresh.samples.loadinglayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;
import com.handmark.pulltorefresh.samples.R;

/**
 * Created by 蓝兵 on 2018/3/23.
 */

public class BaoGaoLoadingLayout extends LoadingLayoutCopy {

    static final int ROTATION_ANIMATION_DURATION = 1200;

    private final Animation mRotateAnimation;

    private boolean mRotateDrawableWhilePulling;

    CircleProgressDrawable circleProgressDrawable;
    private int max_progress = 95;

    public BaoGaoLoadingLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs) {
        super(context, mode, scrollDirection, attrs);

        if (attrs != null) {
            mRotateDrawableWhilePulling = attrs.getBoolean(R.styleable.PullToRefresh_ptrRotateDrawableWhilePulling, true);
        }

        mRotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
        circleProgressDrawable = new CircleProgressDrawable(getResources().getColor(R.color.loading_color));
        mHeaderImage.setImageDrawable(circleProgressDrawable);
    }

    public void onLoadingDrawableSet(Drawable imageDrawable) {
        //mHeaderImage.setImageDrawable(imageDrawable);
    }

    protected void onPullImpl(float scaleOfLayout) {
        float angle;
        if (mRotateDrawableWhilePulling) {
            angle = scaleOfLayout * 90f;
        } else {
            angle = Math.max(0f, Math.min(180f, scaleOfLayout * 360f - 180f));
        }
        int progress = (int) angle * 180 / 360;
        if (progress >= max_progress) {
            progress = max_progress;
        }
        circleProgressDrawable.setProgress(progress);
    }

    @Override
    protected void refreshingImpl() {
        circleProgressDrawable.setProgress(max_progress);
        mHeaderImage.startAnimation(mRotateAnimation);
    }

    @Override
    protected void resetImpl() {
        mHeaderImage.clearAnimation();
    }

    @Override
    protected void pullToRefreshImpl() {
        // NO-OP
    }

    @Override
    protected void releaseToRefreshImpl() {
        // NO-OP
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {

    }

    @Override
    protected int getDefaultDrawableResId() {
        return R.drawable.default_ptr_rotate;
    }
}

