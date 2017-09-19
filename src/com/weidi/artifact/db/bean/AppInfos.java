package com.weidi.artifact.db.bean;

import android.graphics.drawable.Drawable;

public class AppInfos {
	private String packageName;//包名
	private String appName;//程序名
	private Drawable icon;//图标
	private int uid;
	private String spaceUsage;//占用空间
	private long cache;//缓存大小
	private boolean isUserApp;//是否是用户程序
	private boolean isInstallMemory;//是否安装在手机内存中
	private int mobile;
	private int wifi;
	
	public AppInfos(){}

	public AppInfos(String packageName, String appName, Drawable icon,int uid,String spaceUsage,
			long cache, boolean isUserApp, boolean isInstallMemory,int mobile,int wifi) {
		super();
		this.packageName = packageName;
		this.appName = appName;
		this.icon = icon;
		this.uid = uid;
		this.spaceUsage = spaceUsage;
		this.cache = cache;
		this.isUserApp = isUserApp;
		this.isInstallMemory = isInstallMemory;
		this.mobile = mobile;
		this.wifi = wifi;
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

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	
	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getSpaceUsage() {
		return spaceUsage;
	}

	public void setSpaceUsage(String spaceUsage) {
		this.spaceUsage = spaceUsage;
	}
	
	public long getCache() {
		return cache;
	}

	public void setCache(long cache) {
		this.cache = cache;
	}

	public boolean isUserApp() {
		return isUserApp;
	}

	public void setUserApp(boolean isUserApp) {
		this.isUserApp = isUserApp;
	}

	public boolean isInstallMemory() {
		return isInstallMemory;
	}

	public void setInstallMemory(boolean isInstallMemory) {
		this.isInstallMemory = isInstallMemory;
	}
	
	public int getMobile() {
		return mobile;
	}

	public void setMobile(int mobile) {
		this.mobile = mobile;
	}

	public int getWifi() {
		return wifi;
	}

	public void setWifi(int wifi) {
		this.wifi = wifi;
	}

	@Override
	public String toString() {
		return "AppInfos [packageName=" + packageName + ", appName=" + appName
				+ ", icon=" + icon + ", uid=" + uid + ", spaceUsage="
				+ spaceUsage + ", cache=" + cache + ", isUserApp=" + isUserApp
				+ ", isInstallMemory=" + isInstallMemory + ", mobile=" + mobile
				+ ", wifi=" + wifi + "]";
	}

	

}
