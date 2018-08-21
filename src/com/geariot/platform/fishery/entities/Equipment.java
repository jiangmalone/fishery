package com.geariot.platform.fishery.entities;

public class Equipment {
	private String device_sn;	//设备编码
	private String name;		//设备名称
	private String userName;	//用户名称
	private int type;			//设备类型,1-传感器；2-一体机；3-控制器
	private String relation;	//设备所属用户
	private int status;			//设备状态
	private int way;			//设备第几路
	/*
	 * 传感器：0-离线；1-在线；2-异常
	 * 控制器：0-离线；1-断电；2-缺相；3-异常；4-正常
	 */
	
	public Equipment() {
		super();
	}

	public Equipment(String device_sn, String name, String userName, int type, String relation, int status,int way ) {
		super();
		this.device_sn = device_sn;
		this.name = name;
		this.userName = userName;
		this.type = type;
		this.relation = relation;
		this.status = status;
		this.way = way;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getWay() {
		return way;
	}

	public void setWay(int way) {
		this.way = way;
	}
	
	
	
	

}
