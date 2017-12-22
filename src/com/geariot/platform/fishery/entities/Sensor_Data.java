package com.geariot.platform.fishery.entities;

/**
 * @author mxy940127
 *
 */

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Sensor_Data {

	private int id;								//数据记录Id,自增
	private float oxygen;						//溶氧量
	private float water_temperature;			//水温
	private float pH_value;						//ph值
	private Date receiveTime;					//上位机该条数据发送到服务器时间
	private String device_sn;					//上位机设备编号
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public float getOxygen() {
		return oxygen;
	}
	public void setOxygen(float oxygen) {
		this.oxygen = oxygen;
	}
	public float getWater_temperature() {
		return water_temperature;
	}
	public void setWater_temperature(float water_temperature) {
		this.water_temperature = water_temperature;
	}
	public float getpH_value() {
		return pH_value;
	}
	public void setpH_value(float pH_value) {
		this.pH_value = pH_value;
	}
	public Date getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}
	public String getDevice_sn() {
		return device_sn;
	}
	public void setDevice_sn(String device_sn) {
		this.device_sn = device_sn;
	}
	
}
