package com.geariot.platform.fishery.wxutils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WechatSendMessageUtils {

	//private static final Logger log = LogManager.getLogger(WechatSendMessageUtils.class);
    private static ExecutorService  executorService  = Executors.newFixedThreadPool(10);
	
	public static void  sendWechatMessages(final StringBuilder sb,final String openId) {
		executorService.submit(new Runnable() {

            @Override
            public void run(){
            	WechatTemplateMessage.sendBrokenMSG(sb,openId);
            }
        });
	}
	
	public static void  sendWechatAlarmMessages(final String message,final String openId) {
		executorService.submit(new Runnable() {

            @Override
            public void run(){
            	WechatTemplateMessage.alarmMSG(message, openId);
            }
        });
	}
	
	public static void sendWechatOxygenOnOffMessages(final String msg,final String openId) {
		executorService.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				WechatTemplateMessage.sendOxygenOnoffMSG(msg, openId);
			}
			
		});
	}
}
