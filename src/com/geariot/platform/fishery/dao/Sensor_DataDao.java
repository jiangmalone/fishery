package com.geariot.platform.fishery.dao;

import java.util.Date;
import java.util.List;


import com.geariot.platform.fishery.entities.Sensor_Data;
import com.geariot.platform.fishery.model.ExcelData;


public interface Sensor_DataDao {
	Sensor_Data findDataByDeviceSns(String deviceSns);
	
	Sensor_Data findDataByDeviceSnAndWay(String deviceSns,int way);
	
	List<ExcelData> getExcelData(String device_sn, Date startTime, Date endTime);
	
	void updateData(Sensor_Data sensor_Data);
	
	void save(Sensor_Data sensor_Data);
	
	List<Sensor_Data> today(String device_sn);
	
	List<Sensor_Data> sevenData(String device_sn);
}
