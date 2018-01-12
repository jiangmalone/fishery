package com.geariot.platform.fishery.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.geariot.platform.fishery.dao.AlarmDao;
import com.geariot.platform.fishery.dao.LimitDao;
import com.geariot.platform.fishery.dao.Sensor_DataDao;
import com.geariot.platform.fishery.entities.Alarm;
import com.geariot.platform.fishery.entities.Limit_Install;
import com.geariot.platform.fishery.entities.SelfTest;
import com.geariot.platform.fishery.entities.Sensor_Data;
import com.geariot.platform.fishery.model.RESCODE;
import com.geariot.platform.fishery.service.SocketSerivce;
import com.geariot.platform.fishery.utils.ApplicationUtil;
import com.geariot.platform.fishery.utils.CommonUtils;
import com.geariot.platform.fishery.utils.StringUtils;

public class CMDUtils {
	private static Logger logger = Logger.getLogger(CMDUtils.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Map<String, SocketChannel> clientMap = new ConcurrentHashMap<String, SocketChannel>();
	private static	byte[] response = null;
	private static byte[] data=null;
	private static SocketChannel readChannel=null;
	private static String deviceSn;
	private static byte way;
	private static AtomicBoolean feedback=new AtomicBoolean();

	
	public static AtomicBoolean getFeedback() {
		return feedback;
	}
	public static void setFeedback(AtomicBoolean feedback) {
		CMDUtils.feedback = feedback;
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
	       SocketSerivce service =(SocketSerivce) ApplicationUtil.getBean("socketSerivce");
	       System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		    byte ac = data[7];
			byte[] byteLongitude =new byte[4];
			CommonUtils.arrayHandle(data, byteLongitude, 8, 0, 4);
			float longitude= CommonUtils.byte2float(byteLongitude,0);
			byte[] byteLatitude = new byte[4];
			CommonUtils.arrayHandle(data, byteLatitude, 12, 0, 4);
			float latitude= CommonUtils.byte2float(byteLatitude,0);
			byte sensor = data[16];//传感器是否正常
			byte gprs = data[17];
			byte check = data[18];
			String suffix0 = CommonUtils.printHexStringMerge(data,19,4);
           SelfTest selfTest=new SelfTest();
           selfTest.setDevice_sn(deviceSn);
           selfTest.setPath(way);
           selfTest.setAc(ac);
           selfTest.setLatitude(latitude);
           selfTest.setLongitude(longitude);
           selfTest.setGprs(gprs);
           selfTest.setStatus(sensor);
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
			byte check1 = data[19];
			String suffix1 = CommonUtils.printHexStringMerge(data,20,4);
			SocketSerivce service =(SocketSerivce) ApplicationUtil.getBean("socketSerivce");
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
	public static Map<String, Object> downLimitCMD(String deviceSn,Limit_Install limit) {
		SocketChannel channel=clientMap.get(deviceSn);
		if(channel==null) {
        	return RESCODE.NOT_OPEN.getJSONRES();
        }
		if(!channel.isConnected()) {
			return RESCODE.CONNECTION_CLOSED.getJSONRES();
		}
		byte[] request = null;
		String temp=StringUtils.add(deviceSn, limit.getWay(), 2)
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
	    try {
			Thread.sleep(1000);//异步化
		} catch (InterruptedException e) {
		}
	    if(!getFeedback().getAndSet(false)) {
	    	throw new RuntimeException("未收到写指令的反馈消息");
	    }
	    return RESCODE.SUCCESS.getJSONRES();
        
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
			byte check3 = data[15];
			String suffix3 = CommonUtils.printHexStringMerge(data,16,4);
			SocketSerivce service =(SocketSerivce) ApplicationUtil.getBean("socketSerivce");
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
		byte check4 = data[7];
			String suffix4 = CommonUtils.printHexStringMerge(data,8,4);
			/*Dao_Alarm dao_Alarm = new Dao_Alarm();
			row_count = dao_Alarm.insertOne(String.valueOf(id), way, 1);*/
			//AlarmDao alarmDao=(AlarmDao) ApplicationUtil.getBean("alarmDao");
			SocketSerivce service =(SocketSerivce) ApplicationUtil.getBean("socketSerivce");
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
		byte check5 = data[7];
			String suffix5 = CommonUtils.printHexStringMerge(data,8,4);
			/*Dao_Alarm dao_Alarm1 = new Dao_Alarm();
			row_count = dao_Alarm1.insertOne(String.valueOf(id), way, 2);*/
			//AlarmDao alarmDao=(AlarmDao) ApplicationUtil.getBean("alarmDao");
			SocketSerivce service =(SocketSerivce) ApplicationUtil.getBean("socketSerivce");
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
		byte check9 = data[7];
			System.out.println("check = "+check9);
			String suffix9 = CommonUtils.printHexStringMerge(data,8,4);
			System.out.println("suffix = "+suffix9);
			/*Dao_Alarm dao_Alarm3 = new Dao_Alarm();
			row_count = dao_Alarm3.insertOne(String.valueOf(id), way, 3);*/
			//AlarmDao alarmDao=(AlarmDao) ApplicationUtil.getBean("alarmDao");
			SocketSerivce service =(SocketSerivce) ApplicationUtil.getBean("socketSerivce");
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
		byte check10 = data[7];
			System.out.println("check = "+check10);
			String suffix10 = CommonUtils.printHexStringMerge(data,8,4);
			System.out.println("suffix = "+suffix10);
			/*Dao_Alarm dao_Alarm4 = new Dao_Alarm();
			row_count = dao_Alarm4.insertOne(String.valueOf(id), way, 4);*/
			//AlarmDao alarmDao=(AlarmDao) ApplicationUtil.getBean("alarmDao");
			SocketSerivce service =(SocketSerivce) ApplicationUtil.getBean("socketSerivce");
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
		byte power6 = data[7];
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
		String suffix6 = CommonUtils.printHexStringMerge(data,14,4);
			response(14);
	}

	// 服务器开关增氧机
	public static void serverOnOffOxygenCMD() {

	}

	// 服务器设置一键自动
	public static void serverSetAutoCMD() {

	}

	// 服务器设置设备使用哪路传感器
	public static void serverSetpathCMD() {

	}

	// 服务器校准命令
	public static void serverCheckCMD() {

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
		byte check3 = data[19];
		String suffix3 = CommonUtils.printHexStringMerge(data,20,4);
		//Sensor_DataDao sDataDao=(Sensor_DataDao) ApplicationUtil.getBean("sensor_DataDao");
		SocketSerivce service =(SocketSerivce) ApplicationUtil.getBean("socketSerivce");
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
}
