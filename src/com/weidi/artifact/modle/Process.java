package com.weidi.artifact.modle;

import android.graphics.drawable.Drawable;

import com.weidi.dbutil.ClassVersion;
import com.weidi.dbutil.Primary;

/**
 * Created by root on 16-12-25.
 */
@ClassVersion(version = 1)
public class Process {

    @Primary
    public int _id;
    public String packageName;//包名
    public String appName;//程序名
    public String runningProcessName;//进程名
    public Drawable icon;//图标
    public String ramUsed;//运行内存占用空间
    public boolean isUserProcess;//是否是用户进程
    
}
