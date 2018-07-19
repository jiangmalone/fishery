package com.geariot.platform.fishery.timer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.sound.midi.ControllerEventListener;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.fishery.dao.ControllerDao;
import com.geariot.platform.fishery.dao.TimerDao;
import com.geariot.platform.fishery.dao.WXUserDao;
import com.geariot.platform.fishery.entities.Controller;
import com.geariot.platform.fishery.entities.Timer;
import com.geariot.platform.fishery.service.EquipmentService;
import com.geariot.platform.fishery.timer.CMDUtils;

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
	private WXUserDao wxuserDao;
	private static Logger logger = Logger.getLogger(TimerTask.class);
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

	@Scheduled(cron = "0 0/30 * * * ?") // 每半小时执行一次
	public void judgeTime() throws ParseException {
		List<Timer> lt = timerDao.findAllTimer();
		if (!lt.isEmpty()) {			
			long now = sdf.parse(sdf.format(new Date())).getTime();			
			for (Timer timer : lt) {
				//300000毫秒=5*60*1000等于5分钟，可能会有处理定时任务上的时间误差所以定个5分钟
				if (now-sdf.parse(timer.getStartTime()).getTime() <= 300000 && now-sdf.parse(timer.getStartTime()).getTime() >= 0) {
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
				}
			}
		}
		
		List<Controller> conList = equipmentService.getAllControllers();
/*		List<String> devicesnList =*/

	}
	

	

	
}
