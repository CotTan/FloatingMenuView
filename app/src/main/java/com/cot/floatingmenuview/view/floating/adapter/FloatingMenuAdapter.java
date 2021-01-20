package com.cot.floatingmenuview.view.floating.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.blankj.utilcode.util.ConvertUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.cot.floatingmenuview.R;
import com.cot.floatingmenuview.view.floating.bean.FloatingMenuBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FloatingMenuAdapter extends BaseMultiItemQuickAdapter<FloatingMenuBean, BaseViewHolder> {

    public FloatingMenuAdapter(@Nullable List<FloatingMenuBean> data) {
        super(data);
        // 绑定 layout 对应的 type
        addItemType(FloatingMenuBean.VERTICAL_IMAGE, R.layout.item_floating_menu_vertical_image);
        addItemType(FloatingMenuBean.VERTICAL_TEXT, R.layout.item_floating_menu_vertical_text);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, FloatingMenuBean item) {

        LinearLayoutCompat layoutCompat = helper.getView(R.id.ll_floating_menu_item);
        if (item.isHasLine()) {
            //第二次后，加载之前清除上次加载的数据
            if (layoutCompat.getChildCount() > 1) layoutCompat.removeViewAt(1);
            //添加分割线
            if (helper.getAdapterPosition() >= 0
                    && helper.getAdapterPosition() < getData().size() - 1)
                layoutCompat.addView(createView(getContext(), item.getLineColor(), item.getLineHeight()));
        }

        // 根据返回的 type 分别设置数据
        switch (helper.getItemViewType()) {
            case FloatingMenuBean.VERTICAL_IMAGE:
                if (item.getIcon() != 0) {
                    helper.setImageResource(R.id.iv_floating_menu_icon, item.getIcon());
                    helper.getView(R.id.iv_floating_menu_icon).setVisibility(View.VISIBLE);
                } else
                    helper.getView(R.id.iv_floating_menu_icon).setVisibility(View.GONE);

                break;
            case FloatingMenuBean.VERTICAL_TEXT:
                TextView textView = helper.getView(R.id.tv_floating_menu_text);
                if (item.getLabelName() != null && !item.getLabelName().equals("")) {
                    textView.setText(item.getLabelName());
                    textView.setVisibility(View.VISIBLE);
                } else
                    textView.setVisibility(View.GONE);

                if (item.getTextColor() != 0)
                    textView.setTextColor(item.getTextColor());
                else
                    textView.setTextColor(getContext().getResources().getColor(R.color.white));

                if (item.getTextSize() != 0)
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, item.getTextSize());
                else
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                break;
        }
    }

    /**
     * 创建 LinearLayout 的子布局
     */
    public View createView(Context context, int backgroundColor, int height) {

        LinearLayoutCompat.LayoutParams llp = new LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT, Math.max(height, 0));

        llp.height = Math.max(height, 0);
        llp.leftMargin = ConvertUtils.dp2px(6);
        llp.rightMargin = ConvertUtils.dp2px(6);

        View view = new View(context);
        view.setLayoutParams(llp);
        if (backgroundColor == 0)
            view.setBackgroundColor(context.getResources().getColor(R.color.white));
        else
            view.setBackgroundColor(backgroundColor);

        return view;
    }

}