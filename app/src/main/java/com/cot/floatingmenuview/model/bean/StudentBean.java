package com.cot.floatingmenuview.model.bean;

import com.cot.floatingmenuview.view.floating.CheckedBean;

public class StudentBean extends CheckedBean {
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
