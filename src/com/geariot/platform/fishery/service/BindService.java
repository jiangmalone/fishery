/**
 * 
 */
package com.geariot.platform.fishery.service;

import java.util.Map;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.geariot.platform.fishery.dao.AIODao;
import com.geariot.platform.fishery.dao.PondDao;
import com.geariot.platform.fishery.dao.SensorDao;
import com.geariot.platform.fishery.dao.Sensor_ControllerDao;
import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.Pond;
import com.geariot.platform.fishery.entities.Sensor;
import com.geariot.platform.fishery.model.RESCODE;

/**
 * @author mxy940127
 *
 */
@Service
@Transactional
public class BindService {

	private Logger logger = LogManager.getLogger(BindService.class);

	@Autowired
	private PondDao pondDao;
	
	@Autowired
	private SensorDao sensorDao;
	
	@Autowired
	private AIODao aioDao;
	
	@Autowired
	private Sensor_ControllerDao sensor_ControllerDao;
	
	public Map<String, Object> bindPondWithSensor(String device_sn, int pondId) {
		logger.debug("塘口Id:"+pondId+"尝试与传感器设备,编号为:"+device_sn+"进行绑定...");
		Pond pond = pondDao.findPondByPondId(pondId);
		if(pond == null){
			logger.debug("塘口Id:"+pondId+"在数据库中无记录!!!");
			return RESCODE.NOT_FOUND.getJSONRES();
		}else{
			Sensor sensor = sensorDao.findSensorByDeviceSns(device_sn);
			if(sensor == null){
				logger.debug("传感器设备,编号:"+device_sn+"在数据库中无记录!!!");
				return RESCODE.NOT_FOUND.getJSONRES();
			}else{
				sensor.setPondId(pondId);
				logger.debug("塘口Id:"+pondId+"与传感器设备,编号为:"+device_sn+"绑定成功。。。");
				return RESCODE.SUCCESS.getJSONRES();
			}
		}
	}

	public Map<String, Object> bindPondWithAIO(String device_sn, int pondId) {
		logger.debug("塘口Id:"+pondId+"尝试与一体机设备,编号为:"+device_sn+"进行绑定...");
		Pond pond = pondDao.findPondByPondId(pondId);
		if(pond == null){
			logger.debug("塘口Id:"+pondId+"在数据库中无记录!!!");
			return RESCODE.NOT_FOUND.getJSONRES();
		}else{
			AIO aio = aioDao.findAIOByDeviceSns(device_sn);
			if(aio == null){
				logger.debug("一体机设备,编号:"+device_sn+"在数据库中无记录!!!");
				return RESCODE.NOT_FOUND.getJSONRES();
			}else{
				aio.setPondId(pondId);
				logger.debug("塘口Id:"+pondId+"与一体机设备,编号为:"+device_sn+"绑定成功。。。");
				return RESCODE.SUCCESS.getJSONRES();
			}
		}
	}

	public Map<String, Object> delPondWithSensorBind(String device_sn) {
		logger.debug("传感器设备,编号:"+device_sn+"尝试与相关塘口解除绑定...");
		Sensor sensor = sensorDao.findSensorByDeviceSns(device_sn);
		if(sensor == null){
			logger.debug("传感器设备,编号:"+device_sn+"在数据库中无记录!!!");
			return RESCODE.NOT_FOUND.getJSONRES();
		}else{
			if(sensor.getPondId() == 0){
				logger.debug("传感器设备,编号:"+device_sn+"并未与任何塘口有绑定");
				return RESCODE.NOT_BINDED.getJSONRES();
			}else{
				logger.debug("传感器设备,编号:"+device_sn+"与塘口,Id:"+sensor.getPondId()+"有绑定");
				sensor.setPondId(0);
				sensor.setPort_status("00");
				int count = sensor_ControllerDao.delete(sensor.getId());
				logger.debug("传感器设备,编号:"+device_sn+"已和塘口解除绑定,数据库中删除传感器与控制器绑定关系共"+count+"条。。。");
				return RESCODE.SUCCESS.getJSONRES();
			}
		}
	}

	public Map<String, Object> delPondWithAIOBind(String device_sn) {
		logger.debug("一体机设备,编号:"+device_sn+"尝试与相关塘口解除绑定...");
		AIO aio = aioDao.findAIOByDeviceSns(device_sn);
		if(aio == null){
			logger.debug("一体机设备,编号:"+device_sn+"在数据库中无记录!!!");
			return RESCODE.NOT_FOUND.getJSONRES();
		}else{
			if(aio.getPondId() == 0){
				logger.debug("一体机设备,编号:"+device_sn+"并未与任何塘口有绑定");
				return RESCODE.NOT_BINDED.getJSONRES();
			}else{
				logger.debug("一体机设备,编号:"+device_sn+"与塘口,Id:"+aio.getPondId()+"有绑定");
				aio.setPondId(0);
				logger.debug("一体机设备,编号:"+device_sn+"已和塘口解除绑定。。。");
				return RESCODE.SUCCESS.getJSONRES();
			}
		}
	}
	
	
}
