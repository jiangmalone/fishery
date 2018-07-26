package com.geariot.platform.fishery.controller;

import com.geariot.platform.fishery.entities.Timer;
import com.geariot.platform.fishery.entities.WXUser;
import com.geariot.platform.fishery.entities.controllerParam;
import com.geariot.platform.fishery.model.RESCODE;
import com.geariot.platform.fishery.service.EquipmentService;
import com.geariot.platform.fishery.service.UserService;
import com.geariot.platform.fishery.timer.CMDUtils;
import com.geariot.platform.fishery.timer.TimerTask;
import com.geariot.platform.fishery.wxutils.WeChatOpenIdExchange;
import com.geariot.platform.fishery.wxutils.WechatSendMessageUtils;

import cmcc.iot.onenet.javasdk.response.datapoints.DatapointsList.DatastreamsItem.DatapointsItem;
import net.sf.json.JSONObject;
import sun.invoke.empty.Empty;
import sun.util.logging.resources.logging;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.geariot.platform.fishery.dao.ControllerDao;
import com.geariot.platform.fishery.dao.LimitDao;
import com.geariot.platform.fishery.dao.SensorDao;
import com.geariot.platform.fishery.dao.WXUserDao;
import com.geariot.platform.fishery.dao.impl.WXUserDaoImpl;
import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.Controller;
import com.geariot.platform.fishery.entities.Limit_Install;
import com.geariot.platform.fishery.entities.Param;
import com.geariot.platform.fishery.entities.Sensor;

import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import com.geariot.platform.fishery.socket.CMDUtils;

@RestController
@RequestMapping(value = "/equipment")
public class EquipmentController {
	private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(EquipmentController.class);
	@Autowired
	private EquipmentService equipmentService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private LimitDao limitDao;
	
	@Autowired
	private WXUserDao wxUserDao;
	
	@Autowired
	private ControllerDao controllerDao;
	
	@Autowired
	private SensorDao sensorDao;
	
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
	
/*	@RequestMapping(value = "/dataAll", method = RequestMethod.GET)
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
	*/
	@RequestMapping(value = "/myEquipment", method = RequestMethod.GET)
	public Map<String, Object> myEquipment(String relation){
		return equipmentService.myEquipment(relation);
	}	
	
	@RequestMapping(value ="/companyFindEquipment", method = RequestMethod.GET)
	public Map<String, Object> companyFindEquipment(String device_sn, String relation, int page, int number){
		return equipmentService.companyFindEquipment(device_sn, relation, page, number);
	}
	
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
		WXUser wxUser = wxUserDao.findUserByRelation(controller.getRelation());
		String publicOpenID = userService.getPublicOpenId(wxUser.getOpenId());
		WechatSendMessageUtils.sendWechatOnOffMessages("设备取消自动", publicOpenID, controller.getDevice_sn());
		equipmentService.delTimer(controller.getDevice_sn(),  controller.getPort());
		return RESCODE.SUCCESS.getJSONRES();
	}
	
	@RequestMapping(value = "/delLimit", method = RequestMethod.POST)
	public Map<String, Object> delLimit(@RequestBody Controller controller){
		equipmentService.delLimit(controller.getDevice_sn(),  controller.getPort());
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
			List<Controller> controllerList = controllerDao.findControllerByDeviceSnAndWay(limit_Install.getDevice_sn(), limit_Install.getWay());
			List<Sensor> sensorList = sensorDao.findSensorsByRelation(controllerList.get(0).getRelation());
			for(Controller controller:controllerList) {
				int pondId =controller.getPondId();
				for(Sensor sensor:sensorList) {
					if(sensor.getPondId() == pondId) {
						equipmentService.addTrigger("DO", sensor.getDevice_sn(), "<", limit_Install.getLow_limit(), 2,limit_Install.getWay());
						equipmentService.addTrigger("DO", sensor.getDevice_sn(), ">", limit_Install.getUp_limit(), 2,limit_Install.getWay());
					}
				}
			}			
		}else {
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
		String[] type = {"增氧机","投饵机","打水机","其他"};
		Controller controller = param.getController();
		WXUser wxUser =  wxUserDao.findUserByRelation(controller.getRelation());		
		String contents = "KM"+(controller.getPort()+1)+":"+param.getKey();
		long start = System.currentTimeMillis();
		int result = CMDUtils.sendStrCmd(controller.getDevice_sn(), contents);
		long stop = System.currentTimeMillis();
		System.out.println("程序执行时间："+(stop-start));
		logger.debug("程序执行时间："+(stop-start));
		String publicOpenID = userService.getPublicOpenId(wxUser.getOpenId());
		System.out.println("openId:"+wxUser.getOpenId());
		System.out.println("向设备"+controller.getDevice_sn()+"发送命令，将结果推送至微信用户"+publicOpenID);
		String controllertype = "";
		switch (controller.getType()){
			case 0:
				controllertype = type[0];
				break;
			case 1:
				controllertype = type[1];
				break;
			case 2:
				controllertype = type[2];
				break;
			case 3:
				controllertype = type[3];
				break;
		}
		if(result == 0) {//成功
			if(param.getKey() == 0) {//关闭
				/*WechatSendMessageUtils.sendWechatOxygenOnOffMessages(msg, openId, deviceSn, onOff);
				*/						
				WechatSendMessageUtils.sendWechatOnOffMessages("关闭"+controllertype+"成功", publicOpenID, controller.getDevice_sn());
			}else {//打开
				WechatSendMessageUtils.sendWechatOnOffMessages("打开"+controllertype+"成功", publicOpenID, controller.getDevice_sn());
			}
		}else {
			if(param.getKey() == 0) {
				WechatSendMessageUtils.sendWechatOnOffMessages("关闭"+controllertype+"失败", publicOpenID, controller.getDevice_sn());
			}else {
				WechatSendMessageUtils.sendWechatOnOffMessages("打开"+controllertype+"失败", publicOpenID, controller.getDevice_sn());
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
		WechatSendMessageUtils.sendWechatOxyAlarmMessages("关闭增氧机成功", "orEjLv8vQBk6QpWuk327Qt7kUk8I", device_sn);
	}
	
	@RequestMapping(value ="/getAllController", method = RequestMethod.GET)
	public List<Controller> getAllController(){		
		return equipmentService.getAllControllers();
	}
	
	@RequestMapping(value ="/ALLDataYesterday", method = RequestMethod.GET)
	public void ALLDataYesterday(){		
		 equipmentService.saveALLDataYesterday();
	}
	
}
