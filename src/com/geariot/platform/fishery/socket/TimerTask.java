package com.geariot.platform.fishery.socket;

import java.nio.channels.SocketChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
	
//	@Scheduled(cron="0 0 0 * * ?")//每天晚上24点启动自动校准
//	public void check() {
//		Map<String,SocketChannel> map= CMDUtils.getclientMap();
//		for(String deviceSn:map.keySet()) {
//			CMDUtils.serverCheckCMD(deviceSn, 1);//第一路和第二路都去校准，这里不判断了哪一路没开了
//			CMDUtils.serverCheckCMD(deviceSn, 2);
//		}
//
//	}
	
	@Scheduled(cron = "0 0/30 * * * ?") // 每半小时执行一次
	public void judgeTime() throws ParseException {

		List<Timer> lt = timerDao.findAllTimer();
		AIO aio=null;
		if (!lt.isEmpty()) {
			
			long now = sdf.parse(sdf.format(new Date())).getTime();
			
			for (Timer timer : lt) {
				aio = aioDao.findAIOByDeviceSns(timer.getDevice_sn());
				if (aio == null)
					return;
				//300000毫秒等于5分钟，可能会有处理定时任务上的时间误差所以定个5分钟
				if (now-sdf.parse(timer.getStartTime()).getTime() <= 300000 && now-sdf.parse(timer.getStartTime()).getTime() >= 0) {
					logger.debug("检测到数据中有待执行的定时任务，准备向终端发送打开增氧机的命令");

					if (!CMDUtils.serverOnOffOxygenCMD(timer.getDevice_sn(), timer.getWay(), 1).containsKey("0")) {

						if (aio.getRelation() != null && aio.getRelation().contains("WX")) {
							WechatSendMessageUtils.sendWechatOxygenOnOffMessages(
									"设备编号为 " + timer.getDevice_sn() + " 的增氧机在定时时间为:" + timer.getStartTime() + "打开失败",
									wxuserDao.findUserByRelation(aio.getRelation())
											.getOpenId(),timer.getDevice_sn(),1);
						}
					}
				}
				if (now-sdf.parse(timer.getEndTime()).getTime() <= 300000 && now-sdf.parse(timer.getEndTime()).getTime() >= 0) {
					logger.debug("检测到数据中有待执行的定时任务，准备向终端发送关闭增氧机的命令");

					if (!CMDUtils.serverOnOffOxygenCMD(timer.getDevice_sn(), timer.getWay(), 0).containsKey("0")) {
						if (aio.getRelation() != null && aio.getRelation().contains("WX")) {
							WechatSendMessageUtils.sendWechatOxygenOnOffMessages(
									"设备编号为 " + timer.getDevice_sn() + " 的增氧机在定时时间为:" + timer.getStartTime() + "关闭失败",
									wxuserDao.findUserByRelation(aio.getRelation())
											.getOpenId(),timer.getDevice_sn(),0);
						}
					}
				}
			}
		}

	}
	
	
}
