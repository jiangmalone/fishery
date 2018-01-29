/**
 * 
 */
package com.geariot.platform.fishery.model;

/**
 * @author mxy940127
 *
 */
public class Equipment {
	private String device_sn;
	private String name;
	private int status;
	private String userName;
	private String relation;
	private int sensorId;
	public Equipment() {

	}

	public Equipment(String device_sn, String name, int status) {
		this.device_sn = device_sn;
		this.name = name;
		this.status = status;
	}
	
	public Equipment(String device_sn, String name, int status, String relation) {
		this.device_sn = device_sn;
		this.name = name;
		this.status = status;
		this.relation = relation;
	}
	
	public Equipment(String device_sn, String name, int status, String relation, int sensorId) {
		this.device_sn = device_sn;
		this.name = name;
		this.status = status;
		this.relation = relation;
		this.sensorId = sensorId;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public int getSensorId() {
		return sensorId;
	}

	public void setSensorId(int sensorId) {
		this.sensorId = sensorId;
	}

}
