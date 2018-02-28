package com.geariot.platform.fishery.wxutils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WechatSendMessageUtils {

	//private static final Logger log = LogManager.getLogger(WechatSendMessageUtils.class);
    private static ExecutorService  executorService  = Executors.newFixedThreadPool(10);
	
	public static void  sendWechatMessages(final StringBuilder sb,final String openId,final String deviceSn) {
		executorService.execute(new Runnable() {

            @Override
            public void run(){
            	WechatTemplateMessage.sendBrokenMSG(sb,openId,deviceSn);
            }
        });
	}
	
	public static void  sendWechatAlarmMessages(final String message,final String openId,final String deviceSn) {
		executorService.execute(new Runnable() {

            @Override
            public void run(){
            	WechatTemplateMessage.alarmMSG(message, openId,deviceSn);
            }
        });
	}
	
	public static void sendWechatOxygenOnOffMessages(final String msg,final String openId,final String deviceSn,final int onOff) {
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				WechatTemplateMessage.sendOxygenOnoffMSG(msg, openId,deviceSn,onOff);
			}
			
		});
	}
	
	public static void sendWechatOnOffMessages(final String msg,final String openId,final String deviceSn) {
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				WechatTemplateMessage.sendOnoffMSG(msg, openId,deviceSn);
			}
			
		});
	}
	
	public static void sendWechatLimitMessages(final String msg,final String openId,final String deviceSn) {
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				WechatTemplateMessage.sendServerLimitMSG(msg, openId, deviceSn);
			}
			
		});
	}
	
	public static void sendWechatCheckMessages(final String msg,final String openId,final String deviceSn) {
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				WechatTemplateMessage.sendCheckMSG(msg, openId, deviceSn);
			}
			
		});
	}
	
	public static void sendWechatSetPathMessages(final String msg,final String openId,final String deviceSn) {
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				WechatTemplateMessage.sendSetPathMSG(msg, openId, deviceSn);
			}
			
		});
	}
	
	public static void sendWechatSetAutoMessages(final String msg,final String openId,final String deviceSn) {
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				WechatTemplateMessage.sendSetAutoMSG(msg, openId, deviceSn);
			}
			
		});
	}
}
