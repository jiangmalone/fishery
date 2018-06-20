package com.geariot.platform.fishery.entities;

/**
 * @author mxy940127
 *
 */

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="javaClassName")
public class Sensor {

	private int id;							//传感器Id,自增
	private String device_sn;				//设备编号
	private int pondId;						//绑定的塘口Id
	/*private int status;						//状态(0,1,2,3,4 == 正常,离线,断电,缺相,数据异常)
*/	//状态(0,1,2,3,4,5,6,0对应正常,(1,2)|(3,4)|(5,6)对应DO、WT、pH的(预警，危险))
	private int status;						
	private String name;					//传感器名称,可自己定义
	private String relation;				//绑定的用户relation
	private String port_status;				//表示传感器两路绑定状态, 如01 00 10 11等
	
	
	//以下这些字段均用@Transient注解了
	private float oxygen;						//溶氧量
	private float water_temperature;			//水温
	private float pH_value;						//ph值
	private int wayStatus;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	public int getPondId() {
		return pondId;
	}
	public void setPondId(int pondId) {
		this.pondId = pondId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}
	public String getPort_status() {
		return port_status;
	}
	public void setPort_status(String port_status) {
		this.port_status = port_status;
	}
	@Transient
	public float getOxygen() {
		return oxygen;
	}
	public void setOxygen(float oxygen) {
		this.oxygen = oxygen;
	}
	@Transient
	public float getWater_temperature() {
		return water_temperature;
	}
	public void setWater_temperature(float water_temperature) {
		this.water_temperature = water_temperature;
	}
	@Transient
	public float getpH_value() {
		return pH_value;
	}
	public void setpH_value(float pH_value) {
		this.pH_value = pH_value;
	}
	@Transient
	public int getWayStatus() {
		return wayStatus;
	}
	public void setWayStatus(int wayStatus) {
		this.wayStatus = wayStatus;
	}
}
