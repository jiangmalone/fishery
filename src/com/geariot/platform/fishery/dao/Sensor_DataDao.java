package com.geariot.platform.fishery.dao;

import java.util.Date;
import java.util.List;

import com.geariot.platform.fishery.entities.Sensor_Data;
import com.geariot.platform.fishery.model.ExcelData;


public interface Sensor_DataDao {
	Sensor_Data findDataByDeviceSns(String deviceSns);
	
	List<ExcelData> getExcelData(String device_sn, Date startTime, Date endTime);
}
