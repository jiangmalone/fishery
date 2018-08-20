package com.geariot.platform.fishery.entities;

public class EquipmentDetail {
	private String device_sn;	//设备编码
	private String name;		//设备名称
	private String userName;	//用户名称
	private String type;		//设备类型
	private boolean isOnline;	//设备状态
	
	public EquipmentDetail() {
		super();
	}

	public EquipmentDetail(String device_sn, String name, String type, String userName, boolean isOnline) {
		super();
		this.device_sn = device_sn;
		this.name = name;
		this.userName = userName;
		this.isOnline = isOnline;
	}

	public String getDevice_sn() {
		return device_sn;
	}

	public void setDevice_sn(String device_sn) {
		this.device_sn = device_sn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}
