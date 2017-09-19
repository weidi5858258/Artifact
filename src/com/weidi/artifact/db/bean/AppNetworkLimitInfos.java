package com.weidi.artifact.db.bean;

public class AppNetworkLimitInfos {
	private String packageName;
	private int uid;
	private int mobile;
	private int wifi;
	
	public AppNetworkLimitInfos(){}

	public AppNetworkLimitInfos(String packageName, int uid, int mobile,
			int wifi) {
		super();
		this.packageName = packageName;
		this.uid = uid;
		this.mobile = mobile;
		this.wifi = wifi;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
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
		return "AppNetworkLimitInfos [packageName=" + packageName + ", uid="
				+ uid + ", mobile=" + mobile + ", wifi=" + wifi + "]";
	}
	
	
	
}
