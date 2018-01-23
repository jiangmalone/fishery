package com.geariot.platform.fishery.socket;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.geariot.platform.fishery.entities.Timer;
import com.geariot.platform.fishery.service.SocketSerivce;
import com.geariot.platform.fishery.utils.ApplicationUtil;

//开个判断线程每30分钟轮询数据库
public class TimerTask {
	 

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
	
	public static void judgeTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("mm");
		String now = sdf.format(new Date());
		SocketSerivce service = (SocketSerivce) ApplicationUtil.getBean("socketSerivce");
		if (now.compareTo("30") == 0 || now.compareTo("00") == 0) {
			List<Timer> lt = service.findAllTimer();
			if (!lt.isEmpty()) {
				SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
				String now1 = sdf1.format(new Date());
				for (Timer timer : lt) {
					if (now1.compareTo(timer.getStartTime()) <= 5) {
						CMDUtils.serverOnOffOxygenCMD(timer, 1);
					}
					if (now1.compareTo(timer.getEndTime()) <= 5) {
						CMDUtils.serverOnOffOxygenCMD(timer, 0);
					}
				}
			} 
		}else {
			//
		}
	}
	
	
	public static void main(String[] args) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				judgeTime();
			}
			
		}).start();
	}
	
	
	
}
