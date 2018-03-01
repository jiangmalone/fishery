package com.geariot.platform.fishery.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.Controller;
import com.geariot.platform.fishery.entities.Sensor;
import com.geariot.platform.fishery.service.SocketSerivce;
import com.geariot.platform.fishery.utils.ApplicationUtil;
import com.geariot.platform.fishery.utils.CommonUtils;
import com.geariot.platform.fishery.wxutils.WechatSendMessageUtils;

public class DataHandle {
	private Map<String, String> beatMap = new ConcurrentHashMap<String, String>();
	private SimpleDateFormat sdf = new SimpleDateFormat("mm");
	private static Logger logger = Logger.getLogger(DataHandle.class);
	private static SocketSerivce service = (SocketSerivce) ApplicationUtil.getBean("socketSerivce");
	public void handle(byte[] data, SocketChannel readChannel) {
		if (data[6] != 6) {
			logger.debug(CommonUtils.printHexStringMerge(data, 0, data.length) + "进入handle处理");
		}
		String prefix = CommonUtils.printHexStringMerge(data, 0, 2);
		// 心跳包socket，如果有3次没收到终端发来的心跳包则认为设备已经离线
		// [62 65 61 74 5F 49 44 3D 78 78 78 78 78 78 0D 0A ]
		if (prefix.equals("6265")) {
			Map<String, SocketChannel> map = CMDUtils.getclientMap();
			String deviceSn = null;
			if (!map.isEmpty()) {
				for (Entry<String, SocketChannel> es : map.entrySet()) {
					if (es.getValue().equals(readChannel))
						deviceSn = es.getKey();
				}
			}
			logger.debug("设备编号为:" + deviceSn + "的心跳包");
			beatMap.put(deviceSn, sdf.format(new Date()));// 在beatMap里面保存每次收到心跳包的时间
			// 每个设备心跳包发送的时间间隔为2分08秒，这里判断距离上一次时间是否大于5分钟，大于则说明离线
			if (sdf.format(new Date()).compareTo(beatMap.get(deviceSn)) > 6) {
				String judge = deviceSn.substring(0, 2);
				
				if (judge.equals("01") || judge.equals("02")) {
					AIO aio = service.findAIOByDeviceSn(deviceSn);
					aio.setStatus(1);
					service.updateAIO(aio);
					beatMap.remove(deviceSn);
				} else if (judge.equals("03")) {
					Sensor sensor = service.findSensorByDeviceSn(deviceSn);
					sensor.setStatus(1);
					service.updateSensor(sensor);
					beatMap.remove(deviceSn);
				} else if (judge.equals("04")) {
					Controller controller = service.findControllerByDeviceSn(deviceSn);
					controller.setStatus(1);
					service.updateController(controller);
					beatMap.remove(deviceSn);
				}
				CMDUtils.getclientMap().remove(deviceSn);
				logger.debug("离线清除该设备在clientmap里面的信息,并关闭readchannel,设备号为:" + deviceSn);
				try {
					readChannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.debug("长期心跳包没收到关闭其readChannel,设备号为:" + deviceSn);
				}
				// 如果离线了就把clientmap里面存的socketchannel移除,并关闭readChannel
			}
			// byte[] resdata=Arrays.copyOf(data, 16);
			ByteBuffer outBuffer = ByteBuffer.wrap(data);
			try {
				readChannel.write(outBuffer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (prefix.equals("5AA5")) {
			byte[] byteID = new byte[3];
			CommonUtils.arrayHandle(data, byteID, 2, 0, 3);
			String deviceSn = CommonUtils.printHexStringMerge(byteID, 0, 3);
			byte way = data[5];
			byte order = data[6];
			try {
				switch (order) {
				case 0:
					logger.debug("读到自检命令,设备编号为:" + deviceSn + "第" + way + "路");
					CMDUtils.selfTestCMD(data, readChannel, deviceSn, way);
					break;
				case 1:
					logger.debug("终端发送给服务器增氧机三限设备编号为:" + deviceSn + "第" + way + "路");
					CMDUtils.uploadLimitCMD(data, readChannel, deviceSn, way);
					break;
				case 2:
					logger.debug("服务器设置三限的反馈命令,设备编号为:" + deviceSn + "第" + way + "路");
					String openId2 = service.findOpenIdByDeviceSn(deviceSn);
					String msg2=CMDUtils.msg.get(deviceSn+"2");
					WechatSendMessageUtils.sendWechatLimitMessages("设置"+msg2+"成功", openId2, deviceSn);
					CMDUtils.msg.remove(deviceSn+"2");
					break;
				case 3:
					logger.debug("5分钟上传一次溶氧值和水温命令,设备编号为:" + deviceSn + "第" + way + "路");
					CMDUtils.timingUploadCMD(data, readChannel, deviceSn, way);
					break;
				case 4:
					logger.debug("缺相报警命令,设备编号为:" + deviceSn + "第" + way + "路");
					CMDUtils.oxygenAlarmCMD(data, readChannel, deviceSn, way);
					break;
				case 5:
					logger.debug("220v断电报警命令,设备编号为:" + deviceSn + "第" + way + "路");
					CMDUtils.voltageAlarmCMD(data, readChannel, deviceSn, way);
					break;
				case 6:
					CMDUtils.oxygenTimeCMD(data, readChannel, deviceSn, way);
					break;
				case 7:
					logger.debug("服务器开关增氧机反馈命令,设备编号为:" + deviceSn + "第" + way + "路");
					String openId7 = service.findOpenIdByDeviceSn(deviceSn);
					String onOff=CMDUtils.msg.get(deviceSn+"7");
					String msg7=null;
					if(onOff.equals("1")) {
					msg7="打开增氧机";}else {
						msg7="关闭增氧机";
					}
					WechatSendMessageUtils.sendWechatOnOffMessages(msg7+"操作成功", openId7, deviceSn);
					break;
				case 8:
					logger.debug("服务器一键自动反馈命令,设备编号为:" + deviceSn + "第" + way + "路");
					String openId8 = service.findOpenIdByDeviceSn(deviceSn);
					WechatSendMessageUtils.sendWechatSetAutoMessages("一键自动成功", openId8, deviceSn);
					break;
				case 9:
					logger.debug("溶氧值数据异常命令,设备编号为:" + deviceSn + "第" + way + "路");
					CMDUtils.oxygenExceptionAlarmCMD(data, readChannel, deviceSn, way);
					break;
				case 10: // 0A
					logger.debug("取消所有报警命令,设备编号为:" + deviceSn + "第" + way + "路");
					CMDUtils.cancelAllAlarmCMD(data, readChannel, deviceSn, way);
					break;
				case 11: // 0B
					logger.debug("5分钟上传一次溶氧值水温ph命令,设备编号为:" + deviceSn + "第" + way + "路");
					CMDUtils.timingDataCMD(data, readChannel, deviceSn, way);
					break;
				case 12: // 0C
					logger.debug("服务器使用哪路传感器命令,设备编号为:" + deviceSn + "第" + way + "路");
					String openId12 = service.findOpenIdByDeviceSn(deviceSn);
					WechatSendMessageUtils.sendWechatSetPathMessages("更改传感器路数成功", openId12, deviceSn);
					break;
				case 13:// 0D
					logger.debug("服务器校准命令,设备编号为:" + deviceSn + "第" + way + "路");//自动校准不用推送
					//String openId13 = service.findOpenIdByDeviceSn(deviceSn);
					//WechatSendMessageUtils.sendWechatCheckMessages("校准成功", openId13, deviceSn);
					break;
				default:
					break;
				}
			} catch (IOException e1) {
				System.out.println("Connection reset by peer");
				Boolean ret = false;
				try {
					readChannel.close();
					ret = true;
					System.out.println("Close channel");
				} catch (IOException e) {
					System.out.println("Close channel Exception");
					ret = false;
				}
				if (ret) {
					CMDUtils.getclientMap().remove(deviceSn);
				}
			}
		}
	}
}
