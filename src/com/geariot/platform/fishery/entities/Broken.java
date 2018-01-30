package com.geariot.platform.fishery.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Broken {
private int id;
private int entityModel;  //故障实体，0为水泵，1为PH，2为DO溶氧值，3为温度
private int entityType;   //故障类型，0为没故障，1为低限故障，2为高限故障，3为温度断开，4为水泵关闭状态，
                            //5为水泵打开状态，6为水泵低电流状态，7为水泵高电流状态
private Date createDate;
private String deviceSn;  //设备编号

public String getDeviceSn() {
	return deviceSn;
}
public void setDeviceSn(String deviceSn) {
	this.deviceSn = deviceSn;
}
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public int getEntityModel() {
	return entityModel;
}
public void setEntityModel(int entityModel) {
	this.entityModel = entityModel;
}
public int getEntityType() {
	return entityType;
}
public void setEntityType(int entityType) {
	this.entityType = entityType;
}
public Date getCreateDate() {
	return createDate;
}
public void setCreateDate(Date createDate) {
	this.createDate = createDate;
}



}
