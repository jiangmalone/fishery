package com.geariot.platform.fishery.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AlarmMessage {
private int id;
private String deviceSn;
private String message;
private Date createDate;
private boolean isWatch;
private int alarmType;//报警类型，0代表溶氧值，1代表温度，2代表PH


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getDeviceSn() {
	return deviceSn;
}
public void setDeviceSn(String deviceSn) {
	this.deviceSn = deviceSn;
}
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}
public Date getCreateDate() {
	return createDate;
}
public void setCreateDate(Date createDate) {
	this.createDate = createDate;
}
public boolean isWatch() {
	return isWatch;
}
public void setWatch(boolean isWatch) {
	this.isWatch = isWatch;
}
public int getAlarmType() {
	return alarmType;
}
public void setAlarmType(int alarmType) {
	this.alarmType = alarmType;
}


}
