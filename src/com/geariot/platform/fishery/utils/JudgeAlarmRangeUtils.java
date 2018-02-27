package com.geariot.platform.fishery.utils;

import org.apache.log4j.Logger;

import com.geariot.platform.fishery.entities.DataAlarm;
import com.geariot.platform.fishery.model.AlarmRange;
import com.geariot.platform.fishery.service.SocketSerivce;
import com.geariot.platform.fishery.wxutils.WechatAlarmMessage;
import com.geariot.platform.fishery.wxutils.WechatSendMessageUtils;

//3个方法用来分别判断溶氧值，水温，Ph的
public class JudgeAlarmRangeUtils {
	private static SocketSerivce service = (SocketSerivce) ApplicationUtil.getBean("socketSerivce");
    private static AlarmRange ar=LoadAlarmRange.getAlarmRange();
    private static Logger logger = Logger.getLogger(JudgeAlarmRangeUtils.class);
    
	public static void judgeDO(int type, float DO,String openId,String deviceSn,DataAlarm da) {
          String message=null;
         //System.out.println(ar.getCrab_DO_high_limit());
		if(Constants.FISH==type||Constants.LOBSTER==type||Constants.CRAB==type) {
			if(DO<ar.getFish_DO_low_limit()||DO<ar.getLobster_DO_low_limit()||DO<ar.getCrab_DO_low_limit()) {
				if(openId!=null)
				WechatSendMessageUtils.sendWechatAlarmMessages(WechatAlarmMessage.DO_DANGER, openId,deviceSn);
				message=WechatAlarmMessage.DO_DANGER;
			}/*else if(DO>ar.getFish_DO_high_limit()||DO>ar.getLobster_DO_high_limit()||DO>ar.getCrab_DO_high_limit()) {
				if(openId!=null)
					WechatSendMessageUtils.sendWechatAlarmMessages(WechatAlarmMessage.DO_NORMAL, openId);
				message=WechatAlarmMessage.DO_NORMAL;
			}*/else if((DO>=ar.getFish_DO_low_limit()&&DO<=ar.getFish_DO_high_limit())||(DO>=ar.getLobster_DO_low_limit()&&DO<=ar.getLobster_DO_high_limit())
					||(DO>=ar.getCrab_DO_low_limit()&&DO<=ar.getCrab_DO_high_limit())) {
				if(openId!=null)
					WechatSendMessageUtils.sendWechatAlarmMessages(WechatAlarmMessage.DO_WARNING, openId,deviceSn);
				message=WechatAlarmMessage.DO_WARNING;
			}
			
		}
		if(message!=null) {
			da.setAlarmType(0);
			da.setMessage(message);
			
			service.save(da);
		}
		
	}

	public static void judgeWaterTem(int type, float waterTem,String openId,String deviceSn,DataAlarm da) {
		String message=null;
		
		if(Constants.FISH==type||Constants.LOBSTER==type||Constants.CRAB==type) {//是鱼
			if(waterTem<ar.getFish_water_tem_low_limit()||waterTem<ar.getLobster_water_tem_low_limit()||waterTem<ar.getCrab_water_tem_low_limit()) {
				if(openId!=null)
					WechatSendMessageUtils.sendWechatAlarmMessages(WechatAlarmMessage.LOW_WATER_WARNING, openId,deviceSn);
				message=WechatAlarmMessage.LOW_WATER_WARNING;
			}else if(waterTem>ar.getFish_water_tem_high_limit()||waterTem>ar.getLobster_water_tem_high_limit()||waterTem>ar.getCrab_water_tem_high_limit()) {
				if(openId!=null)
					WechatSendMessageUtils.sendWechatAlarmMessages(WechatAlarmMessage.HIGH_WATER_WARNING, openId,deviceSn);
				message=WechatAlarmMessage.HIGH_WATER_WARNING;
			}/*else if((waterTem>=ar.getFish_water_tem_low_limit()&&waterTem<=ar.getFish_water_tem_high_limit())||(waterTem>=ar.getLobster_water_tem_low_limit()&&waterTem<=ar.getLobster_water_tem_high_limit())
					||(waterTem>=ar.getCrab_water_tem_low_limit()&&waterTem<=ar.getCrab_water_tem_high_limit())) {
				if(openId!=null)
					WechatSendMessageUtils.sendWechatAlarmMessages(WechatAlarmMessage.WATER_NORMAL, openId);
				message=WechatAlarmMessage.WATER_NORMAL;
			}*/
		}
		
		if(message!=null) {
			
			da.setAlarmType(1);
			da.setMessage(message);
			service.save(da);
			}
	}

	public static void judgePH(int type, float ph,String openId,String deviceSn,DataAlarm da) {
		String message=null;
		
		if(Constants.FISH==type||Constants.LOBSTER==type||Constants.CRAB==type) {//是鱼
			if((ph<ar.getFish_ph_low_limit()||ph>ar.getFish_ph_high_limit())||(ph<ar.getLobster_ph_low_limit()||ph>ar.getLobster_ph_high_limit())||
					(ph<ar.getCrab_ph_low_limit()||ph>ar.getCrab_ph_high_limit())) {
				if(openId!=null)
					WechatSendMessageUtils.sendWechatAlarmMessages(WechatAlarmMessage.PH_DANGER, openId,deviceSn);
				message=WechatAlarmMessage.PH_DANGER;
			}else if((ph>=ar.getFish_ph_low_limit()&&ph<=ar.getFish_ph_low_to_middle_limit())||(ph>=ar.getLobster_ph_low_limit()&&ph<=ar.getLobster_ph_low_to_middle_limit())||
					(ph>=ar.getCrab_ph_low_limit()&&ph<=ar.getCrab_ph_low_to_middle_limit())
					) {
				if(openId!=null)
					WechatSendMessageUtils.sendWechatAlarmMessages(WechatAlarmMessage.LOW_PH_WARNING, openId,deviceSn);
				message=WechatAlarmMessage.LOW_PH_WARNING;
			}else if((ph>=ar.getFish_ph_middle_to_high_limit()&&ph<=ar.getFish_ph_high_limit())||(ph>=ar.getLobster_ph_middle_to_high_limit()&&ph<=ar.getLobster_ph_high_limit())||
					(ph>=ar.getCrab_ph_middle_to_high_limit()&&ph<=ar.getCrab_ph_high_limit())) {
				if(openId!=null)
					WechatSendMessageUtils.sendWechatAlarmMessages(WechatAlarmMessage.HIGH_PH_WARNING, openId,deviceSn);
				message=WechatAlarmMessage.HIGH_PH_WARNING;
			}
			/*else if((ph>=ar.getFish_ph_low_to_middle_limit()&&ph<=ar.getFish_ph_middle_to_high_limit())||(ph>=ar.getLobster_ph_low_to_middle_limit()&&ph<=ar.getLobster_ph_middle_to_high_limit())||
					(ph>=ar.getCrab_ph_low_to_middle_limit()&&ph<=ar.getCrab_ph_middle_to_high_limit())) {
				if(openId!=null)
					WechatSendMessageUtils.sendWechatAlarmMessages(WechatAlarmMessage.PH_NORMAL, openId);
				message=WechatAlarmMessage.PH_NORMAL;
			}*/
		}
		
		if(message!=null) {
			
			da.setAlarmType(2);
			da.setMessage(message);
			
			service.save(da);
			}
	}
	
}
