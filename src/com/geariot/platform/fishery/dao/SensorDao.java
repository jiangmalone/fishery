package com.geariot.platform.fishery.dao;

import java.util.List;

import com.geariot.platform.fishery.entities.Sensor;

public interface SensorDao {

	void save(Sensor sensor);

	int delete(int sensorid);
	
	void updateSensor(Sensor sensor);
	
	int delete(String deviceSns);

	Sensor findSensorById(int sensorId);
	
	Sensor findSensorByDeviceSns(String deviceSns);
	
	Sensor findSensorByDeviceSnAndWay(String deviceSn,int way);

	List<Sensor> querySensorByNameAndRelation(String relation, String name, int from, int pageSize);
	
	List<Sensor> querySensorByNameAndRelation(String relation, String name);

	long querySensorByNameAndRelationCount(String relation, String name);

	List<Sensor> findSensorsByPondId(int pondId);
	
	List<Sensor> findSensorsByRelation(String relation);

	void updateByPondId(int pondId);
	
	void deleteByRelation(String relation);
}
