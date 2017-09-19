package com.weidi.artifact.db.bean;

public class BlacklistInfo {
	private String name;
	private String number;
	private String mode;
	
	public BlacklistInfo(){}

	public BlacklistInfo(String name, String number, String mode) {
		super();
		this.name = name;
		this.number = number;
		this.mode = mode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		return "BlacklistInfo [name=" + name + ", number=" + number + ", mode="
				+ mode + "]";
	}
	
	
}
