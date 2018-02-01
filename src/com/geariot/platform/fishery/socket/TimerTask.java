package com.geariot.platform.fishery.socket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.fishery.dao.AIODao;
import com.geariot.platform.fishery.dao.TimerDao;
import com.geariot.platform.fishery.dao.WXUserDao;
import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.Timer;
import com.geariot.platform.fishery.wxutils.WechatSendMessageUtils;

@Component
@Transactional
public class TimerTask {

	@Autowired
	private TimerDao timerDao;

	@Autowired
	private AIODao aioDao;

	@Autowired
	private WXUserDao wxuserDao;
	private static Logger logger = Logger.getLogger(TimerTask.class);
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	@Scheduled(cron = "0 0/30 * * * ?") // 每半小时执行一次
	public void judgeTime() {

		List<Timer> lt = timerDao.findAllTimer();
		AIO aio=null;
		if (!lt.isEmpty()) {
			
			String now = sdf.format(new Date());
			for (Timer timer : lt) {
				aio = aioDao.findAIOByDeviceSns(timer.getDevice_sn());
				if (aio == null)
					return;
				if (now.compareTo(timer.getStartTime()) <= 5 && now.compareTo(timer.getStartTime()) >= 0) {
					logger.debug("检测到数据中有待执行的定时任务，准备向终端发送打开增氧机的命令");

					if (!CMDUtils.serverOnOffOxygenCMD(timer.getDevice_sn(), timer.getWay(), 1).containsKey("0")) {

						if (aio.getRelation() != null && aio.getRelation().contains("WX")) {
							WechatSendMessageUtils.sendWechatOxygenOnOffMessages(
									"设备编号为 " + timer.getDevice_sn() + " 的增氧机在定时时间为:" + timer.getStartTime() + "打开失败",
									wxuserDao.findUserByRelation(aio.getRelation())
											.getOpenId());
						}
					}
				}
				if (now.compareTo(timer.getEndTime()) <= 5 && now.compareTo(timer.getEndTime()) >= 0) {
					logger.debug("检测到数据中有待执行的定时任务，准备向终端发送关闭增氧机的命令");

					if (!CMDUtils.serverOnOffOxygenCMD(timer.getDevice_sn(), timer.getWay(), 0).containsKey("0")) {
						if (aio.getRelation() != null && aio.getRelation().contains("WX")) {
							WechatSendMessageUtils.sendWechatOxygenOnOffMessages(
									"设备编号为 " + timer.getDevice_sn() + " 的增氧机在定时时间为:" + timer.getStartTime() + "关闭失败",
									wxuserDao.findUserByRelation(aio.getRelation())
											.getOpenId());
						}
					}
				}
			}
		}

	}
	
	
}
