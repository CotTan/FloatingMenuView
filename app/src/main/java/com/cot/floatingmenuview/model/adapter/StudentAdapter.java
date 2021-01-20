package com.cot.floatingmenuview.model.adapter;

import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.cot.floatingmenuview.R;
import com.cot.floatingmenuview.model.bean.StudentBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StudentAdapter extends BaseQuickAdapter<StudentBean, BaseViewHolder> {

    public StudentAdapter(int layoutResId, @Nullable List<StudentBean> data) {
        super(layoutResId, data);
        addChildClickViewIds(R.id.cb_item_student);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, StudentBean item) {

        CheckBox checkBox = helper.getView(R.id.cb_item_student);
        checkBox.setChecked(item.isChecked());

        setItemText(helper, String.valueOf(helper.getAdapterPosition() + 1),
                R.id.tv_item_student_serial, false);
        setItemText(helper, item.getName(), R.id.tv_item_student_name, false);
    }

    /**
     * item 中设置文本
     *
     * @param helper BaseViewHolder
     * @param value  值
     * @param viewId 控件id
     * @param isHide 是否根据空值隐藏
     */
    public void setItemText(BaseViewHolder helper, String value, @IdRes int viewId, boolean isHide) {
        if (!isEmpty(value)) {
            if (isHide) helper.getView(viewId).setVisibility(View.VISIBLE);
            helper.setText(viewId, value);
        } else {
            if (isHide) helper.getView(viewId).setVisibility(View.GONE);
            helper.setText(viewId, "");
        }
    }

    /**
     * 判断字符串是否为空
     *
     * @param s 要判断的字符串
     * @return true:s为null或者为""; false:s有字符
     */
    public boolean isEmpty(String s) {
        return s == null || s.length() <= 0;
    }

}
