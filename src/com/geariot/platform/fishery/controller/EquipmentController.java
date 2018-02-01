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
import com.geariot.platform.fishery.model.ParamBody;
import com.geariot.platform.fishery.service.EquipmentService;
import com.geariot.platform.fishery.socket.CMDUtils;

@RestController
@RequestMapping(value = "/equipment")
public class EquipmentController {
 
	@Autowired
	private EquipmentService equipmentService;

	/*@RequestMapping(value = "/limit", method = RequestMethod.POST)
	public Map<String, Object> setLimit(@RequestBody Limit_Install limit_Install) {
		return equipmentService.setLimit(limit_Install);
	}*/

	@RequestMapping(value = "/delEquipments", method = RequestMethod.GET)
	public Map<String, Object> delEquipment(String... device_sns) {
		return equipmentService.delEquipment(device_sns);
	}

	/*@RequestMapping(value = "/timer", method = RequestMethod.POST)
	public Map<String, Object> setTimer(@RequestBody Timer... timer) {
		return equipmentService.setTimer(timer);
	}*/

	/*@RequestMapping(value = "/query", method = RequestMethod.GET)
	public Map<String, Object> queryEquipment(String device_sn, String relation, String name, int page, int number) {
		return equipmentService.queryEquipment(device_sn, relation, name, page, number);
	}*/

	@RequestMapping(value = "/exportData", method = RequestMethod.GET)
	public void exportData(String device_sn, String startTime, String endTime, HttpServletResponse response) {
		equipmentService.exportData(device_sn, startTime, endTime, response);
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public Map<String, Object> addEquipment(String device_sn, String name, String relation) {
		return equipmentService.addEquipment(device_sn, name, relation);
	}

	@RequestMapping(value = "/realTimeData", method = RequestMethod.GET)
	public Map<String, Object> realTimeData(String device_sn,int way) {
		return equipmentService.realTimeData(device_sn, way);
	}

	@RequestMapping(value = "/dataToday", method = RequestMethod.GET)
	public Map<String, Object> dataToday(String device_sn,int way) {
		return equipmentService.dataToday(device_sn,way);
	}
	
	@RequestMapping(value = "/dataAll", method = RequestMethod.GET)
	public Map<String, Object> dataAll(String device_sn, int way) {
		return equipmentService.dataAll(device_sn,way);
	}
	
	@RequestMapping(value = "/pc/dataToday", method = RequestMethod.GET)
	public Map<String, Object> pcDataToday(String device_sn, int way) {
		return equipmentService.pcDataToday(device_sn, way);
	}
	
	@RequestMapping(value = "/pc/dataAll", method = RequestMethod.GET)
	public Map<String, Object> pcDataAll(String device_sn, int way) {
		return equipmentService.pcDataAll(device_sn, way);
	}
	
	@RequestMapping(value = "/myEquipment", method = RequestMethod.GET)
	public Map<String, Object> myEquipment(String relation){
		return equipmentService.myEquipment(relation);
	}
	
	@RequestMapping(value ="/adminFindEquipment", method = RequestMethod.GET)
	public Map<String, Object> adminFindEquipment(String device_sn, String userName, int page, int number){
		return equipmentService.adminFindEquipment(device_sn,userName,page,number);
	}
	
	@RequestMapping(value ="/companyFindEquipment", method = RequestMethod.GET)
	public Map<String, Object> companyFindEquipment(String device_sn, String relation, int page, int number){
		return equipmentService.companyFindEquipment(device_sn, relation, page, number);
	}
	
	@RequestMapping(value = "/autoSet", method = RequestMethod.POST)
	public Map<String, Object> autoSet(@RequestBody ParamBody body){
		return equipmentService.autoSet(body.getLimit_Install(), body.getTimers());
	}
	
	//服务器设置校准
	@RequestMapping(value="/serverCheck",method=RequestMethod.GET)
	public Map<String,Object> serverCheck(String device_sn,int way){
		return CMDUtils.serverCheckCMD(device_sn,way);
	}
	
	//增氧机开关
	@RequestMapping(value="/aeratorOnOff",method=RequestMethod.GET)
	public Map<String,Object> aeratorOnOff(String device_sn,int way,int openOrclose){
		return CMDUtils.serverOnOffOxygenCMD(device_sn,way,openOrclose);
	}
	
	@RequestMapping(value ="/queryAeratorData", method = RequestMethod.GET)
	public Map<String, Object> queryAeratorData(String device_sn,int way){
		return equipmentService.queryAeratorData(device_sn,way);
	}
}
