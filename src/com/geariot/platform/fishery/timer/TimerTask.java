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
	private WXUserDao wxUserDao;


	@Autowired
	private WXUserDao wxuserDao;
	
	private static Logger logger = Logger.getLogger(TimerTask.class);
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	
	private String key = "7zMmzMWnY1jlegImd=m4p9EgZiI=";

	@Scheduled(cron = "0 */15 * * * ?") // 每15分钟执行一次
	public void judgeTime() throws ParseException {
		logger.debug("进入定时任务");
		//定时检测增氧机的定时任务
		List<Timer> lt = timerDao.findAllTimer();
		if (!lt.isEmpty()) {
			logger.debug("时间不为空");
			long now = sdf.parse(sdf.format(new Date())).getTime();			
			for (Timer timer : lt) {
				//60000毫秒=1*60*1000等于5分钟，可能会有处理定时任务上的时间误差所以定个5分钟
				String device_sn = timer.getDevice_sn();
				List<Controller> controllerList = controllerDao.findControllerByDeviceSnAndWay(device_sn, timer.getWay());
				String[] controllertype = {"增氧机","投饵机","打水机","其他"};
				logger.debug("device_sn"+device_sn);
				if(controllerList!=null&&controllerList.size()>0) {
					WXUser wxUser = wxUserDao.findUserByRelation(controllerList.get(0).getRelation());
					String publicOpenID = userService.getPublicOpenId(wxUser.getOpenId());
					logger.debug("publicOpenID:"+publicOpenID);
					if (now-sdf.parse(timer.getStartTime()).getTime() <= 60000 && now-sdf.parse(timer.getStartTime()).getTime() >= 0) {
						logger.debug("检测到数据中有待执行的定时任务，准备向终端发送打开增氧机的命令");
						WechatSendMessageUtils.sendWechatOnOffMessages("定时开始，准备打开增氧机", publicOpenID, device_sn);
						int way = timer.getWay();
						String divsn=timer.getDevice_sn();
						String text = "KM"+(way+1)+":"+1;
						int results = CMDUtils.sendStrCmd(divsn,text);					
						if(results == 0) {
							WechatSendMessageUtils.sendWechatOnOffMessages("打开"+controllertype[controllerList.get(0).getType()]+"成功", publicOpenID, controllerList.get(0).getDevice_sn());
						}else {//打开
							WechatSendMessageUtils.sendWechatOnOffMessages("打开"+controllertype[controllerList.get(0).getType()]+"失败", publicOpenID, controllerList.get(0).getDevice_sn());
						}
					}
					if (now-sdf.parse(timer.getEndTime()).getTime() <= 60000 && now-sdf.parse(timer.getEndTime()).getTime() >= 0) {
						logger.debug("检测到数据中有待执行的定时任务，准备向终端发送关闭增氧机的命令");
						WechatSendMessageUtils.sendWechatOnOffMessages("定时结束，准备关闭增氧机", publicOpenID, device_sn);
						int way = timer.getWay();
						String divsn=timer.getDevice_sn();
						String text = "KM"+(way+1)+":"+0;
						int results = CMDUtils.sendStrCmd(divsn,text);
						if(results==0) {
							WechatSendMessageUtils.sendWechatOnOffMessages("关闭"+controllertype[controllerList.get(0).getType()]+"成功", publicOpenID, controllerList.get(0).getDevice_sn());
						}else {//打开
							WechatSendMessageUtils.sendWechatOnOffMessages("关闭"+controllertype[controllerList.get(0).getType()]+"失败", publicOpenID, controllerList.get(0).getDevice_sn());
						}
						
					}
				}				
			}
		}		
		logger.debug("定时任务结束");
	}
	
	@Scheduled(cron = "0 0 1 1/1 * ?")//每天凌晨一点执行一次
	public void dosaveData() {
		logger.debug("凌晨一点，开始存储昨日所有数据");
		long start = System.currentTimeMillis();	
		equipmentService.saveALLDataYesterday();
		long end = System.currentTimeMillis();
		logger.debug("共耗时："+(end-start)+",存储结束！");
	}
	
	
}
