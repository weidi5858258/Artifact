package com.weidi.artifact.db.bean;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

public class RecentTask implements Parcelable{
	private String packageName;
	private String appName;
	private byte[] bt;
//	private Drawable icon;
	private Intent intent;
	
	public RecentTask(){}

//	public RecentTask(String packageName, String appName, Drawable icon, Intent intent) {
//		super();
//		this.packageName = packageName;
//		this.appName = appName;
//		this.icon = icon;
//		this.intent = intent;
//	}

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

//	public Drawable getIcon() {
//		return icon;
//	}
//
//	public void setIcon(Drawable icon) {
//		this.icon = icon;
//	}
	
	public byte[] getBt() {
		return bt;
	}
	
	public void setBt(byte[] bt) {
		this.bt = bt;
	}

	public Intent getIntent() {
		return intent;
	}


	public void setIntent(Intent intent) {
		this.intent = intent;
	}


	
	
	
	
	 public int describeContents() {
         return 0;
     }

     public void writeToParcel(Parcel out, int flags) {
         out.writeString(packageName);
         out.writeString(appName);
         
         out.writeInt(bt.length);
         out.writeByteArray(bt);
         
         
         out.writeParcelable(intent, flags);
     }

	public static final Parcelable.Creator<RecentTask> CREATOR = new Parcelable.Creator<RecentTask>() {
		public RecentTask createFromParcel(Parcel in) {
			RecentTask task = new RecentTask();
			task.packageName = in.readString();
			task.appName = in.readString();
			int len = in.readInt();
			byte[] b = new byte[len];
			in.readByteArray(b);
			task.bt = b;
			task.intent = in.readParcelable(Intent.class.getClassLoader());
			
			return task;
		}

		public RecentTask[] newArray(int size) {
			return new RecentTask[size];
		}
	};

	
}
