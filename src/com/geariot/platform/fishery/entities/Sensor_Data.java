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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="javaClassName")
public class Sensor_Data {
	private int id;								//数据记录Id,自增
	private String device_sn;					//上位机设备编号
	private float DO;						//溶氧量
	private float WT;			//水温
	private float pH;	                    //ph值
	private float DOS;                     //溶氧饱和值
	private Date receiveTime;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDevice_sn() {
		return device_sn;
	}
	public void setDevice_sn(String device_sn) {
		this.device_sn = device_sn;
	}
	public float getDO() {
		return DO;
	}
	public void setDO(float dO) {
		DO = dO;
	}
	public float getWT() {
		return WT;
	}
	public void setWT(float wT) {
		WT = wT;
	}
	public float getpH() {
		return pH;
	}
	public void setpH(float pH) {
		this.pH = pH;
	}
	public float getDOS() {
		return DOS;
	}
	public void setDOS(float dOS) {
		DOS = dOS;
	}
	public Date getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}						
}
