package com.cot.floatingmenuview.model.home.maintenance;

import android.os.Bundle;
import android.view.KeyEvent;

import com.blankj.utilcode.util.ToastUtils;
import com.cot.floatingmenuview.R;
import com.cot.floatingmenuview.base.BaseActivity;

public class SystemMaintenanceActivity extends BaseActivity {
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_maintenance);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtils.showShort(getResources().getString(R.string.again_exit));
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
