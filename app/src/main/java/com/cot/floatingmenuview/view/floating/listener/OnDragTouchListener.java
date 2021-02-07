package com.cot.floatingmenuview.view.floating.listener;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.cot.floatingmenuview.utils.GeneralUtils;

/**
 * 拖拽监听
 * //todo 依附功能虽然可以使用，但是需要优化，移动距离过小会 过动上移
 * 以右下角为参考坐标，参考链接为左上角
 * 参考链接 @link｛https://blog.csdn.net/sl2018god/article/details/83109199｝
 */
public class OnDragTouchListener implements View.OnTouchListener {

    private int mScreenWidth, mScreenHeight;//屏幕宽高
    private float mOriginalX, mOriginalY;//手指按下时的初始位置
    private float mDistanceX, mDistanceY;//记录手指与view的右下角角的距离
    private int left, top, right, bottom;
    private int navHeight;//虚拟按钮高度
    private int titleHeight; //标题栏+状态栏

    private Context mContext;
    private boolean hasAutoPullToBorder;//是否开启自动依附边缘
    private boolean isDrag = false;//默认可拖拽
    private boolean isMoved = false;//是否移动过 默认不是(如果没此参数 第一次点击可能会有问题)

    private int sensitivity = 5;//移动灵敏度，超过该值则表示移动，默认为 5

    private OnDraggableClickListener mListener;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public OnDragTouchListener(Context context) {
        this(context, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public OnDragTouchListener(Context context, boolean isAutoPullToBorder) {
        this.mContext = context;
        hasAutoPullToBorder = isAutoPullToBorder;

        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        //虚拟按钮
        navHeight = GeneralUtils.getCurrentNavigationBarHeight((Activity) mContext);

        //标题栏+状态栏
        titleHeight = BarUtils.getNavBarHeight() + BarUtils.getStatusBarHeight();
    }

    @Override
    public boolean onTouch(final View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mScreenWidth = ScreenUtils.getScreenWidth();
                mScreenHeight = ScreenUtils.getScreenHeight();
                mOriginalX = event.getRawX();
                mOriginalY = event.getRawY();
                mDistanceX = event.getRawX() - v.getLeft();
                mDistanceY = event.getRawY() - v.getTop();

                if (mListener != null) {
                    isDrag = mListener.onStartDrag();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isDrag) {
                    return false;
                }
                isMoved = true;
                left = (int) (event.getRawX() - mDistanceX);
                top = (int) (event.getRawY() - mDistanceY);
                right = left + v.getWidth();
                bottom = top + v.getHeight();
                if (left < 0) {
                    left = 0;
                    right = left + v.getWidth();
                }

                if (top < titleHeight) {
                    top = titleHeight;
                    bottom = top + v.getHeight();
                }

                if (right > mScreenWidth) {
                    right = mScreenWidth;
                    left = right - v.getWidth();
                }

                if (bottom > mScreenHeight - navHeight) {
                    bottom = mScreenHeight - navHeight;
                    top = bottom - v.getHeight();
                }
                v.layout(left, top, right, bottom);
                break;
            case MotionEvent.ACTION_UP:
                if (isMoved && isDrag) {
                    //在拖动过按钮后，如果其他view刷新导致重绘，会让按钮重回原点，所以需要更改布局参数
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                    startAutoPull(v, lp);
                }
                //如果移动距离过小，则判定为点击 灵敏度可以设置
                if (!isDrag
                        || (Math.abs(event.getRawX() - mOriginalX) < sensitivity
                        && Math.abs(event.getRawY() - mOriginalY) < sensitivity)) {
                    if (mListener != null) mListener.onClick(v);
                }
                //消除警告
                v.performClick();
                break;
        }
        return true;
    }

    /**
     * 开启自动拖拽
     *
     * @param v  拉动控件
     * @param lp 控件布局参数
     */
    private void startAutoPull(final View v, final ViewGroup.MarginLayoutParams lp) {
        if (!hasAutoPullToBorder) {
            v.layout(left, top, right, bottom);
            lp.setMargins(0, 0, mScreenWidth - right, mScreenHeight - bottom - titleHeight - navHeight);
            v.setLayoutParams(lp);
            if (mListener != null) {
                mListener.onDragged(v, left, top);
            }
            return;
        }
        //当用户拖拽完后，让控件根据远近距离回到最近的边缘
        float end = 0;
        if ((left + v.getWidth() / 2) >= mScreenWidth / 2) {
            end = mScreenWidth - v.getWidth();
        }
        ValueAnimator animator = ValueAnimator.ofFloat(left, end);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            left = (int) ((float) animation.getAnimatedValue());
            right = left + v.getWidth();
            v.layout(left, top, right, bottom);
            lp.setMargins(0, 0, mScreenWidth - right, mScreenHeight - bottom - titleHeight - navHeight);
            v.setLayoutParams(lp);
        });
        final float finalEnd = end;
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null) {
                    mListener.onDragged(v, (int) finalEnd, top);
                }
            }
        });
        animator.setDuration(400);
        animator.start();
    }

    public boolean isHasAutoPullToBorder() {
        return hasAutoPullToBorder;
    }

    public OnDragTouchListener setHasAutoPullToBorder(boolean hasAutoPullToBorder) {
        this.hasAutoPullToBorder = hasAutoPullToBorder;
        return this;
    }

    public boolean isDrag() {
        return this.isDrag;
    }

    public OnDragTouchListener setDrag(boolean drag) {
        this.isDrag = drag;
        return this;
    }

    public int getSensitivity() {
        return sensitivity;
    }

    public OnDragTouchListener setSensitivity(int sensitivity) {
        this.sensitivity = sensitivity;
        return this;
    }

    public OnDraggableClickListener getOnDraggableClickListener() {
        return mListener;
    }

    public OnDragTouchListener setOnDraggableClickListener(OnDraggableClickListener listener) {
        mListener = listener;
        return this;
    }

    /**
     * 控件拖拽监听器
     */
    public interface OnDraggableClickListener {

        /**
         * 当控件拖拽完后回调
         *
         * @param v      拖拽控件
         * @param right  控件右边距
         * @param bottom 控件底边距
         */
        void onDragged(View v, int right, int bottom);

        /**
         * 当可拖拽控件被点击时回调
         *
         * @param v 拖拽控件
         */
        void onClick(View v);

        /**
         * 拖拽前判断 是否跟进某个值拖拽
         */
        boolean onStartDrag();
    }
}
