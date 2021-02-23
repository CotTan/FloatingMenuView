package com.cot.floatingmenuview.view.floating.bean;

import android.content.Context;

import androidx.annotation.AnyRes;
import androidx.annotation.IntDef;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author COT
 * @version 1.0
 * @since 2021-01-18
 */
public class FloatingMenuBean implements MultiItemEntity {

    public static final int VERTICAL_IMAGE = 0X101 << 2;//竖向图
    public static final int VERTICAL_TEXT = 0X102 << 2;//竖向文

    @Retention(RetentionPolicy.SOURCE)//注解只保留在源文件，当Java文件编译成class文件的时候，注解被遗弃
    @Target(ElementType.PARAMETER)////注解使用目标为形参
    @IntDef({VERTICAL_IMAGE, VERTICAL_TEXT})
    public @interface ItemType {
    }

    private int itemType;//item 布局样式
    private int resId;//资源id
    private String labelName;//文字

    private float textSize;//字体大小
    private int textColor;//字体颜色
    private boolean isCheck;//是否选中

    private int lineHeight = 1;//分割线高度 默认1px
    private int lineColor;//分割线颜色 默认白色
    private boolean hasLine;//是否需要分割线 默认false 不需要 true 需要

    public FloatingMenuBean(Context context, @ItemType int itemType, @AnyRes int resId) {
        this(itemType, resId,
                itemType == VERTICAL_IMAGE ? String.valueOf(resId) : context.getResources().getString(resId));
    }

    @Deprecated //因Java的switch case 不能使用变量建议不用此方法输入
    public FloatingMenuBean(String labelName) {
        this(VERTICAL_TEXT, 0, labelName);
    }

    public FloatingMenuBean(@ItemType int itemType, @AnyRes int resId, String labelName) {
        this(itemType, resId, labelName, false, false);
    }

    public FloatingMenuBean(@ItemType int itemType, @AnyRes int resId, String labelName, boolean isCheck, boolean hasLine) {
        this.itemType = itemType;
        this.resId = resId;
        this.labelName = labelName;
        this.isCheck = isCheck;
        this.hasLine = hasLine;

    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public int getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public boolean isHasLine() {
        return hasLine;
    }

    public void setHasLine(boolean hasLine) {
        this.hasLine = hasLine;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(@ItemType int itemType) {
        this.itemType = itemType;
    }
}
