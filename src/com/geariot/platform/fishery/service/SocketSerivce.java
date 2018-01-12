/**
 * 
 */
package com.geariot.platform.fishery.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.geariot.platform.fishery.dao.AlarmDao;
import com.geariot.platform.fishery.dao.LimitDao;
import com.geariot.platform.fishery.dao.SelfTestDao;
import com.geariot.platform.fishery.dao.Sensor_DataDao;
import com.geariot.platform.fishery.entities.Alarm;
import com.geariot.platform.fishery.entities.Limit_Install;
import com.geariot.platform.fishery.entities.SelfTest;
import com.geariot.platform.fishery.entities.Sensor_Data;

/**
 * @author 84111
 *
 */
@Service
@Transactional
public class SocketSerivce {

	@Autowired
	private SelfTestDao selfTestDao;
	
	@Autowired
	private LimitDao limitDao;
	
	@Autowired
	private Sensor_DataDao sensorDataDao;
	
	@Autowired
	private AlarmDao alarmDao;
	
	public void save(SelfTest selfTest) {
		selfTestDao.save(selfTest);
	}
	
	public void save(Limit_Install limit_Install) {
		limitDao.save(limit_Install);
	}
	
	public void save(Alarm alarm) {
		alarmDao.save(alarm);
	}
	
	public void update(Sensor_Data s) {
		sensorDataDao.updateData(s);
	}
	
	public void save(Sensor_Data s) {
		sensorDataDao.save(s);
	}
}
