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
public class WT {
	private int id;
	private String device_sn;
	private float value;
	private Date date;
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
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
