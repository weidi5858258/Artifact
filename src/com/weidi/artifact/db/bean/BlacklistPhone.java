package com.weidi.artifact.db.bean;

import java.io.Serializable;

//address varchar,date varchar,read int,type int,body varchar
public class BlacklistPhone implements Serializable{
	private String number;
	private String address;
	private long date;//打进或打出的那一刻
	private long duration;
	private String time;//转化后的时间
	private int type;//1打进，2打出，3未接
	private int news;//默认为1
	private int flag;//0表示只响铃没有接通，1表示已经接通过，2表示被拦截，3表示打出去的电话没有接通，4表示打出去的电话已经接通过
	
	public BlacklistPhone(){}
	public BlacklistPhone(String number, String address, long date, long duration, String time, int type, int news, int flag) {
		super();
		this.number = number;
		this.address = address;
		this.date = date;
		this.duration = duration;
		this.time = time;
		this.type = type;
		this.news = news;
		this.flag = flag;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getNews() {
		return news;
	}
	public void setNews(int news) {
		this.news = news;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	@Override
	public String toString() {
		return "BlacklistPhone [number=" + number + ", address=" + address
				+ ", date=" + date + ", duration=" + duration + ", time="
				+ time + ", type=" + type + ", news=" + news + ", flag=" + flag
				+ "]";
	}
	
}
