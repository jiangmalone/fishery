package com.geariot.platform.fishery.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.Controller;
import com.geariot.platform.fishery.entities.Sensor;
import com.geariot.platform.fishery.service.SocketSerivce;
import com.geariot.platform.fishery.utils.ApplicationUtil;
import com.geariot.platform.fishery.utils.CommonUtils;

public class DataHandle {
	private static Map<String,String> beatMap=new ConcurrentHashMap<String,String>();
	private static SimpleDateFormat sdf=new SimpleDateFormat("mm");
	
	public static void handle(SelectionKey key) {
		Map<String, Object> attachmentObject = (Map<String, Object>) key.attachment();
		byte[] data = (byte[]) attachmentObject.get("data");
		
		SocketChannel readChannel = (SocketChannel) attachmentObject.get("readChannel");	
		String prefix = CommonUtils.printHexStringMerge(data, 0, 2);
		//心跳包socket，如果有3次没收到终端发来的心跳包则认为设备已经离线
		//[62 65 61 74 5F 49 44 3D 78 78 78 78 78 78 0D 0A ]
		if(prefix.equals("6265")) {
			Map<String, SocketChannel> map=CMDUtils.getclientMap();
			
			String deviceSn=null;
			if(!map.isEmpty()) {
				for(Entry<String, SocketChannel> es:map.entrySet()) {
					if(es.getValue().equals(readChannel))
						deviceSn= es.getKey();
				}
			}
			beatMap.put(deviceSn,sdf.format(new Date()));//在beatMap里面保存每次收到心跳包的时间
			//每个设备心跳包发送的时间间隔为2分08秒，这里判断距离上一次时间是否大于9，约等于3次，大于则说明离线
			if(sdf.format(new Date()).compareTo(beatMap.get(deviceSn))>9) {
				String judge=deviceSn.substring(0, 2);
				SocketSerivce service =(SocketSerivce) ApplicationUtil.getBean("socketSerivce");
				if(judge.equals("01")||judge.equals("02")) {
					AIO aio=new AIO();
					aio.setStatus(1);
					service.updateAIO(aio);
				}else if(judge.equals("03")) {
					Sensor sensor=new Sensor();
					sensor.setStatus(1);
					service.updateSensor(sensor);
				}else if(judge.equals("04")) {
					Controller controller =new Controller();
					controller.setStatus(1);
					service.updateController(controller);
				}
					
			}
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
			String deviceSn = CommonUtils.printHexStringMerge(byteID,0,3);
			byte way = data[5];
			byte order = data[6];
			attachmentObject.put("deviceSn", deviceSn);
			attachmentObject.put("way", way);
			try {
				
				switch (order) {
				case 0:
					CMDUtils.selfTestCMD(key);
					
					break;
				case 1:
					CMDUtils.uploadLimitCMD(key);
					break;
				case 2:
					CMDUtils.setFeedback(new AtomicBoolean(true));
					break;
				case 3:
					CMDUtils.timingUploadCMD(key);
					break;
				case 4:
					CMDUtils.oxygenAlarmCMD(key);
					break;
				case 5:
					CMDUtils.voltageAlarmCMD(key);
					break;
				case 6:
					CMDUtils.oxygenTimeCMD(key);
					break;
				case 7:
					CMDUtils.setFeedback(new AtomicBoolean(true));
					break;
				case 8:
					CMDUtils.setFeedback(new AtomicBoolean(true));
					break;
				case 9:
					CMDUtils.oxygenExceptionAlarmCMD(key);
					break;
				case 10: // 0A
					CMDUtils.cancelAllAlarmCMD(key);
					break;
				case 11: // 0B
					CMDUtils.timingDataCMD(key);
				case 12: // 0C
					CMDUtils.setFeedback(new AtomicBoolean(true));
					break;
				case 13:// 0D
					CMDUtils.setFeedback(new AtomicBoolean(true));
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
