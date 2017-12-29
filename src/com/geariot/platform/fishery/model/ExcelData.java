package com.geariot.platform.fishery.model;

import java.util.Date;

public class ExcelData {
 private String SID;
 private String device_sn;
 private String oxygen;
 private String pH_value;
 private Date receiveTime;
 private String water_temperature;
public String getSID() {
	return SID;
}
public void setSID(String sID) {
	SID = sID;
}
public String getDevice_sn() {
	return device_sn;
}
public void setDevice_sn(String device_sn) {
	this.device_sn = device_sn;
}
public String getOxygen() {
	return oxygen;
}
public void setOxygen(String oxygen) {
	this.oxygen = oxygen;
}
public String getpH_value() {
	return pH_value;
}
public void setpH_value(String pH_value) {
	this.pH_value = pH_value;
}
public Date getReceiveTime() {
	return receiveTime;
}
public void setReceiveTime(Date receiveTime) {
	this.receiveTime = receiveTime;
}
public String getWater_temperature() {
	return water_temperature;
}
public void setWater_temperature(String water_temperature) {
	this.water_temperature = water_temperature;
}
 
}
