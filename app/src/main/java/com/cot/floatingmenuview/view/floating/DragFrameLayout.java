package com.cot.floatingmenuview.view.floating;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.widget.ViewDragHelper;

import java.util.ArrayList;

/**
 * Created by hq on 2017/10/10.
 */
public class DragFrameLayout extends FrameLayout {
    String TAG = "DragFrameLayout";
    ViewDragHelper dragHelper;
    ArrayList<View> viewList;

    public DragFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public DragFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //第二步：创建存放View的集合
        viewList = new ArrayList<>();
        dragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            /**
             * 是否捕获childView:
             * 如果viewList包含child，那么捕获childView
             * 如果不包含child，就不捕获childView
             */
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return viewList.contains(child);
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
            }

            /**
             * 当捕获到child后的处理：
             * 获取child的监听
             */
            @Override
            public void onViewCaptured(View capturedChild, int activePointerId) {
                super.onViewCaptured(capturedChild, activePointerId);
                if (onDragDropListener != null) {
                    onDragDropListener.onDragDrop(true);
                }
            }

            /**
             * 当释放child后的处理：
             * 取消监听，不再处理
             */
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                if (onDragDropListener != null) {
                    onDragDropListener.onDragDrop(false);
                }
            }

            /**
             * 到左边界的距离
             */
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return left;
            }

            /**
             * 到上边界的距离
             */
            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return top;
            }
        });
    }

    /**
     * 把要实现拖动的子view添加进来
     *
     * @param view
     */
    public void addDragChildView(View view) {
        viewList.add(view);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    //当手指抬起或事件取消的时候 就不拦截事件
        int actionMasked = ev.getActionMasked();
        if (actionMasked == MotionEvent.ACTION_CANCEL || actionMasked == MotionEvent.ACTION_UP) {
            return false;
        }
        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        return true;
    }

    public interface OnDragDropListener {
        void onDragDrop(boolean captured);
    }

    private OnDragDropListener onDragDropListener;

    public void setOnDragDropListener(OnDragDropListener onDragDropListener) {
        this.onDragDropListener = onDragDropListener;
    }
}
