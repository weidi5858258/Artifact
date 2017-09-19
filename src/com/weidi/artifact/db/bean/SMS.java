package com.weidi.artifact.db.bean;

public class SMS {
	public String address;
	public long date;
	public int read;
	public int type;
	public String body;
	
	public SMS(){}

	public SMS(String address, long date, int read, int type, String body) {
		super();
		this.address = address;
		this.date = date;
		this.read = read;
		this.type = type;
		this.body = body;
	}

	@Override
	public String toString() {
		return "Sms:address=" + address + ", date=" + date + ", read=" + read
				+ ", type=" + type + ", body=" + body;
	}
	
	
}
