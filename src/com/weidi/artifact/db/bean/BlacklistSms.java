package com.weidi.artifact.db.bean;

import java.io.Serializable;

//address varchar,date varchar,read int,type int,body varchar
public class BlacklistSms implements Serializable{
	private String number;
	private String address;
	private long date;
	private String time;
	private int read;
	private int type;
	private String body;
	
	public BlacklistSms(){}
	public BlacklistSms(String number, String address, long date, String time, int read, int type,
			String body) {
		super();
		this.number = number;
		this.address = address;
		this.date = date;
		this.time = time;
		this.read = read;
		this.type = type;
		this.body = body;
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
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getRead() {
		return read;
	}
	public void setRead(int read) {
		this.read = read;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	@Override
	public String toString() {
		return "BlacklistSms [address=" + address + ", date=" + date
				+ ", read=" + read + ", type=" + type + ", body=" + body + "]";
	}
	
	
}
