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
public class Sensor_Controller {

	private int id;						//数据库记录Id(自增)
	private int sensorId;				//传感器Id
	private int sensor_port;			//传感器端口Id
	private int controllerId;			//控制器Id
	private int controller_port;		//控制器端口Id
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSensorId() {
		return sensorId;
	}
	public void setSensorId(int sensorId) {
		this.sensorId = sensorId;
	}
	public int getSensor_port() {
		return sensor_port;
	}
	public void setSensor_port(int sensor_port) {
		this.sensor_port = sensor_port;
	}
	public int getControllerId() {
		return controllerId;
	}
	public void setControllerId(int controllerId) {
		this.controllerId = controllerId;
	}
	public int getController_port() {
		return controller_port;
	}
	public void setController_port(int controller_port) {
		this.controller_port = controller_port;
	}
	
}
