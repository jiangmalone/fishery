package com.geariot.platform.fishery.wxutils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.geariot.platform.fishery.dao.ControllerDao;
import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.Controller;
import com.geariot.platform.fishery.entities.Pond;
import com.geariot.platform.fishery.service.EquipmentService;
import com.geariot.platform.fishery.service.SocketSerivce;
import com.geariot.platform.fishery.utils.ApplicationUtil;
import com.geariot.platform.fishery.utils.HttpRequest;

public class WechatTemplateMessage {
	
	
	
	private static final String MSG_TEMPLATE_ID="P9tXcCFGquvWFcqPyDD5OK7BZ6rFFfwZcGu54wrBBa8";
//	private static final Logger log = Logger.getLogger(WechatTemplateMessage.class);
	private static final Logger log = LogManager.getLogger(WechatTemplateMessage.class);
	private static final String ALARM_TEMPLATE_ID="rWbgpqTb6alKSu4Wusf7ItFq2FQRQrzk1CNQV0uyJ_4";
	private static EquipmentService service = (EquipmentService) ApplicationUtil.getBean("EquipmentService");
	//private static final String ALARM_TEMPLATE_ID=null;
   private static SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
	private static String invokeTemplateMessage(JSONObject params){
		StringEntity entity = new StringEntity(params.toString(),"utf-8"); //解决中文乱码问题   
		String result = HttpRequest.postCall(WechatConfig.WECHAT_TEMPLATE_MESSAGE_URL + 
				WechatConfig.getAccessTokenForInteface().getString("access_token"),
				entity, null);
		log.debug("微信模版消息结果：" + result);
		return result;
	}
	


//{{first.DATA}}
//类型：{{keyword1.DATA}}
//金额：{{keyword2.DATA}}
//状态：{{keyword3.DATA}}
//时间：{{keyword4.DATA}}
//备注：{{keyword5.DATA}}
//{{remark.DATA}}

	public static void sendBrokenMSG(StringBuilder sb,String openId,String deviceSn) {
		log.debug("给前台发送故障信息------");
		JSONObject params=new JSONObject();
		JSONObject data=new JSONObject();
		params.put("touser",openId);
		params.put("template_id", MSG_TEMPLATE_ID);
		data.put("first", keywordFactory("故障信息","#173177"));
		data.put("keyword1", keywordFactory(deviceSn,"#173177"));
		data.put("keyword2", keywordFactory(sb.toString(),"#173177"));
		data.put("keyword3", keywordFactory(sdf.format(new Date()),"#173177"));
		data.put("remark", keywordFactory(""));
		params.put("data", data);
		String result=invokeTemplateMessage(params);
		log.debug("故障消息结果:"+result);
		//data.put(key, value);
	}
	
	public static void sendSetAutoMSG(String msg,String openId,String deviceSn) {
		log.debug("给用户发送一键自动结果信息------");
		JSONObject params=new JSONObject();
		JSONObject data=new JSONObject();
		params.put("touser",openId);
		params.put("template_id", MSG_TEMPLATE_ID);
		data.put("first", keywordFactory("结果","#173177"));
		data.put("keyword1", keywordFactory(deviceSn,"#173177"));
		data.put("keyword2", keywordFactory(msg,"#173177"));
		data.put("keyword3", keywordFactory(sdf.format(new Date()),"#173177"));
		data.put("remark", keywordFactory(""));
		params.put("data", data);
		String result=invokeTemplateMessage(params);
		log.debug("结果:"+result);
		//data.put(key, value);
	}
	
	public static void sendSetPathMSG(String msg,String openId,String deviceSn) {
		log.debug("给用户发送设置使用哪路传感器结果信息------");
		JSONObject params=new JSONObject();
		JSONObject data=new JSONObject();
		params.put("touser",openId);
		params.put("template_id", MSG_TEMPLATE_ID);
		data.put("first", keywordFactory("结果","#173177"));
		data.put("keyword1", keywordFactory(deviceSn,"#173177"));
		data.put("keyword2", keywordFactory(msg,"#173177"));
		data.put("keyword3", keywordFactory(sdf.format(new Date()),"#173177"));
		data.put("remark", keywordFactory(""));
		params.put("data", data);
		String result=invokeTemplateMessage(params);
		log.debug("结果:"+result);
		//data.put(key, value);
	}
	public static void sendCheckMSG(String msg,String openId,String deviceSn) {
		log.debug("给用户发送校准结果信息------");
		JSONObject params=new JSONObject();
		JSONObject data=new JSONObject();
		params.put("touser",openId);
		params.put("template_id", MSG_TEMPLATE_ID);
		data.put("first", keywordFactory("结果","#173177"));
		data.put("keyword1", keywordFactory(deviceSn,"#173177"));
		data.put("keyword2", keywordFactory(msg,"#173177"));
		data.put("keyword3", keywordFactory(sdf.format(new Date()),"#173177"));
		data.put("remark", keywordFactory(""));
		params.put("data", data);
		String result=invokeTemplateMessage(params);
		log.debug("结果:"+result);
		//data.put(key, value);
	}
	
	
	public static void sendServerLimitMSG(String msg,String openId,String deviceSn) {
		log.debug("给前台推送用户设置三限结果信息------");
		JSONObject params=new JSONObject();
		JSONObject data=new JSONObject();
		params.put("touser",openId);
		params.put("template_id", MSG_TEMPLATE_ID);
		data.put("first", keywordFactory("设置三限结果信息","#173177"));
		data.put("keyword1", keywordFactory(deviceSn,"#173177"));
		data.put("keyword2", keywordFactory(msg,"#173177"));
		data.put("keyword3", keywordFactory(sdf.format(new Date()),"#173177"));
		data.put("remark", keywordFactory(""));
		params.put("data", data);
		String result=invokeTemplateMessage(params);
		log.debug("消息结果:"+result);
		//data.put(key, value);
	}
	
	public static void sendOnoffMSG(String msg,String openId,String deviceSn) {
		
		log.debug("向微信用户发送增氧机开闭结果信息····");
			
		JSONObject params=new JSONObject();
		JSONObject data=new JSONObject();
		params.put("touser",openId);
		params.put("template_id", MSG_TEMPLATE_ID);
		data.put("first", keywordFactory("结果信息","#173177"));
		data.put("keyword1", keywordFactory(deviceSn,"#173177"));
		data.put("keyword2", keywordFactory(msg,"#173177"));
		data.put("keyword3", keywordFactory(sdf.format(new Date()),"#173177"));
		data.put("remark", keywordFactory(""));
		params.put("data", data);
		String result=invokeTemplateMessage(params);
		log.debug("消息结果:"+result);
		//data.put(key, value);
	}
	
public static void sendOxyAlarmMSG(String msg,String openId,String deviceSn) {
		
		log.debug("向微信用户发送增氧机缺相报警信息····");
			
		JSONObject params=new JSONObject();
		JSONObject data=new JSONObject();
		params.put("touser",openId);
		params.put("template_id", MSG_TEMPLATE_ID);
		data.put("first", keywordFactory("缺相报警信息","#173177"));
		data.put("keyword1", keywordFactory(deviceSn,"#173177"));
		data.put("keyword2", keywordFactory(msg,"#173177"));
		data.put("keyword3", keywordFactory(sdf.format(new Date()),"#173177"));
		data.put("remark", keywordFactory(""));
		params.put("data", data);
		String result=invokeTemplateMessage(params);
		log.debug("消息结果:"+result);
		//data.put(key, value);
	}

public static void sendVoltageAlarmMSG(String msg,String openId,String deviceSn) {
	
	log.debug("向微信用户发送断电报警信息····");
		
	JSONObject params=new JSONObject();
	JSONObject data=new JSONObject();
	params.put("touser",openId);
	params.put("template_id", MSG_TEMPLATE_ID);
	data.put("first", keywordFactory("断电报警信息","#173177"));
	data.put("keyword1", keywordFactory(deviceSn,"#173177"));
	data.put("keyword2", keywordFactory(msg,"#173177"));
	data.put("keyword3", keywordFactory(sdf.format(new Date()),"#173177"));
	data.put("remark", keywordFactory(""));
	params.put("data", data);
	String result=invokeTemplateMessage(params);
	log.debug("消息结果:"+result);
	//data.put(key, value);
}

public static void sendDataAlarmMSG(String msg,String openId,String deviceSn) {
	
	log.debug("向微信用户发送数据异常报警信息····");
		
	JSONObject params=new JSONObject();
	JSONObject data=new JSONObject();
	params.put("touser",openId);
	params.put("template_id", MSG_TEMPLATE_ID);
	data.put("first", keywordFactory("数据异常报警信息","#173177"));
	data.put("keyword1", keywordFactory(deviceSn,"#173177"));
	data.put("keyword2", keywordFactory(msg,"#173177"));
	data.put("keyword3", keywordFactory(sdf.format(new Date()),"#173177"));
	data.put("remark", keywordFactory(""));
	params.put("data", data);
	String result=invokeTemplateMessage(params);
	log.debug("消息结果:"+result);
	//data.put(key, value);
}
	
	public static void sendOxygenOnoffMSG(String msg,String openId,String deviceSn,int onOff) {
		if(onOff==0) {
		log.debug("向微信用户发送增氧机关闭失败信息····");
		}else {
			log.debug("向微信用户发送增氧机打开失败信息");
		}
		JSONObject params=new JSONObject();
		JSONObject data=new JSONObject();
		params.put("touser", openId);
		params.put("template_id", MSG_TEMPLATE_ID);
		data.put("first", keywordFactory("增氧机开闭信息","#173177"));
		data.put("keyword1", keywordFactory(deviceSn,"#173177"));
		if(onOff==1) {
		data.put("keyword2", keywordFactory("增氧机打开失败","#173177"));
		}else {
			data.put("keyword2", keywordFactory("增氧机关闭失败","#173177"));
		}
		data.put("keyword3", keywordFactory(sdf.format(new Date()),"#173177"));
		data.put("remark", keywordFactory(""));
		params.put("data", data);
		String result=invokeTemplateMessage(params);
		if(onOff==1) {
		log.debug("增氧机打开失败信息结果："+result);
		}else {
			log.debug("增氧机关闭失败信息结果："+result);
		}
	}
	
	public static void alarmMSG(String msg,String openId,String deviceSn) {
		log.debug("向微信用户发送报警信息");
		
		/*AIO aio=service.findAIOByDeviceSn(deviceSn);
		Pond pond=null;
		if(aio!=null) {
		pond=service.findPondById(aio.getPondId());
		}*/
		String pondName = "";
		Map<String, Object> returnController = service.getControllersBydevice_sn(deviceSn);				
		for(int i=0;i<returnController.size();i++) {
			Map<String, Object> port_controller = (Map<String, Object>) returnController.get(i+"");
			List<Controller> controllerList = (List<Controller>) port_controller.get("controller");
			for(Controller con:controllerList) {
				pondName = pondName + con.getName()+" ";
			}			
		}		
		JSONObject params=new JSONObject();
		JSONObject data=new JSONObject();
		params.put("touser", openId);
		params.put("template_id", ALARM_TEMPLATE_ID);
		data.put("first", keywordFactory("报警信息","#173177"));
		if(pondName!="") {
		data.put("keyword1", keywordFactory(pondName,"#173177"));
		}else {
		data.put("keyword1", keywordFactory("报警的设备没有绑定塘口","#173177"));
		}
		data.put("keyword2", keywordFactory(deviceSn,"#173177"));
		data.put("keyword3", keywordFactory(sdf.format(new Date()),"#173177"));
		data.put("keyword4", keywordFactory(msg,"#173177"));
		data.put("remark", keywordFactory(""));
		params.put("data", data);
		String result=invokeTemplateMessage(params);
		log.debug("报警信息结果："+result);		
	}
	
	public static void sendSelftestMSG(String msg,String openId,String deviceSn) {
		log.debug("给用户发送设备自检成功信息------");
		JSONObject params=new JSONObject();
		JSONObject data=new JSONObject();
		params.put("touser",openId);
		params.put("template_id", MSG_TEMPLATE_ID);
		data.put("first", keywordFactory("设备连接结果","#173177"));
		data.put("keyword1", keywordFactory(deviceSn,"#173177"));
		data.put("keyword2", keywordFactory(msg,"#173177"));
		data.put("keyword3", keywordFactory(sdf.format(new Date()),"#173177"));
		data.put("remark", keywordFactory(""));
		params.put("data", data);
		String result=invokeTemplateMessage(params);
		log.debug("结果:"+result);
		//data.put(key, value);
	}
	
	
	private static JSONObject keywordFactory(String value){
		JSONObject keyword = new JSONObject();
		keyword.put("value", value);
		return keyword;
	}
	
	private static JSONObject keywordFactory(String value, String color){
		JSONObject keyword = keywordFactory(value);
		keyword.put("color", color);
		return keyword;
	}
	
}
