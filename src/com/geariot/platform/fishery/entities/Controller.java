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
public class Controller {

	private int id;						//控制器Id,自增
	private String device_sn;			//控制器设备编号
	private int pondId;					//绑定的塘口Id
	private String relation;			//绑定的用户relation
	private String name;				//控制器名称
	private int status;					//状态(0,1,2,3,4 == 正常,离线,断电,缺相,数据异常)
	private String port_status;  		//表示传感器四路绑定状态, 如0101 0110 1100 1111等
	
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
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
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
	public String getPort_status() {
		return port_status;
	}
	public void setPort_status(String port_status) {
		this.port_status = port_status;
	}
	
	
}
