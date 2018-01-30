package com.geariot.platform.fishery.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
	private  Map<String,String> beatMap=new ConcurrentHashMap<String,String>();
	private  SimpleDateFormat sdf=new SimpleDateFormat("mm");
	
	public void handle(byte[] data,SocketChannel readChannel) {
		
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
			//每个设备心跳包发送的时间间隔为2分08秒，这里判断距离上一次时间是否大于5分钟，大于则说明离线
			if(sdf.format(new Date()).compareTo(beatMap.get(deviceSn))>5) {
				String judge=deviceSn.substring(0, 2);
				SocketSerivce service =(SocketSerivce) ApplicationUtil.getBean("socketSerivce");
				if(judge.equals("01")||judge.equals("02")) {
					AIO aio=service.findAIOByDeviceSn(deviceSn);
					aio.setStatus(1);
					service.updateAIO(aio);
					beatMap.remove(deviceSn);
				}else if(judge.equals("03")) {
					Sensor sensor=service.findSensorByDeviceSn(deviceSn);
					sensor.setStatus(1);
					service.updateSensor(sensor);
					beatMap.remove(deviceSn);
				}else if(judge.equals("04")) {
					Controller controller =service.findControllerByDeviceSn(deviceSn);
					controller.setStatus(1);
					service.updateController(controller);
					beatMap.remove(deviceSn);
				}
				CMDUtils.getclientMap().remove(deviceSn);//如果离线了就把clientmap里面存的socketchannel移除	
			}
			byte[] resdata=Arrays.copyOf(data, 16);
			ByteBuffer outBuffer = ByteBuffer.wrap(resdata);
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
			
			try {
				 
				switch (order) {
				case 0:
					CMDUtils.selfTestCMD(data,readChannel,deviceSn,way);
					
					break;
				case 1:
					CMDUtils.uploadLimitCMD(data,readChannel,deviceSn,way);
					break;
				case 2:
					
					String lockObject2=CMDUtils.getFeedback().get(deviceSn);
					synchronized (lockObject2) {
						//map.put(deviceSn,String.valueOf(order));
						lockObject2.notify();
					}
					break;
				case 3:
					CMDUtils.timingUploadCMD(data,readChannel,deviceSn,way);
					break;
				case 4:
					CMDUtils.oxygenAlarmCMD(data,readChannel,deviceSn,way);
					break;
				case 5:
					CMDUtils.voltageAlarmCMD(data,readChannel,deviceSn,way);
					break;
				case 6:
					CMDUtils.oxygenTimeCMD(data,readChannel,deviceSn,way);
					break;
				case 7:
					
					String lockObject7=CMDUtils.getFeedback().get(deviceSn);
					synchronized (lockObject7) {
						//map.put(deviceSn,String.valueOf(order));
						lockObject7.notify();
					}
					
					break;
				case 8:
					
					String lockObject8=CMDUtils.getFeedback().get(deviceSn);
					synchronized (lockObject8) {
						//map.put(deviceSn,String.valueOf(order));
						lockObject8.notify();
					}
					break;
				case 9:
					CMDUtils.oxygenExceptionAlarmCMD(data,readChannel,deviceSn,way);
					break;
				case 10: // 0A
					CMDUtils.cancelAllAlarmCMD(data,readChannel,deviceSn,way);
					break;
				case 11: // 0B
					CMDUtils.timingDataCMD(data,readChannel,deviceSn,way);
				case 12: // 0C
					
					String lockObject12=CMDUtils.getFeedback().get(deviceSn);
					synchronized (lockObject12) {
						//map.put(deviceSn,String.valueOf(order));
						lockObject12.notify();
					}
					break;
				case 13:// 0D
					//服务器校准因为有2路传感器，先发送第一路校准，再发送第二路校准，在收到第二路校准的时候反馈给前端
					if(way==2) {
						
						String lockObject13=CMDUtils.getFeedback().get(deviceSn);
						synchronized (lockObject13) {
							//map.put(deviceSn,String.valueOf(order));
							lockObject13.notify();
						}
					}
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
