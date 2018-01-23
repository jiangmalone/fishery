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
import com.geariot.platform.fishery.socket.CMDUtils;

@RestController
@RequestMapping(value = "/equipment")
public class EquipmentController {
 
	@Autowired
	private EquipmentService equipmentService;

	@RequestMapping(value = "/limit", method = RequestMethod.POST)
	public Map<String, Object> setLimit(@RequestBody Limit_Install limit_Install) {
		return equipmentService.setLimit(limit_Install);
	}

	@RequestMapping(value = "/delEquipments", method = RequestMethod.GET)
	public Map<String, Object> delEquipment(String... device_sns) {
		return equipmentService.delEquipment(device_sns);
	}

	@RequestMapping(value = "/timer", method = RequestMethod.POST)
	public Map<String, Object> setTimer(@RequestBody Timer... timer) {
		return equipmentService.setTimer(timer);
	}

	/*@RequestMapping(value = "/query", method = RequestMethod.GET)
	public Map<String, Object> queryEquipment(String device_sn, String relation, String name, int page, int number) {
		return equipmentService.queryEquipment(device_sn, relation, name, page, number);
	}*/

	@RequestMapping(value = "/exportData", method = RequestMethod.GET)
	public void exportData(String device_sn, String startTime, String endTime, HttpServletResponse response) {
		equipmentService.exportData(device_sn, startTime, endTime, response);
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public Map<String, Object> addEquipment(String device_sn, String name, String relationId) {
		return equipmentService.addEquipment(device_sn, name, relationId);
	}

	@RequestMapping(value = "/realTimeData", method = RequestMethod.GET)
	public Map<String, Object> realTimeData(String device_sn) {
		return equipmentService.realTimeData(device_sn);
	}

	@RequestMapping(value = "/dataToday", method = RequestMethod.GET)
	public Map<String, Object> dataToday(String device_sn) {
		return equipmentService.dataToday(device_sn);
	}
	
//	@RequestMapping(value = "/data", method = RequestMethod.GET)
//	public Map<String, Object> dataAll(String device_sn, String startTime, String endTime) {
//		return equipmentService.dataAll(device_sn, startTime, endTime);
//	}
	
	@RequestMapping(value = "/myEquipment", method = RequestMethod.GET)
	public Map<String, Object> myEquipment(String relationId){
		return equipmentService.myEquipment(relationId);
	}
	
	@RequestMapping(value ="/adminFindEquipment", method = RequestMethod.GET)
	public Map<String, Object> adminFindEquipment(String device_sn, String companyName, int page, int number){
		return equipmentService.adminFindEquipment(device_sn,companyName,page,number);
	}
	
	@RequestMapping(value ="/companyFindEquipment", method = RequestMethod.GET)
	public Map<String, Object> companyFindEquipment(String device_sn, String relationId, int page, int number){
		return equipmentService.companyFindEquipment(device_sn, relationId, page, number);
	}
	
	//服务器设置校准
	@RequestMapping(value="/serverCheck",method=RequestMethod.GET)
	public Map<String,Object> serverCheck(String device_sn){
		return CMDUtils.serverCheckCMD(device_sn);
	}
}
