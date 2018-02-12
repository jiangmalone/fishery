package com.geariot.platform.fishery.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="javaClassName")
public class Alarm {
private String deviceSn;
private Date createDate;
private int way;
private int alarmType;         //报警类型，1：增氧机缺相报警，2：220v断电报警，3：增氧机打开后半小时内效果不明显报警，4：取消所有报警
private int id;
public String getDeviceSn() {
	return deviceSn;
}
public void setDeviceSn(String deviceSn) {
	this.deviceSn = deviceSn;
}
public Date getCreateDate() {
	return createDate;
}
public void setCreateDate(Date createDate) {
	this.createDate = createDate;
}
public int getWay() {
	return way;
}
public void setWay(int way) {
	this.way = way;
}
public int getAlarmType() {
	return alarmType;
}
public void setAlarmType(int alarmType) {
	this.alarmType = alarmType;
}
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}


}
