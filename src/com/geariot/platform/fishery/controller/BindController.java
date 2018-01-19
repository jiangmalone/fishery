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
	public Map<String, Object> bindPondWithSensorOrAIO(int id, int pondId, int type){
		switch(type){
			case 1 : return bindService.bindPondWithSensor(id, pondId);
			case 2 : return bindService.bindPondWithAIO(id, pondId);
			default : return RESCODE.WRONG_PARAM.getJSONRES();
		}
	}
	
	@RequestMapping(value = "/delSensorOrAIOBind", method = RequestMethod.GET)
	public Map<String, Object> delPondWithSensorOrAIOBind(int id, int type){
		switch(type){
			case 1 : return bindService.delPondWithSensorBind(id);
			case 2 : return bindService.delPondWithAIOBind(id);
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
}
