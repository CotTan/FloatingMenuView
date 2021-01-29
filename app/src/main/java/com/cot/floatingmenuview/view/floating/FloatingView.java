package com.cot.floatingmenuview.view.floating;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cot.floatingmenuview.R;
import com.cot.floatingmenuview.view.RecycleViewGridDivider;
import com.cot.floatingmenuview.view.floating.adapter.FloatingMenuAdapter;
import com.cot.floatingmenuview.view.floating.bean.CheckedBean;
import com.cot.floatingmenuview.view.floating.bean.FloatingMenuBean;
import com.cot.floatingmenuview.view.floating.listener.OnDragTouchListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author COT
 * @version 1.0
 * @since 2021-01-18
 * //todo 拖拽功能暂不实现，xml配置 也暂不提供,且目前只考虑垂直方向的，
 * 目前不限制按钮组的个数，但是建议数量在10以下
 *
 * 参考链接 @see{https://blog.csdn.net/sl2018god/article/details/83109199}
 * 参考链接 @link{https://blog.csdn.net/lmj623565791/article/details/46858663}
 * 参考链接 @see{https://blog.csdn.net/yanbober/article/details/50419059}
 * 参考链接 @see{https://blog.csdn.net/qq_17810899/article/details/96829458}
 * 参考链接 @see{https://blog.csdn.net/lovoo/article/details/51226884}
 * 参考链接 @see{https://cloud.tencent.com/developer/article/1726085}
 * 参考链接 @see{https://blog.csdn.net/yanbober/article/details/50419059}
 * 参考链接 @see{https://blog.csdn.net/lmj623565791/article/details/46858663}
 */
public class FloatingView extends FrameLayout {
    private String TAG = "FloatingMenuView";
    private Context mContext;
    private View views;
    private ConstraintLayout mLayout;
    private RecyclerView mRecyclerView;
    private ImageView mIvFloating;

    private List<FloatingMenuBean> floatingMenuList;
    private FloatingMenuAdapter floatingAdapter;
    private List<String> labelList;//用来存按钮组中的文字


    protected int screenWidth;
    protected int screenHeight;
    protected int lastX;
    protected int lastY;
    protected int mTouchStartX;
    protected int mTouchStartY;
    boolean mNeedLayout = true;
    private int mCurTop = 0;

    private boolean mIsOpen = true;

//    private ViewDragHelper mDragger;
//
//    private View mDragView;
//    private View mAutoBackView;
//    private View mEdgeTrackerView;

    private Point mAutoBackOriginPos = new Point();

    //悬浮按钮
    private OnClickListener onClickListener;
    private OnLongClickListener onLongClickListener;
    //按钮组
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    private int orientation = 1;//方向 1 、垂直方向 2、水平方向 3、发散（但未实现）
    private boolean isClick = false;

    public FloatingView(@NonNull Context context) {
        this(context, null);
    }

    public FloatingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        initView();
        initScreenInfo();
    }

    /**
     * 初始化获取屏幕宽高
     */
    protected void initScreenInfo() {
        screenHeight = getResources().getDisplayMetrics().heightPixels - 40;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    @SuppressLint({"ClickableViewAccessibility", "WrongConstant"})
    private void initView() {
        labelList = new ArrayList<>();

        views = LayoutInflater.from(mContext).inflate(R.layout.floating_menu_view, this);

        mLayout = views.findViewById(R.id.cl_floating_menu);
        mRecyclerView = views.findViewById(R.id.rv_floating_menu_view);
        mIvFloating = views.findViewById(R.id.iv_floating_menu_floating);

        floatingAdapter = new FloatingMenuAdapter(floatingMenuList);

        mRecyclerView.setAdapter(floatingAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new RecycleViewGridDivider(1, 0,
                true, RecycleViewGridDivider.LINEAR));

//        mIvFloating.setOnClickListener(v -> {
//            onClickListener.onClick();
//        });

//        mIvFloating.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                double x = event.getRawX();
//                double y = event.getRawY();
//                Log.e(TAG, "onTouch: " + event.getAction());
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        isClick = true;
//                        lastX = (int) x;
//                        lastY = (int) y;
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        isClick = false;
//                        double dx = x - lastX;
//                        double dy = y - lastY;
//                        Log.e(TAG, "onTouch: dx==" + dx + ",dy==" + dy);
////            startAnimation(dx,dy);
//
//                        //  moveMethod1(dx, dy);
//                        moveMethod2(dx, dy);
//
//                        lastX = (int) x;
//                        lastY = (int) y;
//                        break;
//                    case MotionEvent.ACTION_CANCEL:
//                    case MotionEvent.ACTION_UP:
//                        if (isClick) {
//                            onClickListener.onClick();
//                            //TODO Click action
//                        }
//                        break;
//                }
//                return true;
//            }
//        });

//        mIvFloating.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                //此方法获取的是手指相对于view的左上角的x,y值
//                int x = (int) event.getX();
//                int y = (int) event.getY();
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        lastX = x;
//                        lastY = y;
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        int offsetX = x - lastX;
//                        int offsetY = y - lastY;
//                        mIvFloating.layout(mIvFloating.getLeft() + offsetX,
//                                mIvFloating.getTop() + offsetY,
//                                mIvFloating.getRight() + offsetX,
//                                mIvFloating.getBottom() + offsetY);
//                        break;
//                }
//
//                return true;
//            }
//        });
//        mIvFloating.setOnTouchListener((v, event) -> {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    isClick = true;
//                    break;
//                case MotionEvent.ACTION_CANCEL:
//                case MotionEvent.ACTION_UP:
//                    if (isClick) {
//                        onClickListener.onClick();
//                        //TODO Click action
//                    }
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    isClick = false;
//                    break;
//                default:
//                    break;
//            }
//            return isClick;
//        });

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
//
//        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
//            @Override
//            public boolean tryCaptureView(View child, int pointerId) {
//                //mEdgeTrackerView禁止直接移动
//                return child == mDragView /*|| child == mAutoBackView*/;
//            }
//
//            @Override
//            public int clampViewPositionHorizontal(View child, int left, int dx) {
//                return left;
//            }
//
//            @Override
//            public int clampViewPositionVertical(View child, int top, int dy) {
//                return top;
//            }
//
//
//            //手指释放的时候回调
//            @Override
//            public void onViewReleased(View releasedChild, float xvel, float yvel) {
//                //mAutoBackView手指释放时可以自动回去
////                if (releasedChild == mAutoBackView) {
////                    mDragger.settleCapturedViewAt(mAutoBackOriginPos.x, mAutoBackOriginPos.y);
////                    invalidate();
////                }
//            }
//
//            //在边界拖动时回调
//            @Override
//            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
////                mDragger.captureChildView(mEdgeTrackerView, pointerId);
//            }
//        });
//        mDragger.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);


        setOnTouchListener(new OnDragTouchListener(mContext, true)
                .setOnDraggableClickListener(new OnDragTouchListener.OnDraggableClickListener() {
                    @Override
                    public void onDragged(View v, int left, int top) {
//                        v.layout(left,top,0,0);

                        ToastUtils.showShort("ff");
                    }

                    @Override
                    public void onClick(View v) {
                        if (onClickListener != null)
                            onClickListener.onClick();
                        setRotation(false);
                    }

                    @Override
                    public boolean onStartDrag() {
                        //rv 隐藏时才能拖动
                        return mRecyclerView.getVisibility() == View.GONE;
                    }
                }));
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//        return mDragger.shouldInterceptTouchEvent(event);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        mDragger.processTouchEvent(event);
//        return true;
//    }
//
//    @Override
//    public void computeScroll() {
//        if (mDragger.continueSettling(true)) {
//            invalidate();
//        }
//    }
//
//
//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//
//        mDragView = getChildAt(0);
//    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//        boolean result = mDragger.shouldInterceptTouchEvent(event);
//        float curX = event.getX();
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                lastX = (int) curX;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (Math.abs(curX - lastX) > mDragger.getTouchSlop()) {
//                    result = true;
//                }
//                break;
//        }
//
//        return mDragger.shouldInterceptTouchEvent(event);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        mDragger.processTouchEvent(event);
////        switch (event.getAction()) {
////            case MotionEvent.ACTION_DOWN:
////                isClick = true;
////                break;
////            case MotionEvent.ACTION_CANCEL:
////            case MotionEvent.ACTION_UP:
////                if (isClick) {
////                    if (onClickListener != null)
////                        onClickListener.onClick();
////                    setRotation(false);
////                    //TODO Click action
////                }
////                break;
////            case MotionEvent.ACTION_MOVE:
////                isClick = false;
////                break;
////            default:
////                break;
////        }
////        return isClick;
//        return true;
//    }
//
//    @Override
//    public void computeScroll() {
//        if (mDragger.continueSettling(true)) {
//            invalidate();
//        }
//    }
//
//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//
//        mDragView = getChildAt(0);
////        mDragView = mLayout;
//    }

    /**
     * 设置数据源 或者通过add的方式也没问题
     *
     * @param floatingList 数据源
     */
    public FloatingView setDate(List<FloatingMenuBean> floatingList) {
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
    public FloatingView add(int icon) {
        return adds(new int[]{icon});
    }

    /**
     * 添加数据源
     *
     * @param icons res 的图片资源
     */
    public FloatingView adds(int... icons) {
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
    public FloatingView add(String label) {
        return adds(label);
    }

    /**
     * 添加数据源
     *
     * @param labels 按钮名称
     */
    public FloatingView adds(String... labels) {
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
    public FloatingView adds(int icon, String... labels) {
        return adds(new int[]{icon}).adds(labels);
    }

    /**
     * 设置按钮组 - 背景色
     */
    public FloatingView setMenuBackground(int resId) {
        if (resId > 0) mRecyclerView.setBackgroundResource(resId);
        return this;
    }

    /**
     * 设置悬浮按钮 - 背景色
     */
    public FloatingView setFloatBackground(int resId) {
        if (resId > 0) mIvFloating.setBackgroundResource(resId);
        return this;
    }

    /**
     * 设置悬浮按钮 - icon
     * 默认 “+”图片
     */
    public FloatingView setFloatIcon(int resId) {
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
    public FloatingView setMargin(int left, int top, int right, int bottom) {
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

        mLayout.setLayoutParams(lp);
        return this;
    }

    /**
     * 设置最小高度
     *
     * @param minHeight 最小高度 单位 dp
     */
    public FloatingView setMinHeight(int minHeight) {
        mLayout.setMinimumHeight(Math.max(minHeight, 0));
        return this;
    }

    /**
     * 设置rv高度  默认自适应 最小40
     *
     * @param maxHeight 高度 单位 dp
     */
    public FloatingView setMaxHeight(int maxHeight) {
        ConstraintLayout.LayoutParams lp =
                (ConstraintLayout.LayoutParams) mRecyclerView.getLayoutParams();
        lp.height = ConvertUtils.dp2px(Math.max(maxHeight, 40));
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
    public FloatingView setRotation(boolean isHide) {
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
    public FloatingView setCheck(int check, List<? extends CheckedBean> list) {
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
    public FloatingView setHideFloating(boolean isHide) {
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
    public FloatingView setIcon(int resId, int position) {
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
    public FloatingView setText(int resId, int position) {
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
    public FloatingView setText(String str, int position) {
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
    public FloatingView setTextColor(int position, @ColorInt int color) {
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
    public FloatingView setTextSize(int position, float size) {
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
    public FloatingView setHasDividingLine(boolean hasDividingLine) {
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
    public FloatingView setDividingLineColor(int resColor) {
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
    public FloatingView setDividingLineHeight(int height) {
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
    public FloatingView setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    /**
     * 设置监听 悬浮按钮 - 长按
     */
    public FloatingView setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
        return this;
    }

    /**
     * 设置监听 按钮组 - 单击
     */
    public FloatingView setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    /**
     * 设置监听 按钮组 - 长按
     */
    public FloatingView setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
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
