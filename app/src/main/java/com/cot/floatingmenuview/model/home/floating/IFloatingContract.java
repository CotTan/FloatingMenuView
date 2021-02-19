package com.cot.floatingmenuview.model.home.floating;

import com.cot.floatingmenuview.base.BasePresenter;
import com.cot.floatingmenuview.base.BaseView;

public interface IFloatingContract {
    interface IFloatingView extends BaseView<IFloatingPresenter> {
    }

    interface IFloatingPresenter extends BasePresenter {
    }
}
