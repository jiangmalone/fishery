/**
 * 
 */
package com.geariot.platform.fishery.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.geariot.platform.fishery.dao.AIODao;
import com.geariot.platform.fishery.dao.AlarmDao;
import com.geariot.platform.fishery.dao.BrokenDao;
import com.geariot.platform.fishery.dao.ControllerDao;
import com.geariot.platform.fishery.dao.LimitDao;
import com.geariot.platform.fishery.dao.SelfTestDao;
import com.geariot.platform.fishery.dao.SensorDao;
import com.geariot.platform.fishery.dao.Sensor_DataDao;
import com.geariot.platform.fishery.dao.TimerDao;
import com.geariot.platform.fishery.dao.WXUserDao;
import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.Alarm;
import com.geariot.platform.fishery.entities.Broken;
import com.geariot.platform.fishery.entities.Controller;
import com.geariot.platform.fishery.entities.Limit_Install;
import com.geariot.platform.fishery.entities.SelfTest;
import com.geariot.platform.fishery.entities.Sensor;
import com.geariot.platform.fishery.entities.Sensor_Data;
import com.geariot.platform.fishery.entities.Timer;
import com.geariot.platform.fishery.entities.WXUser;

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
	
	@Autowired
	private SensorDao sensorDao;
	
	@Autowired
	private BrokenDao brokenDao;
	
	@Autowired
	private WXUserDao wxuserDao;
	
	@Autowired
	private TimerDao timerDao;
	
	@Autowired
	private AIODao aioDao;
	
	@Autowired
	private ControllerDao controllerDao;
	
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
	
	public Sensor findSensorByDeviceSn(String deviceSn) {
		return sensorDao.findSensorByDeviceSns(deviceSn);
	}
	
	public WXUser findWXUserById(String relation) {
		return wxuserDao.findUserById(Integer.parseInt(relation.substring(2)));
	}
	
	public void save(Broken broken) {
		brokenDao.save(broken);
	}
	
	public List<Timer> findAllTimer(){
		return timerDao.findAllTimer();
	}
	
	public void updateAIO(AIO aio) {
		aioDao.update(aio);
	}
	
	public void updateSensor(Sensor sensor) {
		sensorDao.updateSensor(sensor);
	}
	
	public void updateController(Controller controller) {
		controllerDao.updateController(controller);
	}
}
