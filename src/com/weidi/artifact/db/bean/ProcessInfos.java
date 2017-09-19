package com.weidi.artifact.db.bean;

import android.graphics.drawable.Drawable;

import com.weidi.artifact.db.utils.DbVersion;

public class ProcessInfos {

    public String packageName;// 包名
    public String appName;// 程序名
    public String runningProcessName;// 进程名
    public Drawable icon;// 图标
    public String ramUsed;// 运行内存占用空间
    public boolean isUserProcess;// 是否是用户进程

    public ProcessInfos() {
    }

    public ProcessInfos(String packageName, String appName,
                        String runningProcessName, Drawable icon, String ramUsed,
                        boolean isUserProcess) {
        super();
        this.packageName = packageName;
        this.appName = appName;
        this.runningProcessName = runningProcessName;
        this.icon = icon;
        this.ramUsed = ramUsed;
        this.isUserProcess = isUserProcess;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getRunningProcessName() {
        return runningProcessName;
    }

    public void setRunningProcessName(String runningProcessName) {
        this.runningProcessName = runningProcessName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getRamUsed() {
        return ramUsed;
    }

    public void setRamUsed(String ramUsed) {
        this.ramUsed = ramUsed;
    }

    public boolean isUserProcess() {
        return isUserProcess;
    }

    public void setUserProcess(boolean isUserProcess) {
        this.isUserProcess = isUserProcess;
    }

    @Override
    public String toString() {
        return "ProcessInfos [packageName=" + packageName + ", appName="
                + appName + ", runningProcessName=" + runningProcessName
                + ", icon=" + icon + ", ramUsed=" + ramUsed
                + ", isUserProcess=" + isUserProcess + "]";
    }

}
