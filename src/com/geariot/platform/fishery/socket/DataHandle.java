package com.geariot.platform.fishery.socket;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.catalina.tribes.util.Arrays;

import com.geariot.platform.fishery.utils.CommonUtils;

public class DataHandle {
	public void handle(SelectionKey key) {
		Map<String, Object> attachmentObject = (Map<String, Object>) key.attachment();
		byte[] data = (byte[]) attachmentObject.get("data");
		System.out.println(Arrays.toString(data));
		SocketChannel readChannel = (SocketChannel) attachmentObject.get("readChannel");
		
		String prefix = CommonUtils.printHexStringMerge(data, 0, 2);
		
		
		if (prefix.equals("5AA5")) {
			System.out.println("进来了");
			byte[] byteID = new byte[3];

			CommonUtils.arrayHandle(data, byteID, 2, 0, 3);
			String deviceSn = CommonUtils.printHexStringMerge(byteID,0,3);
			byte way = data[5];
			byte order = data[6];
			attachmentObject.put("deviceSn", deviceSn);
			attachmentObject.put("way", way);
			try {
				System.out.println(order);
				switch (order) {
				case 0:
					CMDUtils.selfTestCMD(key);
					System.out.println("switch代码处理完");
					break;
				case 1:
					CMDUtils.uploadLimitCMD(key);
					break;
				case 2:
					//CMDUtils.setFeedback(new AtomicBoolean(true));
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
					
					break;
				case 8:
					
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
					
					break;
				case 13:// 0D
					
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
