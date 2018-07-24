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
import com.geariot.platform.fishery.dao.ControllerDao;
import com.geariot.platform.fishery.dao.TimerDao;
import com.geariot.platform.fishery.dao.WXUserDao;
import com.geariot.platform.fishery.entities.Controller;
import com.geariot.platform.fishery.entities.Limit_Install;
import com.geariot.platform.fishery.entities.Timer;
import com.geariot.platform.fishery.entities.WXUser;
import com.geariot.platform.fishery.service.EquipmentService;
import com.geariot.platform.fishery.service.UserService;
import com.geariot.platform.fishery.timer.CMDUtils;
import com.geariot.platform.fishery.utils.VmsUtils;
import com.geariot.platform.fishery.wxutils.WechatSendMessageUtils;

import cmcc.iot.onenet.javasdk.api.device.GetDevicesStatus;
import cmcc.iot.onenet.javasdk.api.device.GetLatesDeviceData;
import cmcc.iot.onenet.javasdk.response.BasicResponse;
import cmcc.iot.onenet.javasdk.response.device.DeciceLatestDataPoint;
import cmcc.iot.onenet.javasdk.response.device.DevicesStatusList;

@Component
@Transactional
public class TimerTask {

	@Autowired
	private TimerDao timerDao;
	@Autowired
	private ControllerDao controllerDao;
	@Autowired
	private EquipmentService equipmentService;
	@Autowired
	private UserService userService;


	@Autowired
	private WXUserDao wxuserDao;
	private static Logger logger = Logger.getLogger(TimerTask.class);
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	
	private String key = "7zMmzMWnY1jlegImd=m4p9EgZiI=";

	@Scheduled(cron = "0 0/3`0 * * * ?") // 每半小时执行一次
	public void judgeTime() throws ParseException {
		logger.debug("进入定时任务");
		//定时检测增氧机的定时任务
		List<Timer> lt = timerDao.findAllTimer();
		if (!lt.isEmpty()) {
			logger.debug("");
			long now = sdf.parse(sdf.format(new Date())).getTime();			
			for (Timer timer : lt) {
				//300000毫秒=5*60*1000等于5分钟，可能会有处理定时任务上的时间误差所以定个5分钟
				/*if (now-sdf.parse(timer.getStartTime()).getTime() <= 300000 && now-sdf.parse(timer.getStartTime()).getTime() >= 0) {
					logger.debug("检测到数据中有待执行的定时任务，准备向终端发送打开增氧机的命令");
					int way = timer.getWay();
					String divsn=timer.getDevice_sn();
					String text = "KM"+way+":"+1;
					int results = CMDUtils.sendStrCmd(divsn,text);
				}
				if (now-sdf.parse(timer.getEndTime()).getTime() <= 300000 && now-sdf.parse(timer.getEndTime()).getTime() >= 0) {
					logger.debug("检测到数据中有待执行的定时任务，准备向终端发送关闭增氧机的命令");
					int way = timer.getWay();
					String divsn=timer.getDevice_sn();
					String text = "KM"+way+":"+0;
					int results = CMDUtils.sendStrCmd(divsn,text);
				}*/
				if (now-sdf.parse(timer.getStartTime()).getTime() <= 1000 && now-sdf.parse(timer.getStartTime()).getTime() >= 0) {
					logger.debug("检测到数据中有待执行的定时任务，准备向终端发送打开增氧机的命令");
					int way = timer.getWay();
					String divsn=timer.getDevice_sn();
					String text = "KM"+way+":"+1;
					int results = CMDUtils.sendStrCmd(divsn,text);
				}
				if (now-sdf.parse(timer.getEndTime()).getTime() <= 1000 && now-sdf.parse(timer.getEndTime()).getTime() >= 0) {
					logger.debug("检测到数据中有待执行的定时任务，准备向终端发送关闭增氧机的命令");
					int way = timer.getWay();
					String divsn=timer.getDevice_sn();
					String text = "KM"+way+":"+0;
					int results = CMDUtils.sendStrCmd(divsn,text);
				}
			}
		}
		//定时检测控制器的断电/缺相
		/*List<Controller> conList = equipmentService.getAllControllers();
		logger.debug(conList.size());
		if(conList!=null) {
			for(Controller controller:conList) {
				WXUser wxUser = (WXUser) userService.relationDetail(controller.getRelation()).get("data");
				Map<String, Object> controllerMap=new HashMap<>();
				Map<String, Object> controllerDataMap=new HashMap<>();
				//获得设备离线/在线状态
				GetDevicesStatus api = new GetDevicesStatus(controller.getDevice_sn(),key);
		        BasicResponse<DevicesStatusList> response = api.executeApi();
		        logger.debug("获取控制器状态："+response.getJson());

		        if(response.errno == 0) {
		        	controllerMap.put("online", response.data.getDevices().get(0).getIsonline());
		        	//获得控制器最新数据
			        GetLatesDeviceData lddapi = new GetLatesDeviceData(controller.getDevice_sn(), key);
			        BasicResponse<DeciceLatestDataPoint> response2 = lddapi.executeApi();
			        System.out.println(response2.getJson());
			        if(response2.errno == 0) {
			        	List<cmcc.iot.onenet.javasdk.response.device.DeciceLatestDataPoint.DeviceItem.DatastreamsItem> datastreamsList = response2.data.getDevices().get(0).getDatastreams();
			        	if(datastreamsList!=null) {
			        		for(int i=0;i<datastreamsList.size();i++) {
					        	controllerDataMap.put(datastreamsList.get(i).getId(), datastreamsList.get(i).getValue());
					        }        	
					        controllerMap.put("data", controllerDataMap);						      
					        if(controllerDataMap.get("PF") !=null) {						      						        	
					        	String  PF = (String) controllerDataMap.get("PF");
						        String DP = (String) controllerDataMap.get("DP"+controller.getPort());
						        logger.debug("断电1或正常0："+PF);
						        if(PF.equals("1")) {					       
						        	WechatSendMessageUtils.sendWechatVoltageMessages("断电报警", wxUser.getOpenId(), controller.getDevice_sn());
						        	
						        	String json = "{\"deviceName\":\"" + controller.getName() + "\",\"way\":" + controller.getPort() + "}";
									try {
										System.out.println("准备启用阿里云语音服务");
										SingleCallByTtsResponse scbtr =VmsUtils.singleCallByTts(wxUser.getPhone(), "TTS_126781509", json);
									} catch (ClientException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
						        	
						        }else {
						        	if(DP.equals("1")) {
						        		logger.debug("缺相1或正常0："+DP);
						        		WechatSendMessageUtils.sendWechatOxyAlarmMessages("缺相报警", wxUser.getOpenId(), controller.getDevice_sn());
						        		String json = "{\"deviceName\":\"" + controller.getName() + "\",\"way\":" + controller.getPort() + "}";
										try {
											System.out.println("准备启用阿里云语音服务");
											SingleCallByTtsResponse scbtr = VmsUtils.singleCallByTts(wxUser.getPhone(), "TTS_126866281", json);
										} catch (ClientException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
						        	}else {
						        		controller.setStatus(0);
						        	}
						        }
					        }
			        	}    
			        }
		        }			      

			}
		}*/
		logger.debug("定时任务结束");

	}
	

	

	
}
