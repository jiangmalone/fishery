package com.geariot.platform.fishery.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.aliyuncs.exceptions.ClientException;
import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.AeratorStatus;
import com.geariot.platform.fishery.entities.Alarm;
import com.geariot.platform.fishery.entities.Broken;
import com.geariot.platform.fishery.entities.Controller;
import com.geariot.platform.fishery.entities.DataAlarm;
import com.geariot.platform.fishery.entities.Limit_Install;
import com.geariot.platform.fishery.entities.Pond;
import com.geariot.platform.fishery.entities.PondFish;
import com.geariot.platform.fishery.entities.SelfTest;
import com.geariot.platform.fishery.entities.Sensor;
import com.geariot.platform.fishery.entities.Sensor_Data;
import com.geariot.platform.fishery.entities.WXUser;
import com.geariot.platform.fishery.model.BrokenMSG;
import com.geariot.platform.fishery.model.EntityModel;
import com.geariot.platform.fishery.model.EntityType;
import com.geariot.platform.fishery.model.RESCODE;
import com.geariot.platform.fishery.service.SocketSerivce;
import com.geariot.platform.fishery.utils.ApplicationUtil;
import com.geariot.platform.fishery.utils.CommonUtils;
import com.geariot.platform.fishery.utils.JudgeAlarmRangeUtils;
import com.geariot.platform.fishery.utils.StringUtils;
import com.geariot.platform.fishery.utils.VmsUtils;
import com.geariot.platform.fishery.wxutils.WechatSendMessageUtils;

public class CMDUtils {
	private static Logger logger = Logger.getLogger(CMDUtils.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Map<String, SocketChannel> clientMap = new ConcurrentHashMap<String, SocketChannel>();
	private static SocketSerivce service = (SocketSerivce) ApplicationUtil.getBean("socketSerivce");
    private static BrokenMSG bs=new BrokenMSG();
    public static Map<String,String> msg=new ConcurrentHashMap<String,String>();
	public static Map<String, SocketChannel> getclientMap() {
		return clientMap;
	}

	// 自检
	public static void selfTestCMD(byte[] data, SocketChannel readChannel, String deviceSn, byte way)
			throws IOException {

		logger.debug("设备号为:" + deviceSn + "的设备开机自检了，是第" + way + "路");
		clientMap.put(deviceSn, readChannel);
		byte ac = data[7];
		byte[] byteLongitude = new byte[4];
		CommonUtils.arrayHandle(data, byteLongitude, 8, 0, 4);
		float longitude = CommonUtils.byte2float(byteLongitude, 0);
		byte[] byteLatitude = new byte[4];
		CommonUtils.arrayHandle(data, byteLatitude, 12, 0, 4);
		float latitude = CommonUtils.byte2float(byteLatitude, 0);
		
		byte[] status = new byte[2];
		status[0] = data[16];// 传感器和水泵状态
		status[1] = data[17];
		List<Broken> brokenlist = new ArrayList<Broken>();
		statusHandle(status, brokenlist, deviceSn);
		byte gprs = data[18];
		SelfTest selfTest = new SelfTest();
		selfTest.setDevice_sn(deviceSn);
		selfTest.setPath(way);
		selfTest.setAc(ac);
		selfTest.setLatitude(latitude);
		selfTest.setLongitude(longitude);
		selfTest.setGprs(gprs);
		selfTest.setBroken(brokenlist);
		selfTest.setCreateDate(new Date());
		logger.debug("设备号为:" + deviceSn + "的设备自检分析完毕，是第" + way + "路，准备存入数据库");
		service.save(selfTest);
		AIO aio=service.findAIOByDeviceSn(deviceSn);
		if(aio!=null) {
			if(aio.getStatus()==1) {
			aio.setStatus(0);
			service.updateAIO(aio);
			}
		}
			
		response(20, data, readChannel);
	}

	// 下位机设限上传给服务器
	public static void uploadLimitCMD(byte[] data, SocketChannel readChannel, String deviceSn, byte way)
			throws IOException {

		byte[] byteHigh = new byte[4];
		CommonUtils.arrayHandle(data, byteHigh, 7, 0, 4);
		float high = CommonUtils.byte2float(byteHigh, 0);
		byte[] byteUp = new byte[4];
		CommonUtils.arrayHandle(data, byteUp, 11, 0, 4);
		float up = CommonUtils.byte2float(byteUp, 0);
		byte[] bytelow = new byte[4];
		CommonUtils.arrayHandle(data, bytelow, 15, 0, 4);
		float low = CommonUtils.byte2float(bytelow, 0);
		logger.debug("服务器接收设备号为:" + deviceSn + "的设备，的第" + way + "路的低限为:" + low + " 高限为:" + high + " 上限为:" + up);
		Limit_Install limit=service.findLimitByDeviceSnAndWay(deviceSn, way);
		if(limit==null) {
			limit = new Limit_Install();
			limit.setDevice_sn(deviceSn);
			limit.setWay(way);
			limit.setUp_limit(up);
			limit.setHigh_limit(high);
			limit.setLow_limit(low);
			service.save(limit);
		}else {
			limit.setHigh_limit(high);
			limit.setLow_limit(low);
			limit.setUp_limit(up);
			service.updateLimit(limit);
		}
		
		response(20, data, readChannel);
	}

	// 服务器设限下发给终端
	public static Map<String, Object> downLimitCMD(Limit_Install limit) {
		logger.debug("服务器设置设备号为:" + limit.getDevice_sn() + "的设备，的第" + limit.getWay() + "路的低限为:" + limit.getLow_limit()
				+ " 高限为:" + limit.getHigh_limit() + " 上限为:" + limit.getUp_limit());
		SocketChannel channel = clientMap.get(limit.getDevice_sn());
		if (channel == null) {
			return RESCODE.NOT_OPEN.getJSONRES();
		}
		if (!channel.isConnected()) {
			return RESCODE.CONNECTION_CLOSED.getJSONRES();
		}
		byte[] request = null;
		String temp = StringUtils.add(limit.getDevice_sn(), limit.getWay(), 2)
				.append(CommonUtils.reverse(Integer.toHexString(Float.floatToIntBits(limit.getHigh_limit()))))
				.append(CommonUtils.reverse(Integer.toHexString(Float.floatToIntBits(limit.getUp_limit()))))
				.append(CommonUtils.reverse(Integer.toHexString(Float.floatToIntBits(limit.getLow_limit())))).append("          ")
				.toString();

		request = CommonUtils.toByteArray(temp);
	
		request[19] = CommonUtils.arrayMerge(request, 2, 17);
		CommonUtils.addSuffix(request, 20);
		
		ByteBuffer outBuffer = ByteBuffer.wrap(request);

		try {
			channel.write(outBuffer);
		} catch (IOException e) {
			return RESCODE.SEND_FAILED.getJSONRES();
		}
		//return responseToBrowser("2", limit.getDevice_sn());
		System.out.println("22222");
		msg.put(limit.getDevice_sn()+"2", "低限为:"+limit.getLow_limit()+"高限为:"+limit.getHigh_limit()+"上限为:"+limit.getUp_limit());
		System.out.println("11111111111");
       return RESCODE.SUCCESS.getJSONRES();
	}

	// 5分钟一次上传溶氧和水温值
	public static void timingUploadCMD(byte[] data, SocketChannel readChannel, String deviceSn, byte way)
			throws IOException {

		byte[] byteOxygen = new byte[4];
		CommonUtils.arrayHandle(data, byteOxygen, 7, 0, 4);
		float oxygen = CommonUtils.byte2float(byteOxygen, 0);
		byte[] byteWaterTemp = new byte[4];
		CommonUtils.arrayHandle(data, byteWaterTemp, 11, 0, 4);
		float waterTemp = CommonUtils.byte2float(byteWaterTemp, 0);
		Sensor_Data sData = new Sensor_Data();
		sData.setDevice_sn(deviceSn);
		sData.setWay(way);
		sData.setOxygen(oxygen);
		sData.setWater_temperature(waterTemp);
		logger.debug("服务器接收设备号为:" + deviceSn + "的设备，的第" + way + "路的溶氧值为:" + oxygen + "水温:" + waterTemp);
		sData.setReceiveTime(new Date());
		service.update(sData);
		
		 AIO aio=service.findAIOByDeviceSn(deviceSn);
		 String relation=null;
		 String openId=null;
		 Integer pondId=null;
		if(null!=aio) {
				pondId=(Integer)aio.getPondId();
				relation= aio.getRelation();
			}
		 if(relation!=null&&relation.contains("WX")) {
			 WXUser wxuser=service.findWXUserByRelation(relation);
			 if(null!=wxuser) {
				openId= wxuser.getOpenId();
			 }
		 } 
		 DataAlarm da=new DataAlarm();
			da.setCreateDate(new Date());
			da.setDeviceSn(deviceSn);
			da.setRelation(relation);
			da.setWay(way);
			if(aio!=null) {
			da.setDeviceName(aio.getName());
			}else {
				da.setDeviceName(null);
			}
			Pond pond=null;
			if(pondId!=null) {
			pond=service.findPondById(pondId);
			}
			if(pond!=null) {
			da.setPondName(pond.getName());
			}else {
				da.setPondName(null);
			}
		doJudge(deviceSn, waterTemp, oxygen,-1,openId,da);//判断上传的数据是否正常,因为没有PH值所以参数为-1,然后在程序里面再判断为-1代表不支持PH
		AIO aio2=service.findAIOByDeviceSn(deviceSn);
		if(aio2!=null) {
			if(aio2.getStatus()==1) {
			aio2.setStatus(0);
			service.updateAIO(aio2);
			}
		}

		response(16, data, readChannel);
	}

	// 增氧机缺相报警
	public static void oxygenAlarmCMD(byte[] data, SocketChannel readChannel, String deviceSn, byte way)
			throws IOException {
		logger.debug("服务器接收设备号为:" + deviceSn + "的设备，的第" + way + "路缺相报警");
		String judge = deviceSn.substring(0, 2);
		if (judge.equals("01") || judge.equals("02")) {
			AIO aio = service.findAIOByDeviceSnAndWay(deviceSn,way);
			if (aio == null) {
				response(8, data, readChannel);
				return;
			}
			aio.setStatus(3);
			service.updateAIO(aio);
			WXUser wxUser = null;
			wxUser = service.findWXUserByDeviceSn(deviceSn);
			if(wxUser!=null && wxUser.getOpenId()!=null){
				WechatSendMessageUtils.sendWechatVoltageMessages("缺相报警", wxUser.getOpenId(), deviceSn);
	        }
		} else if (judge.equals("03")) {
			Sensor sensor = service.findSensorByDeviceSnAndWay(deviceSn,way);
			if (sensor == null) {
				response(8, data, readChannel);
				return;
			}
			sensor.setStatus(3);
			service.updateSensor(sensor);
			WXUser wxUser = null;
			wxUser = service.findWXUserByDeviceSnSensor(deviceSn);
			if(wxUser!=null && wxUser.getOpenId()!=null){
				WechatSendMessageUtils.sendWechatVoltageMessages("缺相报警", wxUser.getOpenId(), deviceSn);
	        }
		} else if (judge.equals("04")) {
			Controller controller = service.findControllerByDeviceSnAndWay(deviceSn,way);
			if (controller == null) {
				response(8, data, readChannel);
				return;
			}
			controller.setStatus(3);
			service.updateController(controller);
			WXUser wxUser = null;
			wxUser = service.findWXUserByDeviceSnController(deviceSn);
			if(wxUser!=null && wxUser.getOpenId()!=null){
				WechatSendMessageUtils.sendWechatVoltageMessages("缺相报警", wxUser.getOpenId(), deviceSn);
	        }
		}
		Alarm alarm = new Alarm();
		alarm.setDeviceSn(deviceSn);
		alarm.setWay(way);
		alarm.setCreateDate(new Date());
		alarm.setAlarmType(1);
		service.save(alarm);
		response(8, data, readChannel);
	}

	// 220v断电报警
	public static void voltageAlarmCMD(byte[] data, SocketChannel readChannel, String deviceSn, byte way)
			throws IOException, ClientException {
		logger.debug("服务器接收设备号为:" + deviceSn + "的设备，的第" + way + "路220V断电报警");
		String judge = deviceSn.substring(0, 2);
		if (judge.equals("01") || judge.equals("02")) {
			AIO aio = service.findAIOByDeviceSn(deviceSn);
			if (aio == null) {
				response(8, data, readChannel);
				return;
			}
			aio.setStatus(2);
			service.updateAIO(aio);
			WXUser wxUser = null;
			wxUser = service.findWXUserByDeviceSn(deviceSn);
			if(wxUser!=null && wxUser.getOpenId()!=null){
				WechatSendMessageUtils.sendWechatVoltageMessages("断电报警", wxUser.getOpenId(), deviceSn);
	        }
			if(wxUser.getPhone()!=null){
				VmsUtils.singleCallByTts(wxUser.getPhone(), "TTS_126781509", "param");
			}
		} else if (judge.equals("03")) {
			Sensor sensor = service.findSensorByDeviceSn(deviceSn);
			if (sensor == null) {
				response(8, data, readChannel);
				return;
			}
			sensor.setStatus(2);
			service.updateSensor(sensor);
			WXUser wxUser = null;
			wxUser = service.findWXUserByDeviceSnSensor(deviceSn);
			if(wxUser!=null && wxUser.getOpenId()!=null){
				WechatSendMessageUtils.sendWechatVoltageMessages("断电报警", wxUser.getOpenId(), deviceSn);
	        }
		} else if (judge.equals("04")) {
			Controller controller = service.findControllerByDeviceSn(deviceSn);
			if (controller == null) {
				response(8, data, readChannel);
				return;
			}
			controller.setStatus(2);
			service.updateController(controller);
			WXUser wxUser = null;
			wxUser = service.findWXUserByDeviceSnController(deviceSn);
			if(wxUser!=null && wxUser.getOpenId()!=null){
				WechatSendMessageUtils.sendWechatVoltageMessages("断电报警", wxUser.getOpenId(), deviceSn);
	        }
		}
		Alarm alarm = new Alarm();
		alarm.setDeviceSn(deviceSn);
		alarm.setWay(way);
		alarm.setCreateDate(new Date());
		alarm.setAlarmType(2);
		service.save(alarm);
		response(8, data, readChannel);
	}

	// 增氧机打开后半小时内效果不明显报警
	public static void oxygenExceptionAlarmCMD(byte[] data, SocketChannel readChannel, String deviceSn, byte way)
			throws IOException {
		logger.debug("服务器接收设备号为:" + deviceSn + "的设备，的第" + way + "路增氧机打开后半小时内效果不明显报警");

		String judge = deviceSn.substring(0, 2);
		if (judge.equals("01") || judge.equals("02")) {
			AIO aio = service.findAIOByDeviceSnAndWay(deviceSn,way);
			if (aio == null) {
				response(8, data, readChannel);
				return;
			}
			aio.setStatus(4);
			service.updateAIO(aio);
		} else if (judge.equals("03")) {
			Sensor sensor = service.findSensorByDeviceSnAndWay(deviceSn,way);
			if (sensor == null) {
				response(8, data, readChannel);
				return;
			}
			sensor.setStatus(4);
			service.updateSensor(sensor);
		} else if (judge.equals("04")) {
			Controller controller = service.findControllerByDeviceSnAndWay(deviceSn,way);
			if (controller == null) {
				response(8, data, readChannel);
				return;
			}
			controller.setStatus(4);
			service.updateController(controller);
		}
		Alarm alarm = new Alarm();
		alarm.setDeviceSn(deviceSn);
		alarm.setWay(way);
		alarm.setCreateDate(new Date());
		alarm.setAlarmType(3);
		service.save(alarm);
		response(8, data, readChannel);
	}

	// 取消所有报警
	public static void cancelAllAlarmCMD(byte[] data, SocketChannel readChannel, String deviceSn, byte way)
			throws IOException {
		logger.debug("服务器接收设备号为:" + deviceSn + "的设备，的第" + way + "路取消所有报警");
		String judge = deviceSn.substring(0, 2);
		if (judge.equals("01") || judge.equals("02")) {
			AIO aio = service.findAIOByDeviceSnAndWay(deviceSn,way);
			if (aio == null) {
				response(8, data, readChannel);
				return;
			}
			aio.setStatus(0);
			service.updateAIO(aio);
		} else if (judge.equals("03")) {
			Sensor sensor = service.findSensorByDeviceSnAndWay(deviceSn,way);
			if (sensor == null) {
				response(8, data, readChannel);
				return;
			}
			sensor.setStatus(0);
			service.updateSensor(sensor);
		} else if (judge.equals("04")) {
			Controller controller = service.findControllerByDeviceSnAndWay(deviceSn,way);
			if (controller == null) {
				response(8, data, readChannel);
				return;
			}
			controller.setStatus(0);
			service.updateController(controller);
		}
		Alarm alarm = new Alarm();
		alarm.setDeviceSn(deviceSn);
		alarm.setWay(way);
		
		alarm.setCreateDate(new Date());
		
		alarm.setAlarmType(4);
		service.save(alarm);
		response(8, data, readChannel);
	}

	// 增氧机打开和关闭时间记录
	public static void oxygenTimeCMD(byte[] data, SocketChannel readChannel, String deviceSn, byte way)
			throws IOException {
		
		/*
		byte power6 = data[7];
		byte[] byteTime = new byte[5];
		CommonUtils.arrayHandle(data, byteTime, 8, 0, 5);
		String time = "20" + Integer.toString(byteTime[0] & 0xFF) + Integer.toString(byteTime[1] & 0xFF)
				+ Integer.toString(byteTime[2] & 0xFF) + Integer.toString(byteTime[3] & 0xFF)
				+ Integer.toString(byteTime[4] & 0xFF);
		long timeLong = 0;
		try {
			Date date = CommonUtils.stringToDate(time, "yyyyMMddHHmm");
			timeLong = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		byte check6 = data[13];
		String suffix6 = CommonUtils.printHexStringMerge(data, 14, 4);
		 
		logger.debug("增氧机开关记录");*/
		response(14, data, readChannel);
	}

	// 参数operation=1为打开增氧机，为0是关闭增氧机
	public static Map<String, Object> serverOnOffOxygenCMD(String deviceSn, int way, int operation) {
		SocketChannel channel = clientMap.get(deviceSn);
		if (channel == null) {
			return RESCODE.NOT_OPEN.getJSONRES();
		}
		if (!channel.isConnected()) {
			return RESCODE.CONNECTION_CLOSED.getJSONRES();
		}
		byte[] request = null;

		if (operation == 1) {
			logger.debug("服务器打开设备号为:" + deviceSn + "的增氧机，的第" + way + "路");

			String openFirstPath = StringUtils.add(deviceSn, way, 7).append("01").append("          ").toString();

			request = CommonUtils.toByteArray(openFirstPath);

			AeratorStatus status = service.findByDeviceSnAndWay(deviceSn, way);

			status.setOn_off(true);

		} else {

			logger.debug("服务器关闭设备号为:" + deviceSn + "的增氧机，的第" + way + "路");
			String close = StringUtils.add(deviceSn, way, 7).append("00").append("          ").toString();

			request = CommonUtils.toByteArray(close);

			AeratorStatus status = service.findByDeviceSnAndWay(deviceSn, way);

			status.setOn_off(false);

		}
		request[8] = CommonUtils.arrayMerge(request, 2, 6);

		CommonUtils.addSuffix(request, 9);

		ByteBuffer outBuffer = ByteBuffer.wrap(request);

		try {
			channel.write(outBuffer);

		} catch (IOException e) {
			return RESCODE.SEND_FAILED.getJSONRES();
		}
         msg.put(deviceSn+"7",String.valueOf(operation));
		//return responseToBrowser("7", deviceSn);
		 return RESCODE.SUCCESS.getJSONRES();
	}

	// 服务器设置一键自动
	public static Map<String, Object> serverSetAutoCMD(String deviceSn, int way) {
		logger.debug("服务器设置一键自动设备编号和路分别为:" + deviceSn + "第" + way + "路");
		SocketChannel channel = clientMap.get(deviceSn);
		if (channel == null) {
			return RESCODE.NOT_OPEN.getJSONRES();
		}
		if (!channel.isConnected()) {
			return RESCODE.CONNECTION_CLOSED.getJSONRES();
		}
		byte[] request = null;
		String temp = StringUtils.add(deviceSn, way, 8).append("          ").toString();
		request = CommonUtils.toByteArray(temp);
		request[7] = CommonUtils.arrayMerge(request, 2, 5);
		CommonUtils.addSuffix(request, 8);
		ByteBuffer outBuffer = ByteBuffer.wrap(request);
		try {
			channel.write(outBuffer);
		} catch (IOException e) {
			return RESCODE.SEND_FAILED.getJSONRES();
		}
        
		//return responseToBrowser("8", deviceSn);
		 return RESCODE.SUCCESS.getJSONRES();	
	}

	// 服务器设置设备使用哪路传感器，这个指令在前台尚未有调用的地方，先放在这
	public static Map<String, Object> serverSetpathCMD(String deviceSn) {
		SocketChannel channel = clientMap.get(deviceSn);
		if (channel == null) {
			return RESCODE.NOT_OPEN.getJSONRES();
		}
		if (!channel.isConnected()) {
			return RESCODE.CONNECTION_CLOSED.getJSONRES();
		}
		byte[] request = null;
		String temp = StringUtils.add(deviceSn, 1, 12).append("          ").toString();
		request = CommonUtils.toByteArray(temp);
		request[8] = CommonUtils.arrayMerge(request, 2, 6);
		CommonUtils.addSuffix(request, 9);
		ByteBuffer outBuffer = ByteBuffer.wrap(request);
		try {
			channel.write(outBuffer);
		} catch (IOException e) {
			return RESCODE.SEND_FAILED.getJSONRES();
		}

		//return responseToBrowser("12", deviceSn);
		 return RESCODE.SUCCESS.getJSONRES();
	}

	// 服务器校准命令
	public static Map<String, Object> serverCheckCMD(String deviceSn, int way) {
		logger.debug("服务器校准设备编号和路分别为:" + deviceSn + "第" + way + "路");
		SocketChannel channel = clientMap.get(deviceSn);
		if (channel == null) {
			return RESCODE.NOT_OPEN.getJSONRES();
		}
		if (!channel.isConnected()) {
			return RESCODE.CONNECTION_CLOSED.getJSONRES();
		}
		byte[] request = null;

		String path = StringUtils.add(deviceSn, way, 13).append("          ").toString();
		request = CommonUtils.toByteArray(path);
		request[7] = CommonUtils.arrayMerge(request, 2, 5);
		CommonUtils.addSuffix(request, 8);
		ByteBuffer outBuffer = ByteBuffer.wrap(request);
		try {
			channel.write(outBuffer);
		} catch (IOException e) {
			return RESCODE.SEND_FAILED.getJSONRES();
		}

		//return responseToBrowser("13", deviceSn);
		 return RESCODE.SUCCESS.getJSONRES();
	}

	// 五分钟上传一次溶氧，水温和PH值信息
	public static void timingDataCMD(byte[] data, SocketChannel readChannel, String deviceSn, byte way)
			throws IOException {

		byte[] byteOxygen = new byte[4];
		CommonUtils.arrayHandle(data, byteOxygen, 7, 0, 4);
		float oxygen = CommonUtils.byte2float(byteOxygen, 0);
		byte[] byteWaterTemp = new byte[4];
		CommonUtils.arrayHandle(data, byteWaterTemp, 11, 0, 4);
		float waterTemp = CommonUtils.byte2float(byteWaterTemp, 0);
		byte[] bytePhValue = new byte[4];
		CommonUtils.arrayHandle(data, bytePhValue, 15, 0, 4);
		float phValue = CommonUtils.byte2float(bytePhValue, 0);
		byte[] byteSaturation = new byte[4];
		CommonUtils.arrayHandle(data, byteSaturation, 19, 0, 4);
		float saturation = CommonUtils.byte2float(byteSaturation, 0);
		Sensor_Data sData = new Sensor_Data();
		sData.setDevice_sn(deviceSn);
		sData.setWay(way);
		sData.setOxygen(oxygen);
		sData.setWater_temperature(waterTemp);
		sData.setpH_value(phValue);
		sData.setSaturation(saturation);
		logger.debug(
				"服务器接收设备编号和路分别为:" + deviceSn + "第" + way + "路，溶氧值为:" + oxygen + "水温为:" + waterTemp + "ph值为:" + phValue+"溶氧饱和值为:"+saturation);
		sData.setReceiveTime(new Date());
		
		
		
		AIO aio=service.findAIOByDeviceSn(deviceSn);
		 String relation=null;
		 String openId=null;
		 Integer pondId=null;
		if(null!=aio) {
				pondId=(Integer)aio.getPondId();
				relation= aio.getRelation();
			}
		 if(relation!=null&&relation.contains("WX")) {
			 WXUser wxuser=service.findWXUserByRelation(relation);
			 if(null!=wxuser) {
				openId= wxuser.getOpenId();
				
			 }
		 } 
		 DataAlarm da=new DataAlarm();
			da.setCreateDate(new Date());
			da.setDeviceSn(deviceSn);
			da.setRelation(relation);
			da.setWay(way);
			if(aio!=null) {
			da.setDeviceName(aio.getName());
			}else {
				da.setDeviceName(null);
			}
			Pond pond=null;
			if(pondId!=null) {
			pond=service.findPondById(pondId);
			}
			if(pond!=null) {
			da.setPondName(pond.getName());
			}else {
				da.setPondName(null);
			}
		doJudge(deviceSn, waterTemp, oxygen,phValue,openId,da);
		
		service.save(sData);
		AIO aio3=service.findAIOByDeviceSn(deviceSn);
		if(aio3!=null) {
			if(aio3.getStatus()==1) {
			aio3.setStatus(0);
			service.updateAIO(aio3);
			}
		}
		response(24, data, readChannel);
	}

	public static void response(int dataStart, byte[] data, SocketChannel readChannel) throws IOException {
		logger.debug("处理完将反馈指令发送给终端设备");
		byte[] response = new byte[12];
		CommonUtils.arrayHandle(data, response, 0, 0, 7);
		response[7] = CommonUtils.arrayMerge(response, 2, 5);
		CommonUtils.arrayHandle(data, response, dataStart, 8, 4);
		ByteBuffer outBuffer = ByteBuffer.wrap(response);
		logger.debug(CommonUtils.printHexStringMerge(outBuffer.array(),0,outBuffer.array().length));
		readChannel.write(outBuffer);// 将消息回送给客户端
		// System.out.println("cmd代码处理完");
	}

	public static void statusHandle(byte[] status, List<Broken> brokenlist, String deviceSn) {
		logger.debug("设备编号为:" + deviceSn + "的设备开机自检中，现在在进行故障分析");
		String statusStr = CommonUtils.printHexStringMerge(status, 0, 2);
		 System.out.println("分析故障信息");
		String relation = null;
		String type = deviceSn.substring(0, 2);
		System.out.println(type);
		if (type.equals("01") || type.equals("02")) {
			AIO aio = new AIO();
			aio = service.findAIOByDeviceSn(deviceSn);
			if (aio != null) {
				relation = aio.getRelation();
				System.out.println(relation);
			} else {
				return;
			}
		} else if (type.equals("03")) {
			Sensor sensor = service.findSensorByDeviceSn(deviceSn);
			if (sensor != null) {
				relation = sensor.getRelation();
			} else {
				return;
			}
		} else if (type.equals("04")) {
			Controller controller = new Controller();
			controller = service.findControllerByDeviceSn(deviceSn);
			if (controller != null) {
				relation = controller.getRelation();
			} else {
				return;
			}
		}
		//logger.debug("relation为:"+relation);
		System.out.println(statusStr);
		/*
		 * switch (statusStr.substring(0,1)) { case "0": //水泵关闭故障
		 * selfTestBrokenHandle(relation, EntityModel.ENTITY_PUMP,
		 * EntityType.PUMP_OFF,"水泵关闭故障",brokenlist,deviceSn);
		 * System.out.println("````水泵关闭故障"); break; case "1": //水泵打开故障
		 * selfTestBrokenHandle(relation, EntityModel.ENTITY_PUMP,
		 * EntityType.PUMP_ON,"水泵打开故障",brokenlist,deviceSn); break; case "2":
		 * //水泵低电流故障 selfTestBrokenHandle(relation, EntityModel.ENTITY_PUMP,
		 * EntityType.PUMP_LOWCURRENT,"水泵低电流故障",brokenlist,deviceSn); break;
		 * case "3": //水泵高电流故障 selfTestBrokenHandle(relation,
		 * EntityModel.ENTITY_PUMP,
		 * EntityType.HIGH_LIMIT_BROKEN,"水泵高电流故障",brokenlist,deviceSn); break;
		 * default: break; }
		 */

		switch (statusStr.substring(1, 2)) {
		/*
		 * case "0": //PH故障 selfTestBrokenHandle(relation,
		 * EntityModel.ENTITY_PH,
		 * EntityType.NOT_BROKEN,"PH正常",brokenlist,deviceSn);
		 * System.out.println("````ph故障"); break;
		 */
		case "1":
			// PH低限故障
			selfTestBrokenHandle(relation, EntityModel.ENTITY_PH, EntityType.LOW_LIMIT_BROKEN, "PH低限故障", brokenlist,
					deviceSn);
			logger.debug("设备编号为:" + deviceSn + "的设备开机自检中发现PH低限故障");
			break;
		case "2":
			// PH高限故障
			selfTestBrokenHandle(relation, EntityModel.ENTITY_PH, EntityType.HIGH_LIMIT_BROKEN, "PH高限故障", brokenlist,
					deviceSn);
			logger.debug("设备编号为:" + deviceSn + "的设备开机自检中发现PH高限故障");
			break;
		default:
			break;
		}

		switch (statusStr.substring(2, 3)) {
		/*
		 * case "0": //溶氧值故障 selfTestBrokenHandle(relation,
		 * EntityModel.ENTITY_OXYGEN,
		 * EntityType.NOT_BROKEN,"溶氧值正常",brokenlist,deviceSn);
		 * System.out.println("溶氧值故障"); break;
		 */
		case "1":
			// 溶氧值低限故障
			selfTestBrokenHandle(relation, EntityModel.ENTITY_OXYGEN, EntityType.LOW_LIMIT_BROKEN, "溶氧值低限故障",
					brokenlist, deviceSn);
			logger.debug("设备编号为:" + deviceSn + "的设备开机自检中发现溶氧值低限故障");
			break;
		case "2":
			// 溶氧值高限故障
			selfTestBrokenHandle(relation, EntityModel.ENTITY_OXYGEN, EntityType.HIGH_LIMIT_BROKEN, "溶氧值高限故障",
					brokenlist, deviceSn);
			logger.debug("设备编号为:" + deviceSn + "的设备开机自检中发现PH高限故障");
			break;
		default:
			break;
		}

		switch (statusStr.substring(3, 4)) {
		/*
		 * case "0": //温度故障 selfTestBrokenHandle(relation,
		 * EntityModel.ENTITY_TEMPERATURE,
		 * EntityType.NOT_BROKEN,"温度正常",brokenlist,deviceSn); break;
		 */
		case "1":
			// 温度低限故障
			selfTestBrokenHandle(relation, EntityModel.ENTITY_TEMPERATURE, EntityType.LOW_LIMIT_BROKEN, "温度低限故障",
					brokenlist, deviceSn);
			logger.debug("设备编号为:" + deviceSn + "的设备开机自检中发现温度低限故障");
			// System.out.println("温度低限故障");
			break;
		case "2":
			// 温度高限故障
			selfTestBrokenHandle(relation, EntityModel.ENTITY_TEMPERATURE, EntityType.HIGH_LIMIT_BROKEN, "温度高限故障",
					brokenlist, deviceSn);
			logger.debug("设备编号为:" + deviceSn + "的设备开机自检中发现温度高限故障");
			break;
		case "4":
			// 温度断开故障
			selfTestBrokenHandle(relation, EntityModel.ENTITY_TEMPERATURE, EntityType.TEMPORETURE_CLOSED_BROKEN,
					"温度断开故障", brokenlist, deviceSn);
			logger.debug("设备编号为:" + deviceSn + "的设备开机自检中发现温度断开故障");
			break;
		default:
			break;
		}

		
		WXUser wxuser = service.findWXUserByRelation(relation);
		//logger.debug(wxuser.getName());
		if (wxuser != null) {
			
			if (bs.getMSG()!= null) {
				logger.debug("准备将故障信息推送给微信用户");
				WechatSendMessageUtils.sendWechatMessages(bs.getMSG(), wxuser.getOpenId(),deviceSn);
				// WechatTemplateMessage.sendBrokenMSG(bs.getMSG(),wxuser.getOpenId());//把所有故障信息拼接完毕推送给前台
			}
			bs.clear();
		}
		
	}

	public static void selfTestBrokenHandle(String relation, int entityModel, int entityType, String brokenmsg,
			List<Broken> brokenlist, String deviceSn) {
		if (relation.contains("WX")) {
			// 是微信用户就推送给前台
			//BrokenMSG bs = new BrokenMSG();
			
			bs.setMSG(brokenmsg);
		}
		logger.debug("准备将故障信息保存到数据库");
		Broken broken = new Broken();
		broken.setCreateDate(new Date());
		broken.setEntityModel(entityModel);
		broken.setEntityType(entityType);
		broken.setDeviceSn(deviceSn);
		brokenlist.add(broken);
		service.save(broken);
	}
	
	
	public static void doJudge(String deviceSn,float waterTemp,float oxygen,float ph,String openId,DataAlarm da) {
		List<PondFish> fishCategorys=service.queryFishCategorysByDeviceSn(deviceSn);
		 logger.debug("准备根据上传的数据判断水温和溶氧值是否正常");
		if(fishCategorys==null)
			return;
		 Set<Integer> typeset=new HashSet<Integer>();
		if(!fishCategorys.isEmpty()) {
		for(PondFish category:fishCategorys) {
			typeset.add(category.getType());
		}
		}
		for(Integer typetemp:typeset) {
		if(typeset.contains(typetemp)) {
			JudgeAlarmRangeUtils.judgeDO(typetemp, oxygen,openId,deviceSn,da);
			JudgeAlarmRangeUtils.judgeWaterTem(typetemp, waterTemp,openId,deviceSn,da);
			if(-1!=ph)//ph不等于-1说明支持ph功能，然后判断，否则不判断
			JudgeAlarmRangeUtils.judgePH(typetemp, ph,openId,deviceSn,da);
		}
		}
	}

/*	// while循环等待反馈将feedback状态变为true，检测到了就立即返回给浏览器，否则继续，或者等待时间超过10秒，返回失败
	public static Map<String, Object> responseToBrowser(String order, String deviceSn) {

		String lockObject = order + deviceSn;
		logger.debug("生成锁对象" + lockObject);

		Map<String, String> map = getFeedback();
		map.put(deviceSn, lockObject);
		long start = System.currentTimeMillis();
		long end = 0;
		synchronized (lockObject) {

			try {
				logger.debug("当前线程" + Thread.currentThread().getName() + "准备进入等待");
				lockObject.wait(20000);
				logger.debug("当前线程" + Thread.currentThread().getName() + "被唤醒或者超时");
				map.remove(deviceSn);
				end = System.currentTimeMillis();
				if (end - start >= 20000) {
					logger.debug("等待反馈超时");
					return RESCODE.NOT_RECEIVED.getJSONRES();
				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return RESCODE.SUCCESS.getJSONRES();

	}*/
}
