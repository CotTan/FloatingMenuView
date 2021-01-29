package com.cot.floatingmenuview.view.floating;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.customview.widget.ViewDragHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ConvertUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cot.floatingmenuview.R;
import com.cot.floatingmenuview.utils.GeneralUtils;
import com.cot.floatingmenuview.view.RecycleViewGridDivider;
import com.cot.floatingmenuview.view.floating.adapter.FloatingMenuAdapter;
import com.cot.floatingmenuview.view.floating.bean.CheckedBean;
import com.cot.floatingmenuview.view.floating.bean.FloatingMenuBean;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.valueOf;

/**
 * @author COT
 * @version 1.0
 * @since 2021-01-18
 * //todo 拖拽功能暂不实现，xml配置 也暂不提供,且目前只考虑垂直方向的，
 * 目前不限制按钮组的个数，但是建议数量在10以下
 */
public class FloatingMenuView extends FrameLayout /*implements View.OnTouchListener*/ {
    private String TAG = "FloatingMenuView";
    private Context mContext;
    private View views;
    private ConstraintLayout mLayout;
    private RecyclerView mRecyclerView;
    private ImageView mIvFloating;

    private List<FloatingMenuBean> floatingMenuList;
    private FloatingMenuAdapter floatingAdapter;
    private List<String> labelList;//用来存按钮组中的文字

    private boolean isLongPress = false;//是否长按 悬浮按钮
    private int startX, startY;//控件长按的位置
    private int marginBottom = 0;
    private GestureDetector mGestureDetector;

    protected int screenWidth;
    protected int screenHeight;
    protected int lastX;
    protected int lastY;
    boolean mNeedLayout = true;

    private ViewDragHelper viewDragHelper;

    private SharedPreferences.Editor editor;

    //悬浮按钮
    private OnClickListener onClickListener;
    private OnLongClickListener onLongClickListener;
    //按钮组
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    private int orientation = 1;//方向 1 、垂直方向 2、水平方向 3、发散（但未实现）

    public FloatingMenuView(@NonNull Context context) {
        this(context, null);
    }

    public FloatingMenuView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingMenuView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        initView();
        initScreenInfo();
//        setOnTouchListener(this);
    }

    /**
     * 初始化获取屏幕宽高
     */
    protected void initScreenInfo() {
        screenHeight = getResources().getDisplayMetrics().heightPixels - 40;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    private void initView() {
        labelList = new ArrayList<>();

        views = LayoutInflater.from(mContext).inflate(R.layout.floating_menu_view, this);

        mLayout = views.findViewById(R.id.cl_floating_menu);
        mRecyclerView = views.findViewById(R.id.rv_floating_menu_view);
        mIvFloating = views.findViewById(R.id.iv_floating_menu_floating);

        mGestureDetector = new GestureDetector(mContext, onGestureListener);
        mGestureDetector.setOnDoubleTapListener(onDoubleTapListener);
        //解决长按屏幕无法拖动,但是会造成无法识别长按事件
//        mGestureDetector.setIsLongpressEnabled(false);

//        mIvFloating.setOnClickListener(v -> {
//            if (onClickListener != null) onClickListener.onClick();
//            setRotation(false);
//        });
//
//        mIvFloating.setOnLongClickListener(view -> {
//            if (onLongClickListener != null) {
//                return onLongClickListener.onLongClick(view);
//            }
////            Vibrator vib = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);   //获取系统震动服务
////            vib.vibrate(70);   //震动70毫秒
//
////
////            switch (event.getAction()) {
////                case MotionEvent.ACTION_DOWN:
////                    //手指按下
////                    //获取第一次接触屏幕
////                    startX = (int) event.getX();
////                    startY = (int) event.getY();
////                    break;
////                case MotionEvent.ACTION_MOVE:
////                    //手指拖动
////                    //获取距离差
////                    int dx = (int) event.getX() - startX;
////                    int dy = (int) event.getY() - startY;
////                    //更改Imageview在窗体的位置
//////
//////                            v.layout(v.getLeft() + dx, v.getTop() + dy,
//////                                    v.getRight() + dx, v.getBottom() + dy);
////
////                    LayoutParams lp =
////                            (LayoutParams) mLayout.getLayoutParams();
////
////                    marginBottom = lp.bottomMargin;
////
////                    lp.bottomMargin = marginBottom + dy;
////
////                    mLayout.setLayoutParams(lp);
////                    //获取移动后的位置
////                    startX = (int) event.getX();
////                    startY = (int) event.getY();
////                    break;
////                case MotionEvent.ACTION_UP:
////                    //手指弹起
////                    startX = (int) event.getX();
////                    startY = (int) event.getY();
////                    break;
////            }
//
////            view.startDrag(null, new View.DragShadowBuilder(view), null, 0);
//            return false;
//        });
//
//        mIvFloating.setOnDragListener((View v, DragEvent event) -> {
//            Vibrator vib = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);   //获取系统震动服务
//            vib.vibrate(70);   //震动70毫秒
//
//            switch (event.getAction()) {
//                case DragEvent.ACTION_DRAG_STARTED:
//                    //Toast.makeText(TargetItemSelectedTestActivity.this, "开始拖动", Toast.LENGTH_LONG).show();
//
//                    //手指按下
//                    //获取第一次接触屏幕
//                    startX = (int) event.getX();
//                    startY = (int) event.getY();
//                    break;
//                case DragEvent.ACTION_DRAG_ENTERED:
//                    //Toast.makeText(TargetItemSelectedTestActivity.this, "进入目标区域", Toast.LENGTH_LONG).show();
//                    //这里可以改变目标区域的背景色
//                    startY = (int) event.getY();
//                    Log.e("s","Y: " + startY);
//                    break;
//                case DragEvent.ACTION_DRAG_EXITED:
//                    startY = (int) event.getY();
//                    Log.e("ss","Y: " + startY);
//                    //Toast.makeText(TargetItemSelectedTestActivity.this, "离开目标区域", Toast.LENGTH_LONG).show();
//                    break;
//
//                case DragEvent.ACTION_DROP:
//                    //手指弹起
//                    startX = (int) event.getX();
//                    startY = (int) event.getY();
//                    LogUtils.e("s","Y: " + startY);
//
//                    Log.e("sss","Y: " + startY);
//                    //Toast.makeText(TargetItemSelectedTestActivity.this, "放手", Toast.LENGTH_LONG).show();
//                    LayoutParams lp =
//                            (LayoutParams) mLayout.getLayoutParams();
//                    lp.bottomMargin = startY;
//
//                    mLayout.setLayoutParams(lp);
//
////
////
//
//        // 如果手指放在imageView上拖动
//        mIvFloating.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                // event.getRawX(); //获取手指第一次接触屏幕在x方向的坐标
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:// 获取手指第一次接触屏幕
//                        startX = (int) event.getRawX();
//                        startY = (int) event.getRawY();
//                        break;
//                    case MotionEvent.ACTION_MOVE:// 手指在屏幕上移动对应的事件
//                        int x = (int) event.getRawX();
//                        int y = (int) event.getRawY();
//                        // 获取手指移动的距离
//                        int dx = x - startX;
//                        int dy = y - startY;
//                        // 得到imageView最开始的各顶点的坐标
//                        int l = mIvFloating.getLeft();
//                        int r = mIvFloating.getRight();
//                        int t = mIvFloating.getTop();
//                        int b = mIvFloating.getBottom();
//                        // 更改imageView在窗体的位置
////                        mIvFloating.layout(0, t + dy, 0, b + dy);
//
//                        LayoutParams lp =
//                                (LayoutParams) mLayout.getLayoutParams();
//                        lp.bottomMargin = b;
//
//                        mLayout.setLayoutParams(lp);
//
//                        // 获取移动后的位置
//                        startX = (int) event.getRawX();
//                        startY = (int) event.getRawY();
//                        break;
//                    case MotionEvent.ACTION_UP:// 手指离开屏幕对应事件
//                        // 记录最后图片在窗体的位置
//                        int lasty = mIvFloating.getTop();
//                        int lastx = mIvFloating.getLeft();
////                        SharedPreferences.Editor editor = sp.edit();
////                        editor.putInt("lasty", lasty);
////                        editor.putInt("lastx", lastx);
////                        editor.commit();
//                        break;
//                }
//                return true;
//            }
//        });
//

        floatingAdapter = new FloatingMenuAdapter(floatingMenuList);

        mRecyclerView.setAdapter(floatingAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new RecycleViewGridDivider(1, 0,
                true, RecycleViewGridDivider.LINEAR));

        //点击事件
        floatingAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (onItemClickListener != null)
                onItemClickListener.onItemClick(adapter, view, position);
        });

        floatingAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            if (onItemLongClickListener != null)
                return onItemLongClickListener.onItemLongClick(adapter, view, position);
            return false;
        });

        mLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();// 获取触摸事件触摸位置的原始X坐标
                        lastY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        int l = v.getLeft() + dx;
                        int b = v.getBottom() + dy;
                        int r = v.getRight() + dx;
                        int t = v.getTop() + dy;
                        // 下面判断移动是否超出屏幕
                        if (l < 0) {
                            l = 0;
                            r = l + v.getWidth();
                        }
                        if (t < 0) {
                            t = 0;
                            b = t + v.getHeight();
                        }
                        if (r > screenWidth) {
                            r = screenWidth;
                            l = r - v.getWidth();
                        }
                        //获取虚拟按钮高度
                        int navigationBarHeight = GeneralUtils.getNavigationBarHeightIfRoom(mContext);

                        if (b > screenHeight - navigationBarHeight) {
                            b = screenHeight - navigationBarHeight;
                            t = b - v.getHeight();
                        }
                        v.layout(l, t, r, b);
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        v.postInvalidate();
                        mNeedLayout = true;
                        Log.e(TAG, "ACTION_MOVE");
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
//        // 接管onTouchEvent
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    /**
     * 设置数据源 或者通过add的方式也没问题
     *
     * @param floatingList 数据源
     */
    public FloatingMenuView setDate(List<FloatingMenuBean> floatingList) {
        if (floatingList == null) return this;
        if (floatingMenuList == null) floatingMenuList = new ArrayList<>();

        for (FloatingMenuBean bean : floatingList) {
            labelList.add(bean.getLabelName());
            //纯数字不换行
            if (!isNumeric(bean.getLabelName()) && orientation == 1)
                bean.setLabelName(getReplaceWrap(bean.getLabelName(), ""));
            floatingMenuList.add(bean);
        }

        if (floatingAdapter != null)
            floatingAdapter.setList(floatingMenuList);

        return this;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
//
//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        int cWidth = 0;
//        int cHeight = 0;
//        MarginLayoutParams params;
//        for (int i = 0; i < getChildCount(); i++) {
//            View childView = getChildAt(i);
//            cWidth = childView.getMeasuredWidth();
//            cHeight = childView.getMeasuredHeight();
//            params = (MarginLayoutParams) childView.getLayoutParams();
//
//            int cl = 0;
//            int ct = 0;
//            int cr = 0;
//            int cb = 0;
//            switch (i) {
//                case 0:
//                    cl = params.leftMargin;
//                    ct = params.topMargin;
//                    break;
//                case 1:
//                    cl = getWidth() - cWidth - params.leftMargin - params.rightMargin;
//                    ct = params.topMargin;
//                    break;
//                case 2:
//                    cl = params.leftMargin;
//                    ct = getHeight() - cHeight - params.topMargin - params.bottomMargin;
//                    break;
//                case 3:
//                    cl = getWidth() - cWidth - params.leftMargin - params.rightMargin;
//                    ct = getHeight() - cHeight - params.topMargin - params.bottomMargin;
//                    break;
//            }
//
//            cr = cl + cWidth;
//            cb = ct + cHeight;
//
//            childView.layout(cl, ct, cr, cb);
//        }
////        super.onLayout(changed, left, top, right, bottom);
//        Log.i(TAG, "onLayout: 更新UI");
//    }

//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        Log.e(TAG, "onLayout");
//        if (!mNeedLayout) return;
//        super.onLayout(changed, l, t, r, b);
//    }

    //todo  有效，但是 点击会回到原来的位置
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                lastX = (int) event.getRawX();// 获取触摸事件触摸位置的原始X坐标
//                lastY = (int) event.getRawY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                int dx = (int) event.getRawX() - lastX;
//                int dy = (int) event.getRawY() - lastY;
//                int l = v.getLeft() + dx;
//                int b = v.getBottom() + dy;
//                int r = v.getRight() + dx;
//                int t = v.getTop() + dy;
//                // 下面判断移动是否超出屏幕
//                if (l < 0) {
//                    l = 0;
//                    r = l + v.getWidth();
//                }
//                if (t < 0) {
//                    t = 0;
//                    b = t + v.getHeight();
//                }
//                if (r > screenWidth) {
//                    r = screenWidth;
//                    l = r - v.getWidth();
//                }
//                //获取虚拟按钮高度
//                int navigationBarHeight = GeneralUtils.getNavigationBarHeightIfRoom(mContext);
//
//                if (b > screenHeight - navigationBarHeight) {
//                    b = screenHeight - navigationBarHeight;
//                    t = b - v.getHeight();
//                }
//                v.layout(l, t, r, b);
//                lastX = (int) event.getRawX();
//                lastY = (int) event.getRawY();
//                v.postInvalidate();
//                mNeedLayout = true;
//                Log.e(TAG, "ACTION_MOVE");
//                break;
//            case MotionEvent.ACTION_UP:
//                break;
//        }
////        // 接管onTouchEvent
//        return mGestureDetector.onTouchEvent(event);
//    }

    GestureDetector.OnGestureListener onGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent event) {
            Log.i(TAG, "onDown: 按下");
            return true;
        }

        @Override
        public void onShowPress(MotionEvent event) {
            Log.i(TAG, "onShowPress: 刚碰上还没松开");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            Log.i(TAG, "onSingleTapUp: 轻轻一碰后马上松开");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent event, MotionEvent e, float distanceX, float distanceY) {
            Log.i(TAG, "onScroll: 按下后拖动");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            Log.i(TAG, "onLongPress: 长按屏幕");
            Vibrator vib = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);   //获取系统震动服务
            vib.vibrate(70);   //震动70毫秒
        }

        @Override
        public boolean onFling(MotionEvent event, MotionEvent e, float velocityX, float velocityY) {
            Log.i(TAG, "onFling: 滑动后松开");
            return true;
        }
    };

    GestureDetector.OnDoubleTapListener onDoubleTapListener = new GestureDetector.OnDoubleTapListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            mNeedLayout = false;
            Log.i(TAG, "onSingleTapConfirmed: 严格的单击");
            if (onClickListener != null) onClickListener.onClick();
            setRotation(false);
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            Log.i(TAG, "onDoubleTap: 双击");
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.i(TAG, "onDoubleTapEvent: 表示发生双击行为");
            return true;
        }
    };

    /**
     * 判断字符串是否为空
     *
     * @param s 要判断的字符串
     * @return true:s为null或者为""; false:s有字符
     */
    private boolean isEmpty(String s) {
        return s == null || s.length() <= 0;
    }

    /**
     * 判断字符串是否是纯数字
     */
    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 过滤掉所有换行符，Tab等
     *
     * @param string 源字符串
     * @return 过滤后的字符串
     */
    public static String trimString(String string) {
        if (string != null) {
            return string.trim()
                    .replaceAll(" ", "")//去除字符串中所有的空格
                    .replaceAll("\r", "")//去除字符串中所有的回车
                    .replaceAll("\t", "")//去除字符串中所有的制表
                    .replaceAll("\n", "");//去除字符串中所有的换行
        } else {
            return "";
        }
    }

    /**
     * 通过指定字符标记，将字符串换行
     *
     * @param str    原字符串，需要换行的字符串
     * @param target 标记符号，用来换行
     *               如果 target 为"",则所有字符串竖向排列
     */
    public String getReplaceWrap(String str, String target) {
        if (!isEmpty(str)) {
            String s = trimString(str).replace(target, "\n");//将目标字符串替换成换行
            if (s.startsWith("\n")) s = s.substring(1);
            if (s.endsWith("\n")) s = s.substring(0, s.length() - 1);

            return s;
        }
        return "";
    }

    /**
     * 添加数据源
     *
     * @param icon res 的图片资源
     */
    public FloatingMenuView add(int icon) {
        return adds(new int[]{icon});
    }

    /**
     * 添加数据源
     *
     * @param icons res 的图片资源
     */
    public FloatingMenuView adds(int... icons) {
        if (floatingMenuList == null) floatingMenuList = new ArrayList<>();
        for (int icon : icons) {
            if (icon > 0) {
                labelList.add("");
                floatingMenuList.add(new FloatingMenuBean(FloatingMenuBean.VERTICAL_IMAGE, icon));
            }
        }
        if (floatingAdapter != null)
            floatingAdapter.setList(this.floatingMenuList);
        return this;
    }

    /**
     * 添加数据源
     *
     * @param label 按钮名称
     */
    public FloatingMenuView add(String label) {
        return adds(label);
    }

    /**
     * 添加数据源
     *
     * @param labels 按钮名称
     */
    public FloatingMenuView adds(String... labels) {
        if (floatingMenuList == null) floatingMenuList = new ArrayList<>();
        for (String label : labels) {
            if (!isEmpty(label)) {
                //纯数字不换行
                labelList.add(label);
                if (!isNumeric(label) && orientation == 1) label = getReplaceWrap(label, "");

                floatingMenuList.add(new FloatingMenuBean(FloatingMenuBean.VERTICAL_TEXT, label));
            }
        }
        if (floatingAdapter != null)
            floatingAdapter.setList(this.floatingMenuList);
        return this;
    }

    /**
     * 添加数据源
     *
     * @param icon   图标
     * @param labels 文字
     */
    public FloatingMenuView adds(int icon, String... labels) {
        return adds(new int[]{icon}).adds(labels);
    }

    /**
     * 设置按钮组 - 背景色
     */
    public FloatingMenuView setMenuBackground(int resId) {
        if (resId > 0) mRecyclerView.setBackgroundResource(resId);
        return this;
    }

    /**
     * 设置悬浮按钮 - 背景色
     */
    public FloatingMenuView setFloatBackground(int resId) {
        if (resId > 0) mIvFloating.setBackgroundResource(resId);
        return this;
    }

    /**
     * 设置悬浮按钮 - icon
     * 默认 “+”图片
     */
    public FloatingMenuView setFloatIcon(int resId) {
        if (resId > 0) mIvFloating.setImageResource(resId);
        return this;
    }

    /**
     * 设置离边距
     *
     * @param left   默认值 10dp;
     * @param top    默认值 10dp;
     * @param right  默认值 10dp;
     * @param bottom 默认值 100dp;
     */
    public FloatingMenuView setMargin(int left, int top, int right, int bottom) {
        //-6 是因为image占了6dp
        if (left >= 6) left -= 6;
        if (top >= 6) top -= 6;
        if (right >= 6) right -= 6;
        if (bottom >= 6) bottom -= 6;

        LayoutParams lp =
                (LayoutParams) mLayout.getLayoutParams();

        lp.leftMargin = ConvertUtils.dp2px(left);
        lp.topMargin = ConvertUtils.dp2px(top);
        lp.rightMargin = ConvertUtils.dp2px(right);
        lp.bottomMargin = ConvertUtils.dp2px(bottom);

        marginBottom = lp.bottomMargin;

        mLayout.setLayoutParams(lp);
        return this;
    }

    /**
     * 设置最小高度
     *
     * @param minHeight 最小高度 单位 dp
     */
    public FloatingMenuView setMinHeight(int minHeight) {
        mLayout.setMinimumHeight(Math.max(minHeight, 0));
        return this;
    }

    /**
     * 设置高度  默认自适应
     *
     * @param maxHeight 高度 单位 dp
     */
    public FloatingMenuView setMaxHeight(int maxHeight) {
        ConstraintLayout.LayoutParams lp =
                (ConstraintLayout.LayoutParams) mRecyclerView.getLayoutParams();
        lp.height = ConvertUtils.dp2px(Math.max(maxHeight, 0));
        mRecyclerView.setLayoutParams(lp);
        return this;
    }

    /**
     * 获取方向
     */
    public int getOrientation() {
        return orientation;
    }

    /**
     * 设置方向
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    /**
     * 悬浮按钮 旋转
     *
     * @param isHide true 必隐藏
     *               false 如果隐藏则显示，如果显示则隐藏
     */
    public FloatingMenuView setRotation(boolean isHide) {
        if (isHide || mRecyclerView.getVisibility() == View.VISIBLE) {
            mRecyclerView.setVisibility(View.GONE);
            mIvFloating.setRotation(0);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mIvFloating.setRotation(45);
        }
        return this;
    }

    /**
     * 必须绑定list，而且 bean 要继承 BeanChecked
     * 全选等操作
     * 调用完此方法需要手动调用 notifyDataSetChanged();
     *
     * @param check 1 全选
     *              2 取消全选
     *              3 反选
     */
    public FloatingMenuView setCheck(int check, List<? extends CheckedBean> list) {
        if (list != null) {
            for (CheckedBean bean : list) {
                switch (check) {
                    case 1://全选
                        bean.setChecked(true);
                        break;
                    case 2://取消全选
                        bean.setChecked(false);
                        break;
                    case 3://反选
                        bean.setChecked(!bean.isChecked());
                        break;
                }
            }
        }
        return this;
    }

    /**
     * 获取按钮组中的文字
     *
     * @param position 按钮组中所在的位置
     */
    public String getPositionName(int position) {
        if (labelList != null)
            return labelList.get(position);

        return "";
    }

    /**
     * 默认显示 FloatingMenuView
     *
     * @param isHide true 隐藏
     */
    public FloatingMenuView setHideFloating(boolean isHide) {
        if (isHide) {
            views.setVisibility(GONE);
        } else {
            views.setVisibility(VISIBLE);
        }
        return this;
    }

    /**
     * 更换按钮组中图片
     *
     * @param resId    资源id
     * @param position 需要更换的位置
     */
    public FloatingMenuView setIcon(int resId, int position) {
        if (floatingMenuList != null && position < floatingMenuList.size()) {
            floatingMenuList.get(position).setIcon(resId);
            floatingAdapter.notifyDataSetChanged();
        }
        return this;
    }

    /**
     * 更换按钮组文字
     *
     * @param resId    资源id
     * @param position 需要更换的位置
     */
    public FloatingMenuView setText(int resId, int position) {
        if (floatingMenuList != null && position < floatingMenuList.size()) {
            String str = mContext.getResources().getString(resId);
            labelList.set(position, str);
            if (!isNumeric(mContext.getResources().getString(resId)) && orientation == 1)
                str = getReplaceWrap(str, "");
            floatingMenuList.get(position).setLabelName(str);
            floatingAdapter.notifyDataSetChanged();
        }
        return this;
    }

    /**
     * 更换按钮组文字
     *
     * @param position 需要更换的位置
     */
    public FloatingMenuView setText(String str, int position) {
        if (floatingMenuList != null && position < floatingMenuList.size()) {
            labelList.set(position, str);
            if (!isNumeric(str) && orientation == 1) str = getReplaceWrap(str, "");
            floatingMenuList.get(position).setLabelName(str);
            floatingAdapter.notifyDataSetChanged();
        }
        return this;
    }

    /**
     * 更换按钮组中 指定位置文字的颜色
     *
     * @param position 需要更换的位置
     * @param color    color 资源id，默认白色
     */
    public FloatingMenuView setTextColor(int position, @ColorInt int color) {
        if (floatingMenuList != null && position < floatingMenuList.size()) {
            floatingMenuList.get(position).setTextColor(color);
            floatingAdapter.notifyDataSetChanged();
        }
        return this;
    }

    /**
     * 更换按钮组中 指定位置文字的大小
     *
     * @param position 需要更换的位置
     * @param size     字体大小 默认14sp 单位 TypedValue.COMPLEX_UNIT_SP
     *                 单位 see{@link android.util.TypedValue} for the possible dimension units.
     */
    public FloatingMenuView setTextSize(int position, float size) {
        if (floatingMenuList != null && position < floatingMenuList.size()) {
            floatingMenuList.get(position).setTextSize(size);
            floatingAdapter.notifyDataSetChanged();
        }
        return this;
    }

    /**
     * 设置 分割线
     *
     * @param hasDividingLine 默认 false 不需要
     *                        true 需要
     */
    public FloatingMenuView setHasDividingLine(boolean hasDividingLine) {
        if (floatingMenuList != null) {
            for (FloatingMenuBean bean : floatingMenuList) {
                bean.setHasLine(hasDividingLine);
            }
            floatingAdapter.notifyDataSetChanged();
        }
        return this;
    }

    /**
     * 设置 分割线颜色
     *
     * @param resColor color 资源
     */
    public FloatingMenuView setDividingLineColor(int resColor) {
        if (floatingMenuList != null) {
            for (FloatingMenuBean bean : floatingMenuList) {
                bean.setLineColor(resColor);
            }
            floatingAdapter.notifyDataSetChanged();
        }
        return this;
    }

    /**
     * 设置 分割线高度
     *
     * @param height 高度 单位px
     */
    public FloatingMenuView setDividingLineHeight(int height) {
        if (floatingMenuList != null) {
            for (FloatingMenuBean bean : floatingMenuList) {
                bean.setLineHeight(height);
            }
            floatingAdapter.notifyDataSetChanged();
        }
        return this;
    }

    /**
     * 获取数据源
     */
    public List<FloatingMenuBean> getFloatingList() {
        if (floatingMenuList == null) floatingMenuList = new ArrayList<>();
        return floatingMenuList;
    }

    /**
     * 获取适配器
     */
    public FloatingMenuAdapter getFloatingAdapter() {
        return floatingAdapter;
    }

    /**
     * 设置监听 悬浮按钮 - 单击
     */
    public FloatingMenuView setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    /**
     * 设置监听 悬浮按钮 - 长按
     */
    public FloatingMenuView setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
        return this;
    }

    /**
     * 设置监听 按钮组 - 单击
     */
    public FloatingMenuView setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    /**
     * 设置监听 按钮组 - 长按
     */
    public FloatingMenuView setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
        return this;
    }

    /**
     * 悬浮按钮 - 单击 - 监听接口
     */
    public interface OnClickListener {
        void onClick();
    }

    /**
     * 悬浮按钮 - 长按 - 监听接口
     */
    //todo 长按拖拽暂移动未实现
    public interface OnLongClickListener {
        /**
         * @return true 消费事件 false 传递到单击
         */
        boolean onLongClick(View v);
    }

    /**
     * 按钮组 - 单击 - 监听接口
     */
    public interface OnItemClickListener {
        void onItemClick(BaseQuickAdapter<?, ?> adapter, View view, int position);
    }

    /**
     * 按钮组 - 长按 - 监听接口
     */
    public interface OnItemLongClickListener {
        /**
         * @return true 消费事件 false 传递到单击
         */
        boolean onItemLongClick(BaseQuickAdapter<?, ?> adapter, View view, int position);
    }

}
