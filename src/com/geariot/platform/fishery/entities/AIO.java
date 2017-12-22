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
public class AIO {

	private int id;						//一体机Id,自增
	private String device_sn;			//一体机设备编号
	private int type; 					//1,2 = 一体机(不含ph功能),一体机(含ph功能)
	private String name;				//设备名称
	private int status;					//状态(0,1,2,3,4 == 正常,离线,断电,缺相,数据异常)
	private int pondId;					//绑定的塘口Id
	private String relationId;			//绑定的用户relationId
	
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getPondId() {
		return pondId;
	}
	public void setPondId(int pondId) {
		this.pondId = pondId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getRelationId() {
		return relationId;
	}
	public void setRelationId(String relationId) {
		this.relationId = relationId;
	}
}
