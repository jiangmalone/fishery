package com.geariot.platform.fishery.timer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.ControllerEventListener;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aliyuncs.dyvmsapi.model.v20170525.SingleCallByTtsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.geariot.platform.fishery.dao.AIODao;
import com.geariot.platform.fishery.dao.ControllerDao;
import com.geariot.platform.fishery.dao.DeviceDao;
import com.geariot.platform.fishery.dao.SensorDao;
import com.geariot.platform.fishery.dao.TimerDao;
import com.geariot.platform.fishery.dao.WXUserDao;
import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.Controller;
import com.geariot.platform.fishery.entities.Device;
import com.geariot.platform.fishery.entities.Limit_Install;
import com.geariot.platform.fishery.entities.Sensor;
import com.geariot.platform.fishery.entities.Timer;
import com.geariot.platform.fishery.entities.WXUser;
import com.geariot.platform.fishery.service.EquipmentService;
import com.geariot.platform.fishery.service.UserService;
import com.geariot.platform.fishery.timer.CMDUtils;
import com.geariot.platform.fishery.utils.VmsUtils;
import com.geariot.platform.fishery.wxutils.WechatSendMessageUtils;

import cmcc.iot.onenet.javasdk.api.datastreams.GetDatastreamApi;
import cmcc.iot.onenet.javasdk.api.device.GetDevicesStatus;
import cmcc.iot.onenet.javasdk.api.device.GetLatesDeviceData;
import cmcc.iot.onenet.javasdk.response.BasicResponse;
import cmcc.iot.onenet.javasdk.response.datastreams.DatastreamsResponse;
import cmcc.iot.onenet.javasdk.response.device.DeciceLatestDataPoint;
import cmcc.iot.onenet.javasdk.response.device.DevicesStatusList;

@Component
@Transactional
public class TimerTask {

	@Autowired
	private TimerDao timerDao;
	@Autowired
	private SensorDao sensorDao;
	@Autowired
	private AIODao aioDao;
	@Autowired
	private ControllerDao controllerDao;
	@Autowired
	private EquipmentService equipmentService;
	@Autowired
	private UserService userService;
	@Autowired
	private WXUserDao wxUserDao;
	@Autowired
	private DeviceDao deviceDao;
	@Autowired
	private WXUserDao wxuserDao;
	
	private static Logger logger = Logger.getLogger(TimerTask.class);
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	
	private String key = "7zMmzMWnY1jlegImd=m4p9EgZiI=";

	
	@Scheduled(cron = "0 */15 * * * ?") // 每15分钟执行一次
	public void judgeTime() throws ParseException {
		logger.debug("进入定时任务1");
		//定时检测增氧机的定时任务
		List<Timer> lt = timerDao.findAllTimer();
		if (!lt.isEmpty()) {
			logger.debug("时间不为空");
			long now = sdf.parse(sdf.format(new Date())).getTime();			
			for (Timer timer : lt) {
				logger.debug("timer循环");
				if(timer.isValid()) {
					logger.debug("控制器设备编号："+timer.getDevice_sn()+"的时间自动设置有效");
					int way = timer.getWay();
					//60000毫秒=1*60*1000等于5分钟，可能会有处理定时任务上的时间误差所以定个5分钟
					String device_sn = timer.getDevice_sn();
					List<Controller> controllerList = controllerDao.findControllerByDeviceSnAndWay(device_sn, timer.getWay());
					String[] controllertype = {"增氧机","投饵机","打水机","其他"};
					logger.debug("device_sn"+device_sn);
					if(controllerList!=null&&controllerList.size()>0) {
						WXUser wxUser = wxUserDao.findUserByRelation(controllerList.get(0).getRelation());
						String publicOpenID = userService.getPublicOpenId(wxUser.getOpenId());					
						GetDevicesStatus apiOnline = new GetDevicesStatus(device_sn,key);
				        BasicResponse<DevicesStatusList> responseOnline = apiOnline.executeApi();
				        if(responseOnline.errno==0) {
				        	boolean isOnline = responseOnline.data.getDevices().get(0).getIsonline();
				        	if(isOnline) {
				        		
				        		/*
				        		 * 获取增氧机相关数据流
				        		 */ 
				        		GetLatesDeviceData api = new GetLatesDeviceData(device_sn,key);
				     	        BasicResponse<DeciceLatestDataPoint> response = api.executeApi();				     	       
				     	        List<cmcc.iot.onenet.javasdk.response.device.DeciceLatestDataPoint.DeviceItem.DatastreamsItem> DatastreamsList = response.data.getDevices().get(0).getDatastreams();
				     	       if(DatastreamsList == null) {
				     	    	   logger.debug("无数据流");
				     	       }else {
				     	    	  int PF = 0;
				     	    	  int KM = 0;
				     	    	  int DP  = 0;
				     	    	  for(int i=0;i<DatastreamsList.size();i++) {
					     	        	if(DatastreamsList.get(i).getId().equals("PF")) {
					     	        		PF = Integer.parseInt(DatastreamsList.get(i).getValue().toString()) ;						     	        		
					     	        	}else if(DatastreamsList.get(i).getId().equals("KM"+(way+1))) {
					     	        		KM = Integer.parseInt(DatastreamsList.get(i).getValue().toString()) ;	
					     	        	}else if(DatastreamsList.get(i).getId().equals("DP"+(way+1))) {
					     	        		DP = Integer.parseInt(DatastreamsList.get(i).getValue().toString()) ;	
					     	        	}					     	   					     	        	
					     	      }
				     	    	  
				     	    	  
				     	    	  
				     	    	 if (now-sdf.parse(timer.getEndTime()).getTime() <= 0 && now-sdf.parse(timer.getStartTime()).getTime() >= 0) {
				     	    		logger.debug("检测到数据中有待执行的定时任务，准备向终端发送打开增氧机的命令");
									WechatSendMessageUtils.sendWechatOnOffMessages("定时开始，准备打开增氧机", publicOpenID, device_sn);
									if(PF==0&&DP==0) {
					     	    		  logger.debug("增氧机未断电且不缺相");
					     	    		 if(KM != 1) {
							     	    		logger.debug("增氧机未打开，向增氧机发送打开命令。");
												String divsn=timer.getDevice_sn();
												String text = "KM"+(way+1)+":"+1;
												int results = CMDUtils.sendStrCmd(divsn,text);					
												if(results == 0) {
													WechatSendMessageUtils.sendWechatOnOffMessages("自动打开"+controllertype[controllerList.get(0).getType()]+"成功", publicOpenID, controllerList.get(0).getDevice_sn());
												}else {//打开
													WechatSendMessageUtils.sendWechatOnOffMessages("自动打开"+controllertype[controllerList.get(0).getType()]+"失败", publicOpenID, controllerList.get(0).getDevice_sn());
												}
						     	    	  }else {
						     	    		  	logger.debug("增氧机已经打开，不进行打开操作");
						     	    	  }				     	    					     	    		  
					     	    	  }
				     	    	 }
				     	    	 
				     	    	if (now-sdf.parse(timer.getEndTime()).getTime() <= 60000 && now-sdf.parse(timer.getEndTime()).getTime() >= 0) {
				     	    		logger.debug("检测到数据中有待执行的定时任务，准备向终端发送关闭增氧机的命令");
									WechatSendMessageUtils.sendWechatOnOffMessages("定时结束，准备关闭增氧机", publicOpenID, device_sn);
									if(PF==0&&DP==0) {
					     	    		  logger.debug("增氧机未断电且不缺相");
					     	    		 if(KM != 0) {						     	    	
						     	    		logger.debug("增氧机未关闭，向增氧机发送关闭命令！");
											String divsn=timer.getDevice_sn();
											String text = "KM"+(way+1)+":"+0;
											int results = CMDUtils.sendStrCmd(divsn,text);
											if(results==0) {
												WechatSendMessageUtils.sendWechatOnOffMessages("自动关闭"+controllertype[controllerList.get(0).getType()]+"成功", publicOpenID, controllerList.get(0).getDevice_sn());
											}else {//打开
												WechatSendMessageUtils.sendWechatOnOffMessages("自动关闭"+controllertype[controllerList.get(0).getType()]+"失败", publicOpenID, controllerList.get(0).getDevice_sn());
											}
					     	    		 }
									
									}			     	    	  			     	    	 			     	    	 		     	    	  				     	    	  
				     	    	}				     	        
				        	}			        	
				        }else {
				        	logger.debug("增氧机:"+device_sn+"不在线");
				        }													
					
				       }
					}
				}
				
			}				
		}		
		logger.debug("定时任务结束");
	}
	
	@Scheduled(cron = "0 0 1 * * ?")//每天凌晨一点执行一次
	//@Scheduled(cron = "0 */10 * * * ?") // 每15分钟执行一次
	public void dosaveData() {
		logger.debug("凌晨一点，开始存储昨日所有数据");
		long start = System.currentTimeMillis();	
		equipmentService.saveALLDataYesterday();
		long end = System.currentTimeMillis();
		logger.debug("共耗时："+(end-start)+",存储结束！");
	}
	
	@Scheduled(cron = "0 0 */1 * * ?")//每小时执行一次
	//@Scheduled(cron = "0 */20 * * * ?") // 每15分钟执行一次
	public void checkOnline() {
		logger.debug("进入定时检测设备是否在线");
		long start = System.currentTimeMillis();	
		List<Device> deviceList = equipmentService.getAllDevices();
		for(Device device:deviceList) {
			 GetDevicesStatus api = new GetDevicesStatus(device.getDevice_sn(),key);
		     BasicResponse<DevicesStatusList> response = api.executeApi();
		     if(response.errno==0) {
		    	 Boolean isOnline = response.data.getDevices().get(0).getIsonline();
			     Device deviceOld = deviceDao.findDevice(device.getDevice_sn());
			     if(isOnline) {
			    	 deviceOld.setOnline(true);
			    	 deviceDao.updateIsOnline(deviceOld);
			     }else {
			    	 if(deviceOld.isOnline()) {
			    		 WXUser wxUser = null;
			    		 String deviceName = null;
			    		 switch (deviceOld.getType()) {
							case 1:
								Sensor sensor = sensorDao.findSensorByDeviceSns(deviceOld.getDevice_sn());
								wxUser = wxUserDao.findUserByRelation(sensor.getRelation());
								deviceName = sensor.getName();
								break;
							case 2:
								
								break;
							case 3:							
								List<Controller> conList = controllerDao.findControllerByDeviceSns(deviceOld.getDevice_sn());
								wxUser = wxUserDao.findUserByRelation(conList.get(0).getRelation());
								deviceName = conList.get(0).getName();						
								break;
							default:
								break;
						}
			    		
						String publicOpenID = userService.getPublicOpenId(wxUser.getOpenId());	
			    		WechatSendMessageUtils.sendWechatAlarmMessages("设备离线", publicOpenID, device.getDevice_sn(),deviceName);
						String json = "{\"deviceName\":\"" + deviceName + "\",\"way\":" + "0" + "}";
						try {
							logger.debug("准备启用阿里云语音服务");
							VmsUtils.singleCallByTts(wxUser.getPhone(), "TTS_142385982", json);
						} catch (ClientException e) {							
							e.printStackTrace();
						}
						deviceOld.setOnline(false);
						deviceDao.updateIsOnline(deviceOld);
			    	 }
			     }
		     }		    
		}
		
		long end = System.currentTimeMillis();
		logger.debug("共耗时："+(end-start)+",定时检测离线结束！");
	}
}
