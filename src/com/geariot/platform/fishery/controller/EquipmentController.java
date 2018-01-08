package com.geariot.platform.fishery.controller;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.geariot.platform.fishery.entities.Limit_Install;
import com.geariot.platform.fishery.entities.Timer;
import com.geariot.platform.fishery.service.EquipmentService;

@RestController
@RequestMapping(value = "/equipment")
public class EquipmentController {
	
	@Autowired
	private EquipmentService equipmentService;
	@RequestMapping(value = "/limit" , method = RequestMethod.POST)
	public Map<String,Object> setLimit(@RequestBody Limit_Install limit_Install){
		return equipmentService.setLimit(limit_Install);
	}
	
	@RequestMapping(value = "/delEquipments" , method = RequestMethod.GET)
	public Map<String,Object> delEquipment(String... device_sns){
		return equipmentService.delEquipment(device_sns);
	}
	
	@RequestMapping(value = "/timer" , method = RequestMethod.POST)
	public Map<String,Object> setTimer(@RequestBody Timer timer){
		return equipmentService.setTimer(timer);
	}
	
	@RequestMapping(value = "/query" , method = RequestMethod.GET)
	public Map<String,Object> queryEquipment(String device_sn,String relation, String name, int page, int number){
		return equipmentService.queryEquipment(device_sn,relation, name, page, number);
	}
	
	@RequestMapping(value = "/exportData" , method = RequestMethod.GET)
	public void exportData(String device_sn, String startTime, String endTime,HttpServletResponse response){
		 equipmentService.exportData(device_sn, startTime, endTime,response);
	}
	
	@RequestMapping(value = "/add" , method = RequestMethod.POST)
	public Map<String,Object> addEquipment(String device_sn,String name,String relation){
		return equipmentService.addEquipment(device_sn,name,relation);
	}
	
	@RequestMapping(value = "/realTimeData" , method = RequestMethod.GET)
	public Map<String,Object> realTimeData(String device_sn){
		return equipmentService.realTimeData(device_sn);
	}
	
	@RequestMapping(value = "/data" , method = RequestMethod.GET)
	public Map<String,Object> dataAll(String device_sn,String startTime,String endTime){
		return equipmentService.dataAll(device_sn,startTime,endTime);
	}
}
