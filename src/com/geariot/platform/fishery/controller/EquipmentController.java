package com.geariot.platform.fishery.controller;

import com.geariot.platform.fishery.entities.Timer;
import com.geariot.platform.fishery.entities.WXUser;
import com.geariot.platform.fishery.entities.controllerParam;
import com.geariot.platform.fishery.model.RESCODE;
import com.geariot.platform.fishery.service.EquipmentService;
import com.geariot.platform.fishery.timer.CMDUtils;
import com.geariot.platform.fishery.timer.TimerTask;
import com.geariot.platform.fishery.wxutils.WechatSendMessageUtils;

import cmcc.iot.onenet.javasdk.response.datapoints.DatapointsList.DatastreamsItem.DatapointsItem;
import net.sf.json.JSONObject;
import sun.invoke.empty.Empty;
import sun.util.logging.resources.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.geariot.platform.fishery.dao.LimitDao;
import com.geariot.platform.fishery.dao.WXUserDao;
import com.geariot.platform.fishery.dao.impl.WXUserDaoImpl;
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

	@Autowired
	private LimitDao limitDao;
	
	@Autowired
	private WXUserDao wxUserDao;
	

	

//	@RequestMapping(value = "/setlimit", method = RequestMethod.GET)
//	public Map<String, Object> setLimit(String devicesn,int way,String lowlimit,String highlimit,String higherlimit) {
//		//return equipmentService.setLimit(devicesn,way,lowlimit,highlimit,higherlimit);
//	}

	@RequestMapping(value = "/VertifyDevicesn",method = RequestMethod.GET)
	public Map<String, Object> VertifyDevicesn(String divsn){
		return equipmentService.VertifyDevicesn(divsn);
	}
	
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
	@RequestMapping(value = "/getControllersBydevice_sn", method = RequestMethod.POST)
	public Map<String, Object> getControllersBydevice_sn(String device_sn) {
		return equipmentService.getControllersBydevice_sn(device_sn);
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
	public Map dataToday(String device_sn,int way) {
		return equipmentService.dataToday(device_sn,way);
	}
	
	@RequestMapping(value = "/dataYesterday", method = RequestMethod.GET)
	public Map dataYesterday(String device_sn) {
		return equipmentService.dataYesterday(device_sn);
	}
	
	@RequestMapping(value = "/data3days", method = RequestMethod.GET)
	public Map<String,Object> data3days(String device_sn,int way) {
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
	
	/*@RequestMapping(value ="/adminFindEquipment", method = RequestMethod.GET)
	public Map<String, Object> adminFindEquipment(String device_sn, String userName, int page, int number){
		return equipmentService.adminFindEquipment(device_sn,userName,page,number);
	}*/
	
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
		System.out.println("设备way："+param.getWay());
		System.out.println("timer长度："+param.getTimers().length);
		//先删除
		equipmentService.delTimer(param.getDevice_sn(),  param.getWay());
		//后添加
		for(Timer timer:param.getTimers()) {
			timer.setDevice_sn(param.getDevice_sn());
			timer.setWay(param.getWay());
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
		System.out.println("设备编号:"+limit_Install.getDevice_sn());
		System.out.println("way:"+limit_Install.getWay());
		Limit_Install limit_Install2 = limitDao.findLimitByDeviceSnsAndWay(limit_Install.getDevice_sn(), limit_Install.getWay());
		if(limit_Install2 == null) {
			System.out.println("增氧限制未设置");
			limitDao.save(limit_Install);
			equipmentService.addTrigger("DO", limit_Install.getDevice_sn(), "<", limit_Install.getLow_limit(), 2,limit_Install.getWay());
		}else {
			equipmentService.deleteTriggerByDevice_snAndWay(limit_Install2.getDevice_sn(), limit_Install2.getWay());
			equipmentService.setLimit(limit_Install);
		}
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
	public int sendcmd(@RequestBody controllerParam param){
		//
		Controller controller = param.getController();
		WXUser wxUser =  wxUserDao.findUserByRelation(controller.getRelation());
		String contents = "KM"+controller.getPort()+":"+param.getKey();
		int result = CMDUtils.sendStrCmd(controller.getDevice_sn(), contents);
		if(result == 0) {//成功
			if(param.getKey() == 0) {//关闭
				/*WechatSendMessageUtils.sendWechatOxygenOnOffMessages(msg, openId, deviceSn, onOff);
				*/
				WechatSendMessageUtils.sendWechatOxyAlarmMessages("关闭增氧机成功", wxUser.getOpenId(), controller.getDevice_sn());
			}else {//打开
				WechatSendMessageUtils.sendWechatOxyAlarmMessages("打开增氧机成功", wxUser.getOpenId(), controller.getDevice_sn());
			}
		}else {
			if(param.getKey() == 0) {
				WechatSendMessageUtils.sendWechatOxyAlarmMessages("关闭增氧机失败", wxUser.getOpenId(), controller.getDevice_sn());
			}else {
				WechatSendMessageUtils.sendWechatOxyAlarmMessages("打开增氧机失败", wxUser.getOpenId(), controller.getDevice_sn());
			}
		}		
		return result;
	}
	
	@RequestMapping(value ="/refeshcondition", method = RequestMethod.GET)

	public Map<String, Object> refeshcondition(String device_sn,int port,String status){

			return  equipmentService.refeshcondition( device_sn, port, status);

	}
	
	@RequestMapping(value ="/analysisData", method = RequestMethod.GET)
	public List<Map<String, Object>> analysisData(String relation){
		return  equipmentService.getPersonalDianosing(relation);
	}
	
	@RequestMapping(value ="/deleteTrigger", method = RequestMethod.GET)
	public void deleteTrigger(String device_sn){
		  equipmentService.deleteTriggerBySensorId(device_sn);
	}
	
	@RequestMapping(value ="/sendWechatmessage", method = RequestMethod.GET)
	public void sendWechatmessage(String device_sn){
		WechatSendMessageUtils.sendWechatOxyAlarmMessages("关闭增氧机成功", "owhQb0VMYu9F8ABxq4RQ38yx_mHc", device_sn);
	}
	
	@RequestMapping(value ="/getAllController", method = RequestMethod.GET)
	public List<Controller> getAllController(){
		System.out.println(equipmentService.getAllControllers().size());
		return equipmentService.getAllControllers();
	}
	
}
