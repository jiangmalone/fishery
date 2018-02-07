package com.geariot.platform.fishery.dao;

import java.util.List;

import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.Sensor;
import com.geariot.platform.fishery.entities.Sensor_Controller;
import com.geariot.platform.fishery.model.Equipment;

public interface SensorDao {

	void save(Sensor sensor);

	int delete(int sensorid);
	
	void updateSensor(Sensor sensor);
	
	int delete(String deviceSns);

	Sensor findSensorById(int sensorId);
	
	Sensor findSensorByDeviceSns(String deviceSns);

	List<Sensor> querySensorByNameAndRelation(String relation, String name, int from, int pageSize);
	
	List<Sensor> querySensorByNameAndRelation(String relation, String name);

	long querySensorByNameAndRelationCount(String relation, String name);

	List<Sensor> findSensorsByPondId(int pondId);
	
	void updateByPondId(int pondId);
	
	void deleteByRelation(String relation);
}
