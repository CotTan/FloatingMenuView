package com.cot.floatingmenuview.view.floating.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @author COT
 * @version 1.0
 * @since 2021-01-18
 */
public class FloatingMenuBean implements MultiItemEntity {

    public static final int VERTICAL_IMAGE = 0X101 << 2;//竖向图
    public static final int VERTICAL_TEXT = 0X102 << 2;//竖向文

    private int itemType;//item 布局样式
    private int icon;//图片
    private String labelName;//文字

    private float textSize;//字体大小
    private int textColor;//字体颜色
    private boolean isCheck;//是否选中

    private int lineHeight = 1;//分割线高度 默认1px
    private int lineColor;//分割线颜色 默认白色
    private boolean hasLine;//是否需要分割线 默认false 不需要 true 需要

    public FloatingMenuBean(int itemType, int icon) {
        this(itemType, icon, "");
    }

    public FloatingMenuBean(int itemType, String labelName) {
        this(itemType, 0, labelName);
    }

    public FloatingMenuBean(int itemType, int icon, String labelName) {
        this(itemType, icon, labelName, false, false);
    }

    public FloatingMenuBean(int itemType, int icon, String labelName, boolean isCheck, boolean hasLine) {
        this.itemType = itemType;
        this.icon = icon;
        this.labelName = labelName;
        this.isCheck = isCheck;
        this.hasLine = hasLine;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
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
}
