package com.geariot.platform.fishery.entities;

/**
 * @author mxy940127
 *
 */

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Sensor {

	private int id;							//传感器Id,自增
	private String device_sn;				//设备编号
	private int pondId;						//绑定的塘口Id
	private int status;						//状态(0,1,2,3,4 == 正常,离线,断电,缺相,数据异常)
	private String name;					//传感器名称,可自己定义
	private String relationId;				//绑定的用户relationId
	private String port_status;				//表示传感器两路绑定状态, 如01 00 10 11等
	
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
	public String getRelationId() {
		return relationId;
	}
	public void setRelationId(String relationId) {
		this.relationId = relationId;
	}
	public String getPort_status() {
		return port_status;
	}
	public void setPort_status(String port_status) {
		this.port_status = port_status;
	}
}
