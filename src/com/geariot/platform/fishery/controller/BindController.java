/**
 * 
 */
package com.geariot.platform.fishery.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.geariot.platform.fishery.dao.ControllerDao;
import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.Controller;
import com.geariot.platform.fishery.entities.Sensor;
import com.geariot.platform.fishery.model.RESCODE;
import com.geariot.platform.fishery.service.BindService;
import com.geariot.platform.fishery.service.EquipmentService;

/**
 * @author mxy940127
 *
 */
@RestController
@RequestMapping("/bind")
public class BindController {

	@Autowired
	private BindService bindService;
	
	@Autowired
	private ControllerDao controllerDao;
	
	@RequestMapping(value = "/pondWithSensor", method = RequestMethod.POST)
	public Map<String, Object> bindPondWithSensorOrAIO(Sensor sensor){	
			return bindService.bindPondWithSensor(sensor);			
	}
	
	@RequestMapping(value = "/pondWithAio", method = RequestMethod.POST)
	public Map<String, Object> bindPondWithSensorOrAIO(AIO aio){
			
		return bindService.bindPondWithAIO(aio);

	}
	
	@RequestMapping(value = "/delSensorOrAIOBind", method = RequestMethod.GET)
	public Map<String, Object> delPondWithSensorOrAIOBind(String device_sn, int type){
		switch(type){
			case 1 : return bindService.delPondWithSensorBind(device_sn);
			case 2 : return bindService.delPondWithAIOBind(device_sn);
			default : return RESCODE.WRONG_PARAM.getJSONRES();
		}
	}
	@RequestMapping(value = "/delControllerBind", method = RequestMethod.GET)
	public Map<String, Object> delControllerBind(String device_sn, int port){
		return bindService.delControllerBind(device_sn,port);
	}
	
	
	@RequestMapping(value = "/delSensorControllerBind", method = RequestMethod.GET)
	public Map<String, Object> delSensorControllerBind(int sensorId, int sensor_port){
		return bindService.delSensorControllerBind(sensorId, sensor_port);
	}
	
	@RequestMapping(value = "/bindSensorController", method = RequestMethod.GET)
	public Map<String, Object> bindSensorController(int sensorId, int sensor_port, int controllerId, int controller_port){
		return bindService.bindSensorController(sensorId, sensor_port, controllerId, controller_port);
	}
	
	@RequestMapping(value = "/bindState", method = RequestMethod.GET)
	public Map<String, Object> bindState(String device_sn){
		return bindService.bindState(device_sn);
	}
}
