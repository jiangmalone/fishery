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

import cmcc.iot.onenet.javasdk.api.datastreams.GetDatastreamApi;
import cmcc.iot.onenet.javasdk.response.BasicResponse;
import cmcc.iot.onenet.javasdk.response.datapoints.DatapointsList.DatastreamsItem.DatapointsItem;
import cmcc.iot.onenet.javasdk.response.datastreams.DatastreamsResponse;
import net.sf.json.JSONObject;
import sun.invoke.empty.Empty;
import sun.util.logging.resources.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import com.geariot.platform.fishery.socket.CMDUtils;

@RestController
@RequestMapping(value = "/equipment")
public class EquipmentController {
	private Logger logger = LogManager.getLogger(EquipmentController.class);
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
	
	@Autowired
	private HttpServletRequest request;
	
	@RequestMapping(value = "/VertifyDevicesn",method = RequestMethod.GET)
	public Map<String, Object> VertifyDevicesn(String divsn){
		return equipmentService.VertifyDevicesn(divsn);
	}
	
	@RequestMapping(value = "/changeControllerWayOnoff",method = RequestMethod.GET)
	public void changeControllerWayOnoff(String divsn, int way ,int key){
		equipmentService.changeControllerWayOnoff(divsn,way,key);
	}
	
	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public Map<String, Object> queryEquipment(String device_sn, String userName, int page, int number) {
		return equipmentService.queryEquipment(device_sn, userName, page, number);
	}
	@RequestMapping(value = "/CompanyDelEquipments", method = RequestMethod.GET)
	public Map<String, Object> CompanyDelEquipments(String device_sn) {
		return equipmentService.CompanyDelEquipments(device_sn);
	}

	@RequestMapping(value = "/delEquipments", method = RequestMethod.GET)
	public Map<String, Object> delEquipment(String device_sn) {
		List<Controller> conList = controllerDao.findControllerByDeviceSns(device_sn);
		Sensor sen= sensorDao.findSensorByDeviceSns(device_sn);
		String relation = "";
		String openId="";
		if(conList!=null && conList.size()>0) {
			relation = conList.get(0).getRelation();
		}else if(sen!=null) {
			relation = sen.getRelation();
		}
		if(relation.equals("")==false) {
			 openId = wxUserDao.findUserByRelation(relation).getOpenId();			
		}
		String ip="";
		if (request.getHeader("x-forwarded-for") == null) {  
			 ip= request.getRemoteAddr();  
		}else {
			ip= request.getHeader("x-forwarded-for");  
		}  
		logger.debug("用户："+openId+"接口调用，删除设备："+device_sn+",ip:"+ip);
		return equipmentService.delEquipment(device_sn);
	}
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public Map<String, Object> addEquipment(String device_sn, String name, String relation) {
		return equipmentService.addEquipment(device_sn, name, relation);
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
	
	@RequestMapping(value = "/querySensor", method = RequestMethod.GET)
	public Map<String, Object> querySensor(String device_sn) {
		return equipmentService.querySensor(device_sn);
	}
	

	@RequestMapping(value = "/addController", method = RequestMethod.POST)
	public Map<String, Object> addController(@RequestBody Controller... controllers) {
		return equipmentService.addController(controllers);
	}
	
	@RequestMapping(value = "/modifyController", method = RequestMethod.POST)
	public Map<String, Object> modifyController(@RequestBody Controller... controllers) {
		return equipmentService.modifyController(controllers);
	}
	@RequestMapping(value = "/getControllersBydevice_sn", method = RequestMethod.GET)
	public List<Map<String, Object>> getControllersBydevice_sn(String device_sn) {
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
	
	@RequestMapping(value = "/findOneTrigger", method = RequestMethod.GET)
	public void checkSet(String id){
		equipmentService.findOnetrigger(id);
		
	}
	
	@RequestMapping(value = "/modifyallTrigger", method = RequestMethod.GET)
	public void modifyallTrigger(){
		equipmentService.modifyallTrigger();
		
	}
	
	@RequestMapping(value = "/setTimer", method = RequestMethod.POST)
	public Map<String, Object> autoSet(@RequestBody Param param){
		System.out.println("设备id："+param.getDevice_sn());
		System.out.println("设备way："+param.getWay());
		System.out.println("timer长度："+param.getTimers().length);
		//先删除
		equipmentService.delTimer(param.getDevice_sn(),  param.getWay());
		//后添加
		logger.debug("设置设备编号："+param.getDevice_sn()+"的自动设置时间");
		for(Timer timer:param.getTimers()) {			
			logger.debug("自动时间："+timer.getStartTime()+"--"+timer.getEndTime());
			timer.setDevice_sn(param.getDevice_sn());
			timer.setWay(param.getWay());
			timer.setValid(true);
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
		
		logger.debug("设备编号:"+limit_Install.getDevice_sn());
		logger.debug("way:"+limit_Install.getWay());
		Limit_Install limit_Install2 = limitDao.findLimitByDeviceSnsAndWay(limit_Install.getDevice_sn(), limit_Install.getWay());
		if(limit_Install2 == null) {
			logger.debug("增氧限制未设置");
			limit_Install.setValid(true);
			limitDao.save(limit_Install);
			List<Controller> controllerList = controllerDao.findControllerByDeviceSnAndWay(limit_Install.getDevice_sn(), limit_Install.getWay());
			List<Sensor> sensorList = sensorDao.findSensorsByRelation(controllerList.get(0).getRelation());
			for(Controller controller:controllerList) {
				int pondId =controller.getPondId();
				for(Sensor sensor:sensorList) {
					if(sensor.getPondId() == pondId) {
						equipmentService.addTrigger("DO", sensor.getDevice_sn(), "<", limit_Install.getLow_limit(), 2,limit_Install.getWay());
						equipmentService.addTrigger("DO", sensor.getDevice_sn(), ">", limit_Install.getUp_limit(), 2,limit_Install.getWay());
						equipmentService.addTrigger("DO", sensor.getDevice_sn(), ">", limit_Install.getHigh_limit(), 5,limit_Install.getWay());
					}
				}
			}			
		}else {
			logger.debug("进入修改");
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
	
	@RequestMapping(value ="/sendcmdtest", method = RequestMethod.POST)
	public int sendcmdtest(@RequestBody controllerParam param){
		Controller controller = param.getController();
		String contents = "KM"+(controller.getPort()+1)+":"+param.getKey();
		long start = System.currentTimeMillis();
		int result = CMDUtils.sendStrCmd(controller.getDevice_sn(), contents);
		long end = System.currentTimeMillis();
		System.out.println("总共花费："+(end-start));
		return result;
	}
	
	@RequestMapping(value ="/sendcmd", method = RequestMethod.POST)
	public Map<String, Object> sendcmd(@RequestBody controllerParam param){
		String ip = "";
		if (request.getHeader("x-forwarded-for") == null) {  
			 ip= request.getRemoteAddr();  
		}else {
			ip= request.getHeader("x-forwarded-for");  
		}  
		/*logger.debug("sendcmd+向设备发送命令");
		String[] type = {"增氧机","投饵机","打水机","其他"};
		Controller controller = param.getController();
		System.out.println("用户："+controller.getRelation());
		WXUser wxUser =  wxUserDao.findUserByRelation(controller.getRelation());		
		String contents = "KM"+(controller.getPort()+1)+":"+param.getKey();
		
		GetDatastreamApi api = new GetDatastreamApi(controller.getDevice_sn(), "KM"+(controller.getPort()+1), "7zMmzMWnY1jlegImd=m4p9EgZiI=");
		BasicResponse<DatastreamsResponse> response = api.executeApi();
		if(response.errno == 0) {
			logger.debug(response.getJson());
			int currentvalue = Integer.parseInt((String)response.data.getCurrentValue()) ;
			if(currentvalue == 0) {
				logger.debug("控制器处于关闭状态");
				if(param.getKey()==1) {
					logger.debug("手动打开控制器，自动设置失效");
					equipmentService.delAuto(controller.getDevice_sn(), controller.getPort());					
				}				
			}else if(currentvalue == 1) {
				logger.debug("控制器处于打开状态");
				if(param.getKey()==0) {
					logger.debug("手动关闭控制器，自动设置生效");
					equipmentService.openAuto(controller.getDevice_sn(), controller.getPort());					
				}
			}
		}
		
		int result = CMDUtils.sendStrCmd(controller.getDevice_sn(), contents);	
		if(wxUser == null) {
			System.out.println("用户不存在");
		}else {
			System.out.println(wxUser.getName());
		}
		String publicOpenID = userService.getPublicOpenId(wxUser.getOpenId());
		logger.debug("向设备"+controller.getDevice_sn()+"发送命令，将结果推送至微信用户"+publicOpenID);
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
		return result;*/
		logger.debug("进入接口：sendcmd"+",ip:"+ip+",对控制器"+param.getController().getDevice_sn()+"进行操作");
		return equipmentService.sendcmd(param);
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
	
/*	@RequestMapping(value ="/getControllersBydevice_sn", method = RequestMethod.GET)
	public Map<String, Object> getControllersBydevicesn(String device_sn){	
		String pondName = "";
		Map<String, Object> returnController= equipmentService.getControllersBydevice_sn(device_sn);
		for(int i=0;i<returnController.size();i++) {
			Map<String, Object> port_controller = (Map<String, Object>) returnController.get(i+"");
			List<Controller> controllerList = (List<Controller>) port_controller.get("controller");
			for(Controller con:controllerList) {
				int j= 0;
				pondName = pondName + con.getName() + "/";
				j++;
			}
			
		}
		System.out.println("pondName:"+pondName);
		return equipmentService.getControllersBydevice_sn(device_sn);
	}*/
	
	@RequestMapping(value ="/adminFindEquipment", method = RequestMethod.GET)
	public Map<String, Object> adminFindEquipment(String device_sn, String userName, int page, int number){
		return equipmentService.adminFindEquipment(device_sn,userName,page,number);
	}
	
}
