package com.cot.floatingmenuview.model.home.main;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cot.floatingmenuview.R;
import com.cot.floatingmenuview.base.BaseActivity;
import com.cot.floatingmenuview.model.home.floating.FloatingActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.bt_floating)
    Button btFloating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showToolBarTitle(getResources().getString(R.string.app_name));
        setHideBackPressedIcon();
    }

    @OnClick({R.id.bt_floating})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_floating:
                FloatingActivity.loadActivity(this);
                break;
        }
    }
}