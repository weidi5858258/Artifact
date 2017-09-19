package com.weidi.artifact.db.bean;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class RecentApp {

    public String packageName;
    public String appName;
    public Drawable icon;
    public Intent intent;

    public RecentApp() {
    }

    public RecentApp(String packageName, String appName, Drawable icon,
                     Intent intent) {
        this.packageName = packageName;
        this.appName = appName;
        this.icon = icon;
        this.intent = intent;
    }

    @Override
    public String toString() {
        return "RecentApp [packageName=" + packageName + ", appName=" + appName
                + ", icon=" + icon + ", intent=" + intent + "]";
    }


}
