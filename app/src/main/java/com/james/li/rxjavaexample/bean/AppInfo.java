package com.james.li.rxjavaexample.bean;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by lsy-android on 9/12/16 in zsl-tech.
 */
@Data
@Accessors(prefix = "m")
public class AppInfo implements Comparable<Object> {
    long mLastUpdateTime;

    String mName;

    String mIcon;

    public AppInfo(long mLastUpdateTime, String mName, String mIcon) {
        this.mLastUpdateTime = mLastUpdateTime;
        this.mName = mName;
        this.mIcon = mIcon;
    }

    @Override
    public int compareTo(Object obj) {
        AppInfo appInfo = (AppInfo) obj;
        return getName().compareTo(appInfo.getName());
    }
}
