package com.geariot.platform.fishery.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.Alarm;
import com.geariot.platform.fishery.entities.Broken;
import com.geariot.platform.fishery.entities.Controller;
import com.geariot.platform.fishery.entities.Limit_Install;
import com.geariot.platform.fishery.entities.SelfTest;
import com.geariot.platform.fishery.entities.Sensor;
import com.geariot.platform.fishery.entities.Sensor_Data;
import com.geariot.platform.fishery.entities.Timer;
import com.geariot.platform.fishery.entities.WXUser;
import com.geariot.platform.fishery.model.BrokenMSG;
import com.geariot.platform.fishery.model.EntityModel;
import com.geariot.platform.fishery.model.EntityType;
import com.geariot.platform.fishery.model.RESCODE;
import com.geariot.platform.fishery.service.SocketSerivce;
import com.geariot.platform.fishery.utils.ApplicationUtil;
import com.geariot.platform.fishery.utils.CommonUtils;
import com.geariot.platform.fishery.utils.StringUtils;
import com.geariot.platform.fishery.wxutils.WechatTemplateMessage;

public class CMDUtils {
	private static Logger logger = Logger.getLogger(CMDUtils.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Map<String, SocketChannel> clientMap = new ConcurrentHashMap<String, SocketChannel>();
	private static	byte[] response = null;
	private static byte[] data=null;
	private static SocketChannel readChannel=null;
	private static String deviceSn;
	private static byte way;
	private static  AtomicBoolean feedback=new AtomicBoolean();
    private static SocketSerivce service =(SocketSerivce) ApplicationUtil.getBean("socketSerivce");
	
	public static  AtomicBoolean getFeedback() {
		return feedback;
	}
	public static void setFeedback(AtomicBoolean feedback) {
		CMDUtils.feedback = feedback;
	}
	public static Map<String, SocketChannel> getclientMap()
	{
		return clientMap;
	}
	@SuppressWarnings("unchecked")
	public static void preHandle(SelectionKey key) {
		System.out.println(key.attachment());
		Map<String,Object> attachmentObject=(Map<String,Object>) key.attachment();
		 data =(byte[]) attachmentObject.get("data");		
		 readChannel=(SocketChannel) attachmentObject.get("readChannel");
		 deviceSn=(String) attachmentObject.get("deviceSn");
		 System.out.println(deviceSn+"..........................");
		 way=(byte) attachmentObject.get("way");
        
	}
	// 自检
	public static void selfTestCMD(SelectionKey key) throws IOException {
		  
		preHandle(key);
		clientMap.put(deviceSn, readChannel); 
	      
	       //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		    byte ac = data[7];
			byte[] byteLongitude =new byte[4];
			CommonUtils.arrayHandle(data, byteLongitude, 8, 0, 4);
			float longitude= CommonUtils.byte2float(byteLongitude,0);
			byte[] byteLatitude = new byte[4];
			CommonUtils.arrayHandle(data, byteLatitude, 12, 0, 4);
			float latitude= CommonUtils.byte2float(byteLatitude,0);
			//SocketSerivce service =(SocketSerivce) ApplicationUtil.getBean("socketSerivce");
			byte[] status = new byte[2];
			status[0]=data[16];//传感器和水泵状态
			status[1]=data[17];
			List<Broken> brokenlist=new ArrayList<Broken>();
			statusHandle(status,brokenlist);
			byte gprs = data[18];
			//byte check = data[18];
			//String suffix0 = CommonUtils.printHexStringMerge(data,19,4);
           SelfTest selfTest=new SelfTest();
           selfTest.setDevice_sn(deviceSn);
           selfTest.setPath(way);
           selfTest.setAc(ac);
           selfTest.setLatitude(latitude);
           selfTest.setLongitude(longitude);
           selfTest.setGprs(gprs);
           selfTest.setBroken(brokenlist);
           selfTest.setCreateDate(new Date());
           
		service.save(selfTest);
		response(19);
	}

	// 下位机设限上传给服务器
	public static void uploadLimitCMD(SelectionKey key) throws IOException {
		 preHandle(key);
		byte[] byteHigh = new byte[4];
			CommonUtils.arrayHandle(data, byteHigh, 7, 0, 4);
			float high= CommonUtils.byte2float(byteHigh,0);
			byte[] byteUp = new byte[4];
			CommonUtils.arrayHandle(data, byteUp, 11, 0, 4);
			float up= CommonUtils.byte2float(byteUp,0);
			byte[] bytelow = new byte[4];
			CommonUtils.arrayHandle(data, bytelow, 15, 0, 4);
			float low= CommonUtils.byte2float(bytelow,0);
			//byte check1 = data[19];
			//String suffix1 = CommonUtils.printHexStringMerge(data,20,4);
			//SocketSerivce service =(SocketSerivce) ApplicationUtil.getBean("socketSerivce");
			Limit_Install limit=new Limit_Install();
			limit.setDevice_sn(deviceSn);
			limit.setWay(way);
			limit.setUp_limit(up);
			limit.setHigh_limit(high);
			limit.setLow_limit(low);
			service.save(limit);
				response(20);
	}

	// 服务器设限下发给终端
	public static Map<String, Object> downLimitCMD(Limit_Install limit) {
		SocketChannel channel=clientMap.get(limit.getDevice_sn());
		if(channel==null) {
        	return RESCODE.NOT_OPEN.getJSONRES();
        }
		if(!channel.isConnected()) {
			return RESCODE.CONNECTION_CLOSED.getJSONRES();
		}
		byte[] request = null;
		String temp=StringUtils.add(limit.getDevice_sn(), limit.getWay(), 2)
				.append(Integer.toHexString(Float.floatToIntBits(limit.getHigh_limit())))
				.append(Integer.toHexString(Float.floatToIntBits(limit.getUp_limit())))
				.append(Integer.toHexString(Float.floatToIntBits(limit.getLow_limit())))
                .append("          ")
				.toString();
		
		request=CommonUtils.toByteArray(temp);
		request[19]=CommonUtils.arrayMerge(request, 2, 17);
		CommonUtils.addSuffix(request, 20);
		
	    ByteBuffer outBuffer = ByteBuffer.wrap(request);
	    
	    try {
			channel.write(outBuffer);
		} catch (IOException e) {
			return RESCODE.SEND_FAILED.getJSONRES();
		}
	    return responseToBrowser();
        
	}

	// 5分钟一次上传溶氧和水温值
	public static void timingUploadCMD(SelectionKey key) throws IOException {
		 preHandle(key);
		byte[] byteOxygen= new byte[4];
			CommonUtils.arrayHandle(data, byteOxygen, 7, 0, 4);
			float oxygen= CommonUtils.byte2float(byteOxygen,0);
			byte[] byteWaterTemp = new byte[4];
			CommonUtils.arrayHandle(data, byteWaterTemp, 11, 0, 4);
			float waterTemp= CommonUtils.byte2float(byteWaterTemp,0);
			String receiveTime = sdf.format(new Date());
			//byte check3 = data[15];
			//String suffix3 = CommonUtils.printHexStringMerge(data,16,4);
			//SocketSerivce service =(SocketSerivce) ApplicationUtil.getBean("socketSerivce");
			Sensor_Data sData=new Sensor_Data();
			sData.setDevice_sn(deviceSn);
			sData.setWay(way);
			sData.setOxygen(oxygen);
			sData.setWater_temperature(waterTemp);
			try {
				sData.setReceiveTime(sdf.parse(receiveTime));
			} catch (ParseException e) {
			logger.debug("日期转换错误");
			}
			service.update(sData);

				response(16);
	}

	// 增氧机缺相报警
	public static void oxygenAlarmCMD(SelectionKey key) throws IOException {
		 preHandle(key);    
		//byte check4 = data[7];
			//String suffix4 = CommonUtils.printHexStringMerge(data,8,4);
			
			//SocketSerivce service =(SocketSerivce) ApplicationUtil.getBean("socketSerivce");
		 String judge=deviceSn.substring(0, 2);
			if(judge.equals("01")||judge.equals("02")) {
				AIO aio=new AIO();
				aio.setStatus(3);
				service.updateAIO(aio);
			}else if(judge.equals("03")) {
				Sensor sensor=new Sensor();
				sensor.setStatus(3);
				service.updateSensor(sensor);
			}else if(judge.equals("04")) {
				Controller controller =new Controller();
				controller.setStatus(3);
				service.updateController(controller);
			}
		 Alarm alarm=new Alarm();
			alarm.setDeviceSn(deviceSn);
             alarm.setWay(way);	
             try {
				alarm.setCreateDate(sdf.parse(sdf.format(new Date())));
			} catch (ParseException e) {
				logger.debug("日期转换错误");
			}
             alarm.setAlarmType(1);
             service.save(alarm);

				response(8);
	}

	// 220v断电报警
	public static void voltageAlarmCMD(SelectionKey key) throws IOException {
		 preHandle(key);
		//byte check5 = data[7];
			//String suffix5 = CommonUtils.printHexStringMerge(data,8,4);
			
			//SocketSerivce service =(SocketSerivce) ApplicationUtil.getBean("socketSerivce");
		 String judge=deviceSn.substring(0, 2);
			if(judge.equals("01")||judge.equals("02")) {
				AIO aio=new AIO();
				aio.setStatus(2);
				service.updateAIO(aio);
			}else if(judge.equals("03")) {
				Sensor sensor=new Sensor();
				sensor.setStatus(2);
				service.updateSensor(sensor);
			}else if(judge.equals("04")) {
				Controller controller =new Controller();
				controller.setStatus(2);
				service.updateController(controller);
			}	
		 Alarm alarm=new Alarm();
			alarm.setDeviceSn(deviceSn);
             alarm.setWay(way);	
             try {
				alarm.setCreateDate(sdf.parse(sdf.format(new Date())));
			} catch (ParseException e) {
				logger.debug("日期转换错误");
			}
             alarm.setAlarmType(2);
             service.save(alarm);
				response(8);
	}

	// 增氧机打开后半小时内效果不明显报警
	public static void oxygenExceptionAlarmCMD(SelectionKey key) throws IOException {
		 preHandle(key);
		//byte check9 = data[7];
			//System.out.println("check = "+check9);
			//String suffix9 = CommonUtils.printHexStringMerge(data,8,4);
			//System.out.println("suffix = "+suffix9);
			
			//SocketSerivce service =(SocketSerivce) ApplicationUtil.getBean("socketSerivce");
			
		 String judge=deviceSn.substring(0, 2);
			if(judge.equals("01")||judge.equals("02")) {
				AIO aio=new AIO();
				aio.setStatus(4);
				service.updateAIO(aio);
			}else if(judge.equals("03")) {
				Sensor sensor=new Sensor();
				sensor.setStatus(4);
				service.updateSensor(sensor);
			}else if(judge.equals("04")) {
				Controller controller =new Controller();
				controller.setStatus(4);
				service.updateController(controller);
			}
		 Alarm alarm=new Alarm();
			alarm.setDeviceSn(deviceSn);
             alarm.setWay(way);	
             try {
				alarm.setCreateDate(sdf.parse(sdf.format(new Date())));
			} catch (ParseException e) {
				logger.debug("日期转换错误");
			}
             alarm.setAlarmType(3);
             service.save(alarm);
				response(8);
	}

	// 取消所有报警
	public static void cancelAllAlarmCMD(SelectionKey key) throws IOException {
		 preHandle(key);
		//byte check10 = data[7];
			//System.out.println("check = "+check10);
			//String suffix10 = CommonUtils.printHexStringMerge(data,8,4);
			//System.out.println("suffix = "+suffix10);
			
			//SocketSerivce service =(SocketSerivce) ApplicationUtil.getBean("socketSerivce");
		 String judge=deviceSn.substring(0, 2);
			if(judge.equals("01")||judge.equals("02")) {
				AIO aio=new AIO();
				aio.setStatus(0);
				service.updateAIO(aio);
			}else if(judge.equals("03")) {
				Sensor sensor=new Sensor();
				sensor.setStatus(0);
				service.updateSensor(sensor);
			}else if(judge.equals("04")) {
				Controller controller =new Controller();
				controller.setStatus(0);
				service.updateController(controller);
			}	
		 Alarm alarm=new Alarm();
			alarm.setDeviceSn(deviceSn);
             alarm.setWay(way);	
             try {
				alarm.setCreateDate(sdf.parse(sdf.format(new Date())));
			} catch (ParseException e) {
				logger.debug("日期转换错误");
			}
             alarm.setAlarmType(4);
             service.save(alarm);
				response(8);
	}

	// 增氧机打开和关闭时间记录
	public static void oxygenTimeCMD(SelectionKey key) throws IOException {
		 preHandle(key);
		/*byte power6 = data[7];
		byte[] byteTime = new byte[5];
		CommonUtils.arrayHandle(data, byteTime, 8, 0, 5);
		String time = "20"+Integer.toString(byteTime[0]& 0xFF)+Integer.toString(byteTime[1]& 0xFF)+Integer.toString(byteTime[2]& 0xFF)+Integer.toString(byteTime[3]& 0xFF)+Integer.toString(byteTime[4]& 0xFF);
		long timeLong = 0;
		try
		{
			Date date = CommonUtils.stringToDate(time,"yyyyMMddHHmm");
			timeLong = date.getTime();
		} 
		catch (ParseException e)
		{
			e.printStackTrace();
			return;
		}
		byte check6 = data[13];
		String suffix6 = CommonUtils.printHexStringMerge(data,14,4);*/
			response(14);
	}

	// 服务器开关增氧机
	public static Map<String, Object> serverOnOffOxygenCMD(Timer timer,int operation) {
		SocketChannel channel=clientMap.get(timer.getDevice_sn());
		if(channel==null) {
        	return RESCODE.NOT_OPEN.getJSONRES();
        }
		if(!channel.isConnected()) {
			return RESCODE.CONNECTION_CLOSED.getJSONRES();
		}
		byte[] request = null;
		if(operation==1) {
		String open=StringUtils.add(timer.getDevice_sn(), way, 7)
				.append("01")
                .append("          ")
				.toString();
		request=CommonUtils.toByteArray(open);
		request[8]=CommonUtils.arrayMerge(request, 2, 6);
		CommonUtils.addSuffix(request, 9);
		}else {
			request[7]=(byte)0x00;
			request[8]=CommonUtils.arrayMerge(request, 2, 6);
		}
	    ByteBuffer outBuffer = ByteBuffer.wrap(request);
	    try {
			channel.write(outBuffer);
		} catch (IOException e) {
			return RESCODE.SEND_FAILED.getJSONRES();
		}
	
	    return responseToBrowser();
        

	}

	// 服务器设置一键自动
	public static Map<String, Object> serverSetAutoCMD(String deviceSn) {
		SocketChannel channel=clientMap.get(deviceSn);
		if(channel==null) {
        	return RESCODE.NOT_OPEN.getJSONRES();
        }
		if(!channel.isConnected()) {
			return RESCODE.CONNECTION_CLOSED.getJSONRES();
		}
		byte[] request = null;
		String temp=StringUtils.add(deviceSn, way, 8)
                .append("          ")
				.toString();
		request=CommonUtils.toByteArray(temp);
		request[7]=CommonUtils.arrayMerge(request, 2, 5);
		CommonUtils.addSuffix(request, 8);
	    ByteBuffer outBuffer = ByteBuffer.wrap(request);
	    try {
			channel.write(outBuffer);
		} catch (IOException e) {
			return RESCODE.SEND_FAILED.getJSONRES();
		}
	
	    return responseToBrowser();
	}

	// 服务器设置设备使用哪路传感器
	public static Map<String, Object> serverSetpathCMD(String deviceSn) {
		SocketChannel channel=clientMap.get(deviceSn);
		if(channel==null) {
        	return RESCODE.NOT_OPEN.getJSONRES();
        }
		if(!channel.isConnected()) {
			return RESCODE.CONNECTION_CLOSED.getJSONRES();
		}
		byte[] request = null;
		String temp=StringUtils.add(deviceSn, way, 12)
                .append("          ")
				.toString();
		request=CommonUtils.toByteArray(temp);
		request[8]=CommonUtils.arrayMerge(request, 2, 6);
		CommonUtils.addSuffix(request, 9);
	    ByteBuffer outBuffer = ByteBuffer.wrap(request);
	    try {
			channel.write(outBuffer);
		} catch (IOException e) {
			return RESCODE.SEND_FAILED.getJSONRES();
		}
	
	    return responseToBrowser();
	}

	// 服务器校准命令
	public static Map<String, Object> serverCheckCMD(String deviceSn) {
		SocketChannel channel=clientMap.get(deviceSn);
		if(channel==null) {
        	return RESCODE.NOT_OPEN.getJSONRES();
        }
		if(!channel.isConnected()) {
			return RESCODE.CONNECTION_CLOSED.getJSONRES();
		}
		byte[] request = null;
		//第一路开始校准
		String firstpath=StringUtils.add(deviceSn, 1, 13)
                .append("          ")
				.toString();
		request=CommonUtils.toByteArray(firstpath);
		request[7]=CommonUtils.arrayMerge(request, 2, 5);
		CommonUtils.addSuffix(request, 8);
	    ByteBuffer outBuffer = ByteBuffer.wrap(request);
	    try {
			channel.write(outBuffer);
		} catch (IOException e) {
			return RESCODE.SEND_FAILED.getJSONRES();
		}
	    //第二路开始校准
	    String secondpath=StringUtils.add(deviceSn, 2, 13)
                .append("          ")
				.toString();
	    request=CommonUtils.toByteArray(secondpath);
		request[7]=CommonUtils.arrayMerge(request, 2, 5);
		CommonUtils.addSuffix(request, 8);
	     outBuffer = ByteBuffer.wrap(request);
	    try {
			channel.write(outBuffer);
		} catch (IOException e) {
			return RESCODE.SEND_FAILED.getJSONRES();
		}
	
	    return responseToBrowser();
	}

	// 五分钟上传一次溶氧，水温和PH值信息
	public static void timingDataCMD(SelectionKey key) throws IOException {
		 preHandle(key);
		byte[] byteOxygen= new byte[4];
		CommonUtils.arrayHandle(data, byteOxygen, 7, 0, 4);
		float oxygen= CommonUtils.byte2float(byteOxygen,0);
		byte[] byteWaterTemp = new byte[4];
		CommonUtils.arrayHandle(data, byteWaterTemp, 11, 0, 4);
		float waterTemp= CommonUtils.byte2float(byteWaterTemp,0);
		byte[] bytePhValue=new byte[4];
		CommonUtils.arrayHandle(data, bytePhValue, 15, 0, 4);
		float phValue=CommonUtils.byte2float(bytePhValue,0);
		String receiveTime = sdf.format(new Date());
		//byte check3 = data[19];
		//String suffix3 = CommonUtils.printHexStringMerge(data,20,4);
		
		//SocketSerivce service =(SocketSerivce) ApplicationUtil.getBean("socketSerivce");
		Sensor_Data sData=new Sensor_Data();
		sData.setDevice_sn(deviceSn);
		sData.setWay(way);
		sData.setOxygen(oxygen);
		sData.setWater_temperature(waterTemp);
		sData.setpH_value(phValue);
		try {
			sData.setReceiveTime(sdf.parse(receiveTime));
		} catch (ParseException e) {
		logger.debug("日期转换错误");
		}
		service.save(sData);

			response(16);
	}
	
	public static void response(int dataStart) throws IOException {
		response = new byte[12];
		CommonUtils.arrayHandle(data, response, 0, 0, 7);
		response[7]=CommonUtils.arrayMerge(response, 2, 5);
		CommonUtils.arrayHandle(data, response, dataStart, 8, 4);
		ByteBuffer outBuffer = ByteBuffer.wrap(response);
		readChannel.write(outBuffer);// 将消息回送给客户端
		System.out.println("cmd代码处理完");
	}
	
	public static void statusHandle(byte[] status,List<Broken> brokenlist) {
		
		String statusStr=CommonUtils.printHexStringMerge(status, 0, 2);
		Sensor sensor=service.findSensorByDeviceSn(deviceSn);
		String relation=sensor.getRelationId();
		switch (statusStr.substring(0,1)) {
		case "0":
			//水泵关闭故障
			selfTestBrokenHandle(relation, EntityModel.ENTITY_PUMP, EntityType.PUMP_OFF_BROKEN,"水泵关闭故障",brokenlist);
			break;
		case "1":
			//水泵打开故障
			selfTestBrokenHandle(relation, EntityModel.ENTITY_PUMP, EntityType.PUMP_ON_BROKEN,"水泵打开故障",brokenlist);
			break;
		case "2":
			//水泵低电流故障
			selfTestBrokenHandle(relation, EntityModel.ENTITY_PUMP, EntityType.PUMP_LOWCURRENT_BROKEN,"水泵低电流故障",brokenlist);
			break;
		case "3":
			//水泵高电流故障
			selfTestBrokenHandle(relation, EntityModel.ENTITY_PUMP, EntityType.HIGH_LIMIT_BROKEN,"水泵高电流故障",brokenlist);
			break;
		default:
			break;
		}
		
		switch (statusStr.substring(1,2)) {
		case "0":
			//PH故障
			selfTestBrokenHandle(relation, EntityModel.ENTITY_PH, EntityType.BROKEN,"PH故障",brokenlist);
			break;
		case "1":
			//PH低限故障
			selfTestBrokenHandle(relation, EntityModel.ENTITY_PH, EntityType.LOW_LIMIT_BROKEN,"PH低限故障",brokenlist);
			break;
		case "2":
			//PH高限故障
			selfTestBrokenHandle(relation, EntityModel.ENTITY_PH, EntityType.HIGH_LIMIT_BROKEN,"PH高限故障",brokenlist);
			break;
		default:
			break;
		}
		
		switch (statusStr.substring(2,3)) {
		case "0":
			//溶氧值故障
			selfTestBrokenHandle(relation, EntityModel.ENTITY_OXYGEN, EntityType.BROKEN,"溶氧值故障",brokenlist);
			break;
		case "1":
			//溶氧值低限故障
			selfTestBrokenHandle(relation, EntityModel.ENTITY_OXYGEN, EntityType.LOW_LIMIT_BROKEN,"溶氧值低限故障",brokenlist);
			break;
		case "2":
			//溶氧值高限故障
			selfTestBrokenHandle(relation, EntityModel.ENTITY_OXYGEN, EntityType.HIGH_LIMIT_BROKEN,"溶氧值高限故障",brokenlist);
			break;
		default:
			break;
		}
		
		switch (statusStr.substring(3,4)) {
		case "0":
			//温度故障
			selfTestBrokenHandle(relation, EntityModel.ENTITY_TEMPERATURE, EntityType.BROKEN,"温度故障",brokenlist);
			break;
		case "1":
			//温度低限故障
			selfTestBrokenHandle(relation, EntityModel.ENTITY_TEMPERATURE, EntityType.LOW_LIMIT_BROKEN,"温度低限故障",brokenlist);
			break;
		case "2":
			//温度高限故障
			selfTestBrokenHandle(relation, EntityModel.ENTITY_TEMPERATURE, EntityType.HIGH_LIMIT_BROKEN,"温度高限故障",brokenlist);
			break;
		case "4":
			//温度断开故障
			selfTestBrokenHandle(relation, EntityModel.ENTITY_TEMPERATURE, EntityType.TEMPORETURE_CLOSED_BROKEN,"温度断开故障",brokenlist);
			break;
		default:
			break;
		}
		WXUser wxuser=new WXUser();
		wxuser=service.findWXUserById(relation);
		BrokenMSG bs=new BrokenMSG();
		WechatTemplateMessage.sendBrokenMSG(bs.getMSG(),wxuser.getOpenId());
		bs.clear();//把所有故障信息拼接完毕推送给前台
	}
	
	public static void selfTestBrokenHandle(String relation,int entityModel,int entityType,String brokenmsg,List<Broken> brokenlist) {
		if(relation!=null) {
			if(relation.contains("WX")) {
				//是微信用户就推送给前台
				BrokenMSG bs=new BrokenMSG();
				bs.setMSG("brokenmsg");
			}
			 Broken  broken=new Broken();
				broken.setCreateDate(new Date());
				broken.setEntityModel(entityModel);
				broken.setEntityType(entityType);
				broken.setDeviceSn(deviceSn);
				brokenlist.add(broken);
				service.save(broken);
			
		}
		
	}
	//while循环等待反馈将feedback状态变为true，检测到了就立即返回给浏览器，否则继续，或者等待时间超过10秒，返回失败
	public static Map<String, Object> responseToBrowser(){
		 long start=System.currentTimeMillis();
		    long end=0;
		    while(true) {
		    	if(getFeedback().getAndSet(false)) {
		    		return RESCODE.SUCCESS.getJSONRES();
		    	}
		    	
		    	end=System.currentTimeMillis();
		    	if(end-start>=10000) {
		    		return RESCODE.NOT_RECEIVED.getJSONRES();
		    	}
		    }
	}
}
