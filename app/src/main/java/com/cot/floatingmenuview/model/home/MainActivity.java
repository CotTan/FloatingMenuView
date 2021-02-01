package com.cot.floatingmenuview.model.home;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
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

public class MainActivity extends BaseActivity {

    @BindView(R.id.rv_main_student)
    RecyclerView rvMainStudent;
    @BindView(R.id.fmv_floating)
    FloatingMenuView fmvFloating;
    @BindView(R.id.cl_main)
    ConstraintLayout clMain;

    private List<FloatingMenuBean> floatingList;

    private List<StudentBean> studentList;
    private StudentAdapter studentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showToolBarTitle(getResources().getString(R.string.app_name));
        setHideBackPressedIcon();

        studentList = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            if (i % 2 == 0)
                studentList.add(new StudentBean("张三" + i));
            else
                studentList.add(new StudentBean("李四" + i));
        }

        studentAdapter = new StudentAdapter(R.layout.item_student, studentList);
        rvMainStudent.setLayoutManager(new GridLayoutManager(this, 1));
        rvMainStudent.setAdapter(studentAdapter);
        rvMainStudent.addItemDecoration(new RecycleViewGridDivider(32, 0,
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
        floatingList.add(new FloatingMenuBean(FloatingMenuBean.VERTICAL_IMAGE, R.drawable.icon_to_top));
        floatingList.add(new FloatingMenuBean(FloatingMenuBean.VERTICAL_TEXT, "全选"));
        floatingList.add(new FloatingMenuBean(FloatingMenuBean.VERTICAL_TEXT, "反选"));
        floatingList.add(new FloatingMenuBean(FloatingMenuBean.VERTICAL_TEXT, "保存"));

        fmvFloating
                .setDate(floatingList) //两种方式都行
                .add(R.drawable.icon_to_top)
                .add("取消")
                .adds(R.drawable.icon_back, R.drawable.icon_to_top)
                .adds("测试", "制作")
                .adds(R.drawable.icon_back, "讨论", "调查", "发现", "知乎", "百度")
                .setTextColor(5, getResources().getColor(R.color.red))
                .setTextSize(5, 18)
                .setText("wow", 8)
                .setHasDividingLine(true)
                .setDividingLineHeight(1)
                .setMaxHeight(200)
                .setDrag(true)
                .setMargin(0, 10, 10, 20)
                .setMenuBackground(R.drawable.shape_solid_bg_blue_10)
                .setDividingLineColor(getResources().getColor(R.color.white))
                .setOnClickListener(() -> {
                    ToastUtils.showShort("单击 - 悬浮按钮");
                })
//                .setOnLongClickListener((FloatingMenuView.OnLongClickListener) v -> {
//                    ToastUtils.showShort("长按 - 悬浮按钮");
//                    return false;
//                })
                .setOnItemClickListener((adapter, view, position) -> {
                    switch (position) {
                        case 0:
                        case 4:
                            rvMainStudent.scrollToPosition(0);
                            break;
                        case 1:
                            if (fmvFloating.getPositionName(position).equals("全选")) {
                                fmvFloating.setCheck(1, studentList).setText("取消全选", position);
                                studentAdapter.notifyDataSetChanged();
                            } else if (fmvFloating.getPositionName(position).equals("取消全选")) {
                                fmvFloating.setCheck(2, studentList).setText("全选", position);
                                studentAdapter.notifyDataSetChanged();
                            }
                            break;
                        case 2:
                            fmvFloating.setCheck(3, studentList);
                            studentAdapter.notifyDataSetChanged();
                            break;
                        case 3:
                            int count = 0;
                            for (StudentBean bean : studentList) {
                                if (bean.isChecked())
                                    count++;
                            }
                            ToastUtils.showShort("已选：" + count);
                            break;
                        case 8:
                            fmvFloating.setHideFloating(true);
                            break;
                        default:
                            ToastUtils.showShort("单击：" + fmvFloating.getFloatingList().get(position).getLabelName());
                            fmvFloating.setTextColor(position, getResources().getColor(R.color.yellow))
                                    .setTextSize(position, 16);
                            break;
                    }
                })
                .setOnItemLongClickListener((adapter, view, position) -> {
                    ToastUtils.showShort("长按：" + fmvFloating.getFloatingList().get(position).getLabelName());
                    if (position == 8) {
                        fmvFloating.setHideFloating(true);
                    }
                    fmvFloating.setRotation(true);
                    fmvFloating.setTextColor(position, getResources().getColor(R.color.yellow))
                            .setTextSize(position, 18);
                    return true;
                });

    }

}