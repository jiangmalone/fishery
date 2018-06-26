package com.geariot.platform.fishery.controller;

import com.geariot.platform.fishery.entities.Timer;
import com.geariot.platform.fishery.entities.controllerParam;
import com.geariot.platform.fishery.model.RESCODE;
import com.geariot.platform.fishery.service.EquipmentService;
import com.geariot.platform.fishery.timer.CMDUtils;

import net.sf.json.JSONObject;
import sun.invoke.empty.Empty;
import sun.util.logging.resources.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.geariot.platform.fishery.dao.LimitDao;
import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.Controller;
import com.geariot.platform.fishery.entities.Limit_Install;
import com.geariot.platform.fishery.entities.Param;
import com.geariot.platform.fishery.entities.Sensor;

import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

//import com.geariot.platform.fishery.socket.CMDUtils;

@RestController
@RequestMapping(value = "/equipment")
public class EquipmentController {
 
	@Autowired
	private EquipmentService equipmentService;


//	@RequestMapping(value = "/setlimit", method = RequestMethod.GET)
//	public Map<String, Object> setLimit(String devicesn,int way,String lowlimit,String highlimit,String higherlimit) {
//		//return equipmentService.setLimit(devicesn,way,lowlimit,highlimit,higherlimit);
//	}

	@RequestMapping(value = "/changeControllerWayOnoff",method = RequestMethod.GET)
	public void changeControllerWayOnoff(String divsn, int way ,int key){
		equipmentService.changeControllerWayOnoff(divsn,way,key);
	}


	@RequestMapping(value = "/delEquipments", method = RequestMethod.GET)
	public Map<String, Object> delEquipment(String  device_sn) {
		return equipmentService.delEquipment(device_sn);
	}
	
	@RequestMapping(value = "/modifyEquipment", method = RequestMethod.GET)
	public Map<String, Object> modifyEquipment(String device_sn,String name) {
		return equipmentService.modifyEquipment(device_sn,name);
	}

	@RequestMapping(value = "/addSensor", method = RequestMethod.POST)
	public Map<String, Object> addEquipment(@RequestBody Sensor... sensors) {
		return equipmentService.addSensor(sensors);
	}
	@RequestMapping(value = "/modifySensor", method = RequestMethod.POST)
	public Map<String, Object> modifySensor(@RequestBody Sensor...sensors) {
		return equipmentService.modifySensor(sensors);
	}

	@RequestMapping(value = "/addController", method = RequestMethod.POST)
	public Map<String, Object> addController(@RequestBody Controller... controllers) {
		return equipmentService.addController(controllers);
	}
	
	@RequestMapping(value = "/modifyController", method = RequestMethod.POST)
	public Map<String, Object> modifyController(@RequestBody Controller... controllers) {
		return equipmentService.modifyController(controllers);
	}
	
	@RequestMapping(value = "/addAio", method = RequestMethod.POST)
	public Map<String, Object> addController(@RequestBody AIO... aios) {
		return equipmentService.addAio(aios);
	}
	
	@RequestMapping(value = "/modifyAio", method = RequestMethod.POST)
	public Map<String, Object> modifyAio(@RequestBody AIO... aios) {
		return equipmentService.modifyAio(aios);
	}

	@RequestMapping(value = "/realTimeData", method = RequestMethod.GET)
	public Sensor realTimeData(String device_sn) {
		return equipmentService.realTimeData(device_sn);
	}

	@RequestMapping(value = "/dataToday", method = RequestMethod.GET)
	public String dataToday(String device_sn,int way) {
		return equipmentService.dataToday(device_sn,way);
	}
	
	@RequestMapping(value = "/data3days", method = RequestMethod.GET)
	public String data3days(String device_sn,int way) {
		return equipmentService.data3days(device_sn,way);
	}
	
	@RequestMapping(value = "/dataAll", method = RequestMethod.GET)
	public Map<String, Object> dataAll(String device_sn, int way) {
		return equipmentService.dataAll(device_sn,way,3);
	}
	
	@RequestMapping(value = "/pc/dataToday", method = RequestMethod.GET)
	public Map<String, Object> pcDataToday(String device_sn, int way) {
		return equipmentService.pcDataToday(device_sn, way);
	}
	
	@RequestMapping(value = "/pc/dataAll", method = RequestMethod.GET)
	public Map<String, Object> pcDataAll(String device_sn, int way) {
		return equipmentService.pcDataAll(device_sn, way,7);
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
	
	/*@RequestMapping(value = "/addTimer", method = RequestMethod.POST)
	public Map<String, Object> autoSet(@RequestBody Timer...timers){
		for(Timer timer:timers) {
			equipmentService.addTimer(timer);
		}
		return RESCODE.SUCCESS.getJSONRES();
	}
	
	@RequestMapping(value = "/modifyTimer", method = RequestMethod.POST)
	public Map<String, Object> modifySet(@RequestBody Timer...timers){
		equipmentService.
		for(Timer timer:timers) {
			String str = timer.getId()+"";
			if(str.equals("")) {
				equipmentService.addTimer(timer);
			}else {
				equipmentService.modifyTimer(timer);
			}
		}
		return RESCODE.SUCCESS.getJSONRES();
	}*/
	
	@RequestMapping(value = "/setTimer", method = RequestMethod.POST)
	public Map<String, Object> autoSet(@RequestBody Param param){
		System.out.println("设备id："+param.getDevice_sn());
		System.out.println("设备id："+param.getWay());
		System.out.println("timer长度："+param.getTimers().length);
		//先删除
		equipmentService.delTimer(param.getDevice_sn(),  param.getWay());
		//后添加
		for(Timer timer:param.getTimers()) {
			equipmentService.addTimer(timer);
		}
		return RESCODE.SUCCESS.getJSONRES();		
	}
	
	@RequestMapping(value = "/checkTimer", method = RequestMethod.POST)
	public Integer checkSet(@RequestBody Controller controller){
		return equipmentService.checkTimer(controller.getDevice_sn(),  controller.getPort());
		
	}
	
	@RequestMapping(value = "/delTimer", method = RequestMethod.POST)
	public Map<String, Object> delSet(@RequestBody Controller controller){
		equipmentService.delTimer(controller.getDevice_sn(),  controller.getPort());
		return RESCODE.SUCCESS.getJSONRES();
	}
	
	
	@RequestMapping(value = "/setLimit", method = RequestMethod.POST)
	public Map<String, Object> setLimit(@RequestBody Limit_Install limit_Install){
		equipmentService.setLimit(limit_Install);
		return RESCODE.SUCCESS.getJSONRES();
	}
	
	@RequestMapping(value = "/getLimit", method = RequestMethod.GET)
	public List<Limit_Install> getLimit(String device_sn){
		 List<Limit_Install> limitList = equipmentService.queryLimitByDeviceSn(device_sn);
		 return limitList;
	}
	
	@RequestMapping(value ="/triggeractive", method = RequestMethod.POST)
	public void alarmIsRead(@RequestBody JSONObject data){
		equipmentService.triggeractive(data);
	}
	
	@RequestMapping(value ="/sendcmd", method = RequestMethod.POST)
	public Map<String, Object> sendcmd(@RequestBody controllerParam param){
		//
		Controller controller = param.getController();
		String contents = "KM"+controller.getPort()+":"+param.getKey();
		CMDUtils.sendStrCmd(controller.getDevice_sn(), contents);
		
		return RESCODE.SUCCESS.getJSONRES();
	}
	
}
