package com.cot.floatingmenuview.model.home.floating;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.cot.floatingmenuview.R;
import com.cot.floatingmenuview.base.BaseActivity;
import com.cot.floatingmenuview.model.adapter.StudentAdapter;
import com.cot.floatingmenuview.model.bean.StudentBean;
import com.cot.floatingmenuview.view.RecycleViewGridDivider;
import com.cot.floatingmenuview.view.floating.FloatingMenuView;
import com.cot.floatingmenuview.view.floating.bean.FloatingMenuBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.cot.floatingmenuview.view.floating.FloatingMenuView.SELECT_ALL;
import static com.cot.floatingmenuview.view.floating.FloatingMenuView.SELECT_ALL_NOT;
import static com.cot.floatingmenuview.view.floating.FloatingMenuView.SELECT_REVERSE;
import static com.cot.floatingmenuview.view.floating.bean.FloatingMenuBean.VERTICAL_IMAGE;
import static com.cot.floatingmenuview.view.floating.bean.FloatingMenuBean.VERTICAL_TEXT;

public class FloatingActivity extends BaseActivity {

    @BindView(R.id.rv_floating_student)
    RecyclerView rvFloatingStudent;
    @BindView(R.id.fmv_floating)
    FloatingMenuView fmvFloating;

    private List<FloatingMenuBean> floatingList;

    private List<StudentBean> studentList;
    private StudentAdapter studentAdapter;

    public static void loadActivity(Activity activity) {
        activity.startActivity(new Intent(activity, FloatingActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating);

        showToolBarTitle(getResources().getString(R.string.app_name));

        studentList = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            if (i % 2 == 0)
                studentList.add(new StudentBean("张三" + i));
            else
                studentList.add(new StudentBean("李四" + i));
        }

        studentAdapter = new StudentAdapter(R.layout.item_student, studentList);
        rvFloatingStudent.setLayoutManager(new GridLayoutManager(this, 1));
        rvFloatingStudent.setAdapter(studentAdapter);
        rvFloatingStudent.addItemDecoration(new RecycleViewGridDivider(32, 0,
                true, RecycleViewGridDivider.GRID));

        studentAdapter.setOnItemClickListener((adapter, view, position) -> {
            studentList.get(position).setChecked(!studentList.get(position).isChecked());
            studentAdapter.notifyDataSetChanged();
        });
        studentAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            studentList.get(position).setChecked(!studentList.get(position).isChecked());
            studentAdapter.notifyDataSetChanged();
        });

        floatingList = new ArrayList<>();
        floatingList.add(new FloatingMenuBean(this, VERTICAL_IMAGE, R.drawable.icon_to_top));
        floatingList.add(new FloatingMenuBean(this, VERTICAL_TEXT, R.string.select_all));
        floatingList.add(new FloatingMenuBean(this, VERTICAL_TEXT, R.string.reverse_select));
        floatingList.add(new FloatingMenuBean(this, VERTICAL_TEXT, R.string.save));

        /**
         * 通过setData() 或 adds() 方法都可以 ，个人更喜欢第二种，如果是纯数字需转换成字符
         * 因Java的switch case 不能使用变量，建议不用过期方法
         */
        fmvFloating
                .setData(floatingList)
                .add(VERTICAL_IMAGE, R.drawable.icon_to_top)
                .add(VERTICAL_TEXT, R.string.cancel)
                .adds(VERTICAL_IMAGE, R.drawable.icon_back, R.drawable.icon_to_top)
                .adds("吸附", "不吸附")
                .adds(R.drawable.icon_back, "测试0", "测试1", "测试2")
                .setText("置顶", 4)//此方法不支持resId获取
                .setText("隐藏", 11)
                .setTextColor(5, getResources().getColor(R.color.red))
                .setTextSize(5, 18)
                .setHasDividingLine(true)
                .setDividingLineHeight(1)
                .setMaxHeight(200)
                .setDrag(true)
                .setAutoPullToBorder(true)
                .setMargin(0, 10, 10, 20)
                .setMenuBackground(R.drawable.shape_solid_bg_blue_10)
                .setDividingLineColor(R.color.white)
                .setOnClickListener(() -> {
                    ToastUtils.showShort("单击 - 悬浮按钮");
                })
//                .setOnLongClickListener((FloatingMenuView.OnLongClickListener) v -> {
//                    ToastUtils.showShort("长按 - 悬浮按钮");
//                    return false;
//                })
                .setOnItemClickListener((adapter, view, position) -> {
                    //todo 由于 Java的switch case 不支持 变量表达式，建议使用if else 代替或者 position 或以下两种方式共用

                    ToastUtils.showShort("单击：" +
                            fmvFloating.getPositionName(position));
                    fmvFloating.setTextColor(position, getResources().getColor(R.color.yellow))
                            .setTextSize(position, 16);

                    /**
                     * 如果使用过期方法则按此方式判断
                     */
                    switch (fmvFloating.getPositionName(position)) {
                        case "置顶":
                            rvFloatingStudent.scrollToPosition(0);
                            break;
                        case "吸附":
                            fmvFloating.setAutoPullToBorder(true);
                            break;
                        case "不吸附":
                            fmvFloating.setAutoPullToBorder(false);
                            break;
                        case "隐藏"://隐藏
                            fmvFloating.setHideFloating(true);
                            break;
                    }

                    /**
                     * 正常方法，判断
                     */
                    switch (fmvFloating.getPositionResId(position)) {
                        case R.drawable.icon_to_top:
                            rvFloatingStudent.scrollToPosition(0);
                            break;
                        case R.string.select_all:
                            fmvFloating.setCheck(SELECT_ALL, studentList)
                                    .setText(R.string.unselect_all, position);
                            studentAdapter.notifyDataSetChanged();
                            break;
                        case R.string.unselect_all:
                            fmvFloating.setCheck(SELECT_ALL_NOT, studentList)
                                    .setText(R.string.select_all, position);
                            studentAdapter.notifyDataSetChanged();
                            break;
                        case R.string.reverse_select:
                            fmvFloating.setCheck(SELECT_REVERSE, studentList);
                            studentAdapter.notifyDataSetChanged();
                            break;
                        case R.string.save:
                            int count = 0;
                            for (StudentBean bean : studentList)
                                if (bean.isChecked()) count++;
                            ToastUtils.showShort("已选：" + count);
                            break;
                    }
                })
                .setOnItemLongClickListener((adapter, view, position) -> {
                    ToastUtils.showShort("长按：" + fmvFloating.getFloatingList().get(position).getLabelName());
                    if (fmvFloating.getPositionName(position).equals("隐藏")) {
                        fmvFloating.setHideFloating(true);
                    }
                    fmvFloating.dismiss()
                            .setTextColor(position, getResources().getColor(R.color.yellow))
                            .setTextSize(position, 18);
                    return true;
                });

    }

}
