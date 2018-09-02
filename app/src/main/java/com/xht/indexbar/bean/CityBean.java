package com.xht.indexbar.bean;

/**
 * Created by xht on 2018/3/7.
 */

public class CityBean extends BaseIndexPinyinBean {
    private String name;// 名称

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CityBean{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public String getTarget() {
        return name;
    }
}
