package com.cot.floatingmenuview.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.LogUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cot
 * @version 1.0.0
 * @since 2020-09-18 16:13
 */
public class GeneralUtils {

    /**
     * 回调接口
     */
    public interface Callback {
        void onCallback(String value);
    }

    // 两次点击按钮之间的点击间隔不能少于400毫秒
    private static final int MIN_CLICK_DELAY_TIME = 400;
    private static long lastClickTime;

    /**
     * 判断是否快速点击按钮
     *
     * @return true 属于快速点击
     */
    public static boolean isFastClick() {
        boolean flag = true;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = false;
        }
        LogUtils.i("ClickTime:" + (curClickTime - lastClickTime));
        lastClickTime = curClickTime;
        return flag;
    }

    /**
     * 判断字符串是否为空
     *
     * @param s 要判断的字符串
     * @return true:s为null或者为""; false:s有字符
     */
    public static boolean isEmpty(String s) {
        return s == null || s.length() <= 0;
    }

    /**
     * 将object转换成集合
     *
     * @param obj   object
     * @param clazz 实体类
     * @param <T>   泛型
     * @return 实体类的集合
     */
    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    /**
     * 判断是否为空集合
     */
    public static boolean isEmptyList(List<?> list) {
        return (list == null || list.size() == 0);
    }

    /**
     * 判断是否为空集合 如果是则new 一个list  不是则返回
     */
    public static <T> List<T> getList(List<T> list) {
        if (isEmptyList(list)) {
            list = new ArrayList<>();
        }
        return list;
    }

    /**
     * 判断是否存在虚拟按键
     *
     * @return true: 存在
     */
    public static boolean hasNavigationBar(Activity activity) {
        boolean hasNavigationBar = false;
        Resources rs = activity.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            @SuppressLint("PrivateApi")
            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception ignored) {
        }
        return hasNavigationBar;
    }

    /**
     * 隐藏虚拟按键
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void hideVirtualKey(Activity activity) {

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        // 使用activity的window是隐藏虚拟按键。
        activity.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }

    /**
     * 获取虚拟按键的高度
     */
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavigationBar((Activity) context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

}
