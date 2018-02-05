package com.geariot.platform.fishery.dao;

import java.util.List;

import com.geariot.platform.fishery.entities.AlarmMessage;

public interface AlarmMessageDao {

	
	void save(AlarmMessage am);
	
	List<AlarmMessage> queryAlarmMessageByDeviceSn(String deviceSn);
	
	void updateStatus(AlarmMessage am);
}
