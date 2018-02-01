package com.geariot.platform.fishery.dao;

import java.util.List;

import com.geariot.platform.fishery.entities.Sensor_Controller;

public interface Sensor_ControllerDao {
	
	int delete(int sensorId);
	
	Sensor_Controller findBySensorIdAndPort(int sensorId, int port);
	
	void deleteRecord(int id);
	
	void save(Sensor_Controller sensor_Controller);
	
	Sensor_Controller findByControllerIdAndPort(int controllerId, int controller_port);
	
	List<Sensor_Controller> list(int sensorId);
	
	List<Sensor_Controller> controller(int controllerId);
	
	void deleteController(int controllerId);
}
