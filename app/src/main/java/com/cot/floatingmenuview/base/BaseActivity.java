package com.cot.floatingmenuview.base;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.cot.floatingmenuview.R;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    protected ViewGroup viewGroup;
    protected LinearLayout parentLinearLayout;
    protected TextView tvToolbarCenterTitle;
    protected TextView tvToolbarRightText;
    protected Toolbar tbToolbar;

    private FrameLayout frameLayout;

    private boolean isShowDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //强制竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        trimMemory();
        initSystemBarTint();
        initContentView();
        initToolBar();
        setHideAppBar();

        if (KeyboardUtils.isSoftInputVisible(this)) {
            KeyboardUtils.hideSoftInput(this);//隐藏键盘
        }
    }

    private void initContentView() {
        viewGroup = findViewById(android.R.id.content);
        viewGroup.removeAllViews();
        parentLinearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        parentLinearLayout.setLayoutParams(lp);
        parentLinearLayout.setOrientation(LinearLayout.VERTICAL);
        viewGroup.addView(parentLinearLayout);
        frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(lp);
        ProgressBar progressBar = new ProgressBar(this);
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(layoutParams);
        frameLayout.addView(progressBar);
        frameLayout.setClickable(true);
        frameLayout.setVisibility(View.GONE);
        frameLayout.setBackgroundColor(getResources().getColor(R.color.backgroundTransparent));
        viewGroup.addView(frameLayout);
        LayoutInflater.from(this).inflate(R.layout.toolbar_layout, parentLinearLayout, true);
    }

    private void initToolBar() {
        tbToolbar = findViewById(R.id.tb_toolbar);
        tvToolbarCenterTitle = findViewById(R.id.tv_toolbar_center_title);
        tvToolbarRightText = findViewById(R.id.tv_toolbar_right_text);
        if (!isShowCenterToolBarTitle()) {
            tbToolbar.setTitle(getString(R.string.app_name));
        }
        setSupportActionBar(tbToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            if (isShowCenterToolBarTitle()) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
        if (tbToolbar != null) {
            tbToolbar.setNavigationOnClickListener(view -> onBackPressed());
        }
    }

    protected void setBackClick(View.OnClickListener onClickListener) {
        if (tbToolbar != null && onClickListener != null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }
            if (KeyboardUtils.isSoftInputVisible(this)) {
                KeyboardUtils.hideSoftInput(this);//隐藏键盘
            }
            tbToolbar.setNavigationOnClickListener(onClickListener);

        }
    }

    protected void setToolBarTitle(CharSequence title) {
        if (tbToolbar != null) {
            tbToolbar.setTitle(title);
        }
    }

    protected void setToolBarTitle(int titleId) {
        if (tbToolbar != null) {
            tbToolbar.setTitle(titleId);
        }
    }

    /**
     * 需要设置isShowCenterToolBarTitle 为true才生效
     *
     * @param title 标题
     */
    protected void setCenterToolBarTitle(CharSequence title) {
        if (tvToolbarCenterTitle != null) {
            tvToolbarCenterTitle.setText(title);
        }
    }

    protected void setCenterToolBarTitle(int titleId) {
        if (tvToolbarCenterTitle != null) {
            tvToolbarCenterTitle.setText(titleId);
        }
    }

    protected boolean isShowCenterToolBarTitle() {
        return true;
    }

    protected void setHideAppBar() {
        if (getSupportActionBar() == null) {
            return;
        }
        getSupportActionBar().hide();
    }

    protected void setShowAppBar() {
        if (getSupportActionBar() == null) {
            return;
        }
        getSupportActionBar().show();
    }

    protected void setShowToolBarRightText() {
        if (tvToolbarRightText != null) {
            tvToolbarRightText.setVisibility(View.VISIBLE);
        }
    }

    protected void setHideToolBarRightText() {
        if (tvToolbarRightText != null) {
            tvToolbarRightText.setVisibility(View.INVISIBLE);
        }
    }

    protected void setToolBarRightText(CharSequence text) {
        if (tvToolbarRightText != null) {
            setShowToolBarRightText();
            tvToolbarRightText.setText(text);
        }
    }

    protected void setToolBarRightIconResource(int resId) {
        if (tvToolbarRightText != null) {
            setShowToolBarRightText();

            String imageResId = "<img src='" + resId + "'>";
            tvToolbarRightText.setText(Html.fromHtml(imageResId, source -> {
                int id = Integer.parseInt(source);
                Drawable drawable = ContextCompat.getDrawable(BaseActivity.this, resId);
                if (drawable == null) {
                    return null;
                }
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }, null));
        }
    }

    protected void setToolBarRightText(int text) {
        if (tvToolbarRightText != null) {
            setShowToolBarRightText();
            tvToolbarRightText.setText(text);
        }
    }

    protected void setToolbarRightTextColor(@ColorInt int textColor) {
        if (tvToolbarRightText != null) {
            tvToolbarRightText.setTextColor(textColor);
        }
    }

    protected void setToolBarRightTextClick(View.OnClickListener onClickListener) {
        if (KeyboardUtils.isSoftInputVisible(this)) {
            KeyboardUtils.hideSoftInput(this);//隐藏键盘
        }
        if (tvToolbarRightText != null && onClickListener != null) {
            tvToolbarRightText.setOnClickListener(onClickListener);
        }
    }

    protected void showToolBarTitle(int textId) {
        setShowAppBar();
        setToolBarTitle(textId);
    }

    protected void showToolBarTitle(CharSequence title) {
        setShowAppBar();
        setToolBarTitle(title);
    }

    protected void setHideBackPressedIcon() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }
    }

    public void setShowBackPressedIcon() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    protected void setToolBarRightDrawable(int resId) {
        if (tvToolbarRightText != null) {
            setShowToolBarRightText();
            Drawable drawable = getResources().getDrawable(resId);
            tvToolbarRightText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void setToolbarMenu() {
        setShowAppBar();
        setToolBarRightText(getResources().getString(R.string.app_name));
        setToolBarRightDrawable(R.drawable.icon_back);
        setToolbarRightTextColor(Color.WHITE);
        if (tvToolbarRightText != null) {
            tvToolbarRightText.setTextSize(SizeUtils.sp2px(10));
            tvToolbarRightText.setCompoundDrawablePadding(15);
            Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) tvToolbarRightText.getLayoutParams();
            layoutParams.setMarginEnd(30);
            tvToolbarRightText.setLayoutParams(layoutParams);
            tvToolbarRightText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID, parentLinearLayout, true);

        viewGroup.bringChildToFront(frameLayout);

        ButterKnife.bind(this);
    }

    /**
     * 设置状态栏颜色
     */
    protected void initSystemBarTint() {
        Window window = getWindow();
        if (translucentStatusBar()) {
            // 设置状态栏全透明
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            return;
        }
        // 沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0以上使用原生方法
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(setStatusBarColor());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = window.getDecorView();
            int vis = decorView.getSystemUiVisibility();
            if (isLightMode()) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decorView.setSystemUiVisibility(vis);
        }
    }

    protected boolean isLightMode() {
        return false;
    }

    /**
     * 获取主题色
     */
    public int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    /**
     * 子类可以重写改变状态栏颜色
     */
    protected int setStatusBarColor() {
        return getColorPrimary();
    }

    /**
     * 子类可以重写决定是否使用透明状态栏
     */
    protected boolean translucentStatusBar() {
        return false;
    }

//    private void trimMemory() {
//        if (!"SplashActivity".equals(getClass().getSimpleName()) && 0 == Constant.STATUS_FORCE_KILLED) {
//            Intent intent = new Intent(this, SplashActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            android.os.Process.killProcess(android.os.Process.myPid());
//        }
//    }

    public void clickBackButton(View v) {
//        if(getSupportFragmentManager().getFragments().size()>0){
//            getSupportFragmentManager().popBackStack();
//        }else {
//            finish();
//        }
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void showDialog() {
        if (frameLayout != null && frameLayout.getVisibility() == View.GONE) {
            frameLayout.setVisibility(View.VISIBLE);

            isShowDialog = true;
        }
    }

    public void dismissDialog() {
        if (frameLayout != null && frameLayout.getVisibility() == View.VISIBLE) {
            frameLayout.setVisibility(View.GONE);

            isShowDialog = false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 判断连续点击事件时间差
            /*if (GeneralUtils.isFastClick()) {
                return true;
            }*/ /*else {
                //隐藏键盘
                if (KeyboardUtils.isSoftInputVisible(this)) {
                    KeyboardUtils.hideSoftInput(this);
                }
            }*/
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isShowDialog && keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}