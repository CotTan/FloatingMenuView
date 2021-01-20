package com.cot.floatingmenuview.model.home;

import com.cot.floatingmenuview.base.BasePresenter;
import com.cot.floatingmenuview.base.BaseView;

public interface IMainContract {
    interface IMainView extends BaseView<IMainPresenter> {
    }

    interface IMainPresenter extends BasePresenter {
    }
}
