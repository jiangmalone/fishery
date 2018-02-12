package com.geariot.platform.fishery.entities;

/**
 * @author mxy940127
 *
 */

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="javaClassName")
public class Limit_Install {

	private int id;						//数据库记录Id(自增)
	private float low_limit;			//溶氧下限
	private float up_limit;				//溶氧上限
	private float high_limit;			//溶氧高限
	private String device_sn;			//设备编号
	private int way;                    //第几路
	public int getWay() {
		return way;
	}
	public void setWay(int way) {
		this.way = way;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public float getLow_limit() {
		return low_limit;
	}
	public void setLow_limit(float low_limit) {
		this.low_limit = low_limit;
	}
	public float getUp_limit() {
		return up_limit;
	}
	public void setUp_limit(float up_limit) {
		this.up_limit = up_limit;
	}
	public float getHigh_limit() {
		return high_limit;
	}
	public void setHigh_limit(float high_limit) {
		this.high_limit = high_limit;
	}
	public String getDevice_sn() {
		return device_sn;
	}
	public void setDevice_sn(String device_sn) {
		this.device_sn = device_sn;
	}

}
