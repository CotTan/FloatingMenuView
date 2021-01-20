package com.cot.floatingmenuview.base;

public interface BaseView<T> {
    void setPresenter(T presenter);

    void showDialog();

    void dismissDialog();
}
