package com.geariot.platform.fishery.dao;

import com.geariot.platform.fishery.entities.DataAlarm;

public interface DataAlarmDao {
	
	void save(DataAlarm dataAlarm);

	DataAlarm findDataAlarmById(int id);

	DataAlarm findDataAlarmByRelation(String relation);
	
	void updateStatus(DataAlarm da);
}
