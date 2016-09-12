package com.james.li.rxjavaexample.bean;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created by lsy-android on 9/12/16 in zsl-tech.
 */
@Accessors(prefix = "m")
public class ApplicationList {
    private static ApplicationList list = new ApplicationList();

    @Setter
    @Getter
    private List<AppInfo> mList;

    private ApplicationList() {

    }

    public static ApplicationList getInstance() {
        return list;
    }
}
