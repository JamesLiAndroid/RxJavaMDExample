package com.james.li.rxjavaexample.bean;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;

import java.util.Locale;

import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created by lsy-android on 9/12/16 in zsl-tech.
 */
@Accessors(prefix = "m")
public class AppInfoRich implements Comparable<Object> {

    @Setter
    String mName = null;

    private Context mContext;

    private ResolveInfo mResolveInfo;

    private ComponentName mComponentName = null;

    private PackageInfo pi = null;

    private Drawable icon = null;

    public AppInfoRich(Context context, ResolveInfo info) {
        this.mContext = context;
        this.mResolveInfo = info;

        mComponentName = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
        try {
            pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        if (mName != null) {
            return mName;
        } else {
            try {
                return getNameFromResolveInfo(mResolveInfo);
            } catch (PackageManager.NameNotFoundException e) {
                return getPackageName();
            }
        }
    }

    public String getActivityName() {
        return mResolveInfo.activityInfo.name;
    }

    public ComponentName getComponentName() {
        return mComponentName;
    }

    public String getComponentInfo() {
        if (getComponentName() != null) {
            return getComponentName().toString();
        } else {
            return "";
        }
    }


    public ResolveInfo getResolveInfo() {
        return mResolveInfo;
    }

    public PackageInfo getPackageInfo() {
        return pi;
    }

    public String getVersionName() {
        PackageInfo pi = getPackageInfo();
        if (pi != null) {
            return pi.versionName;
        } else {
            return "";
        }
    }

    public int getVersionCode() {
        PackageInfo pi = getPackageInfo();
        if (pi != null) {
            return pi.versionCode;
        } else {
            return 0;
        }
    }


    public Drawable getIcon() {
        if (icon == null) {
            icon = getResolveInfo().loadIcon(mContext.getPackageManager());
            /*
            Drawable dr = getResolveInfo().loadIcon(mContext.getPackageManager());
            Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
            icon = new BitmapDrawable(mContext.getResources(), AppHelper.getResizedBitmap(bitmap, 144, 144));
            */
        }
        return icon;
    }

    @SuppressLint("NewApi")
    public long getFirstInstallTime() {
        PackageInfo pi = getPackageInfo();
        if (pi != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            return pi.firstInstallTime;
        } else {
            return 0;
        }
    }

    @SuppressLint("NewApi")
    public long getLastUpdateTime() {
        PackageInfo pi = getPackageInfo();
        if (pi != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            return pi.lastUpdateTime;
        } else {
            return 0;
        }
    }


    @Override
    public String toString() {
        return getName();
    }


    public String getPackageName() {
        return mResolveInfo.activityInfo.packageName;
    }

    /**
     * 从ResolveInfo获取应用程序名称
     * @param mResolveInfo
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    public String getNameFromResolveInfo(ResolveInfo mResolveInfo) throws PackageManager.NameNotFoundException {
        String name = mResolveInfo.resolvePackageName;
        if (mResolveInfo.activityInfo != null) {
            Resources res = mContext.getPackageManager()
                    .getResourcesForApplication(mResolveInfo.activityInfo.applicationInfo);
            Resources engRes = getEnglishResources(res);

            if (mResolveInfo.activityInfo.labelRes != 0) {
                name = engRes.getString(mResolveInfo.activityInfo.labelRes);

                if (name == null || name.equals("")) {
                    name = res.getString(mResolveInfo.activityInfo.labelRes);
                }
            } else {
                name = mResolveInfo.activityInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString();
            }
        }
        return name;
    }

    /**
     * 获取应用的英文信息
     * @param res
     * @return
     */
    public Resources getEnglishResources(Resources res) {
        AssetManager manager = res.getAssets();
        DisplayMetrics metrics = res.getDisplayMetrics();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(Locale.US);
        } else {
            config.locale = Locale.US;
        }
        return new Resources(manager, metrics, config);
    }

    @Override
    public int compareTo(Object o) {
        AppInfoRich f = (AppInfoRich) o;
        return getName().compareTo(f.getName());
    }
}
