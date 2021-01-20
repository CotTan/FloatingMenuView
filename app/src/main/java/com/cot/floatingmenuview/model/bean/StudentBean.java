package com.cot.floatingmenuview.model.bean;

import com.cot.floatingmenuview.view.floating.BeanChecked;

public class StudentBean extends BeanChecked {
    String name;

    public StudentBean() {
    }

    public StudentBean(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
