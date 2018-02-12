package com.geariot.platform.fishery.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="javaClassName")
public class SelfTest {
	private int id;
	private int path;               //第几路
	private Date createDate;         //上传时间
	private int ac;                 //有无220v交流电
	private double latitude;        //纬度
	private double longitude;       //经度
	private List<Broken> broken;       //传感器及水泵状态
	private int gprs;           //gprs强度
	private String device_sn;			//设备编号
	public String getDevice_sn() {
		return device_sn;
	}

	public void setDevice_sn(String device_sn) {
		this.device_sn = device_sn;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPath() {
		return path;
	}

	public void setPath(int path) {
		this.path = path;
	}

	

	public int getAc() {
		return ac;
	}

	public void setAc(int ac) {
		this.ac = ac;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}



	public int getGprs() {
		return gprs;
	}

	public void setGprs(int gprs) {
		this.gprs = gprs;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@OneToMany(cascade = {CascadeType.ALL},orphanRemoval=true,fetch=FetchType.EAGER)
	@JoinColumn(name="selfTestId", foreignKey=@ForeignKey(name="none"))
	public List<Broken> getBroken() {
		return broken;
	}

	public void setBroken(List<Broken> broken) {
		this.broken = broken;
	}

	

	
}
