package com.geariot.platform.fishery.socket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.geariot.platform.fishery.entities.Timer;
import com.geariot.platform.fishery.service.SocketSerivce;
import com.geariot.platform.fishery.utils.ApplicationUtil;


@Component
public class TimerTask {
	private static Logger logger = Logger.getLogger(TimerTask.class);
	@Scheduled(cron = "0 0/30 * * * ?") // 每半小时执行一次
	public static void judgeTime() {

		SocketSerivce service = (SocketSerivce) ApplicationUtil.getBean("socketSerivce");
         
		List<Timer> lt = service.findAllTimer();
		
		if (!lt.isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			String now = sdf.format(new Date());
			for (Timer timer : lt) {
			
				if (now.compareTo(timer.getStartTime()) <= 5&&now.compareTo(timer.getStartTime()) >= 0) {
					logger.debug("检测到数据中有待执行的定时任务，准备向终端发送打开增氧机的命令");
					
					CMDUtils.serverOnOffOxygenCMD(timer, 1);
					
				}
				if (now.compareTo(timer.getEndTime())<=5&&now.compareTo(timer.getEndTime())>=0) {
					logger.debug("检测到数据中有待执行的定时任务，准备向终端发送关闭增氧机的命令");
					
					CMDUtils.serverOnOffOxygenCMD(timer, 0);
					
				}
			}
		}

	}
	
	
	
	
}
/*public void timerOpen(final Timer timer) {  
Runnable runnableOpen = new Runnable() { 
    public void run() {  
        CMDUtils.serverOnOffOxygenCMD(timer, 1);
    }  
};  


Runnable runnableClose = new Runnable() { 
    public void run() {  
        CMDUtils.serverOnOffOxygenCMD(timer, 0);
    }  
};  
ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
// 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间  

SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");

long now = 0;
long start = 0;
long end=0;
try {
 now=sdf.parse(sdf.format(new Date())).getTime()/60000;
 start=sdf.parse(timer.getStartTime()).getTime()/60000+1440;
 end=sdf.parse(timer.getEndTime()).getTime()/60000+1440;
} catch (ParseException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}        
service.scheduleAtFixedRate(runnableOpen, start-now, 1440, TimeUnit.MINUTES);
service.scheduleAtFixedRate(runnableClose, end-now, 1440, TimeUnit.MINUTES);  
}  */