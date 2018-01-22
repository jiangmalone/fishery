/**
 * 
 */
package com.geariot.platform.fishery.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.geariot.platform.fishery.model.RESCODE;
import com.geariot.platform.fishery.service.BindService;

/**
 * @author mxy940127
 *
 */
@RestController
@RequestMapping("/bind")
public class BindController {

	@Autowired
	private BindService bindService;
	
	@RequestMapping(value = "/pondWithSensorOrAIO", method = RequestMethod.GET)
	public Map<String, Object> bindPondWithSensorOrAIO(String device_sn, int pondId, int type){
		switch(type){
			case 1 : return bindService.bindPondWithSensor(device_sn, pondId);
			case 2 : return bindService.bindPondWithAIO(device_sn, pondId);
			default : return RESCODE.WRONG_PARAM.getJSONRES();
		}
	}
	
	@RequestMapping(value = "/delSensorOrAIOBind", method = RequestMethod.GET)
	public Map<String, Object> delPondWithSensorOrAIOBind(String device_sn, int type){
		switch(type){
			case 1 : return bindService.delPondWithSensorBind(device_sn);
			case 2 : return bindService.delPondWithAIOBind(device_sn);
			default : return RESCODE.WRONG_PARAM.getJSONRES();
		}
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
