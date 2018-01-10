package com.geariot.platform.fishery.socket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;

import org.apache.catalina.tribes.util.Arrays;

import com.geariot.platform.fishery.utils.CommonUtils;

public class DataHandle {

	/*
	 * private static Map<String, SocketChannel> clientMap = new HashMap<String,
	 * SocketChannel>();// id SocketChannel private static List<MsgStruct> msg_list
	 * = new ArrayList<MsgStruct>(); //private static Lock lock = new
	 * ReentrantLock();
	 * 
	 * 
	 * public static Map<String, SocketChannel> getclientMap() { return clientMap; }
	 */

	/*
	 * public static Lock getLock() { return lock; }
	 */
	// 1
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
			String deviceSn = String.valueOf(CommonUtils.bytesToInt(byteID));
			byte way = data[5];
			byte order = data[6];
			attachmentObject.put("deviceSn", deviceSn);
			attachmentObject.put("way", way);
			try {
				System.out.println(order);
				switch (order) {
				case 0:
					System.out.println("jinlaile");
					CMDUtils.selfTestCMD(key);
					break;
				case 1:
					CMDUtils.uploadLimitCMD(key);
					break;
				// case为2的时候终端发个确认的指令过来，读到这个数据不知道怎么处理，先放在这
				case 2:
					byte check2 = data[7];
					String suffix2 = CommonUtils.printHexStringMerge(data, 8, 4);
					// System.out.println("server set limit success !!!");
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
					/*
					 * byte power7 = data[7]; byte check7 = data[8]; String suffix7 =
					 * CommonUtils.printHexStringMerge(data,9,4); Dao_AeratorStatus
					 * dao_AeratorStatus1 = new Dao_AeratorStatus(); row_count =
					 * dao_AeratorStatus1.insertOne(String.valueOf(id), way,power7, "2"); Date now1
					 * = new Date(); for(int i=0;i<msg_list.size();i++) {
					 * if((now1.getTime()-msg_list.get(i).getStart_time())/1000>20) {
					 * msg_list.get(i).getChannel().close(); msg_list.remove(i); } }
					 * Iterator<MsgStruct> it1 = msg_list.iterator(); while(it1.hasNext()) {
					 * MsgStruct one = it1.next(); if((now1.getTime()-one.getStart_time())/1000>20)
					 * { one.getChannel().close(); it1.remove(); } else { byte[] device_sn_byte =
					 * CommonUtils.getByteArray(id);
					 * if(one.getId()==id&one.getWay()==way&one.getOrder()==2) { response = new
					 * byte[9]; response[0]=(byte)0x5B; response[1]=(byte)0xB5;
					 * response[2]=(byte)0x01; response[3]=device_sn_byte[0];
					 * response[4]=device_sn_byte[1]; response[5]=device_sn_byte[2];
					 * response[6]=(byte)way; if(row_count==1)//0 success 1 offline 2 fail {
					 * response[7]= (byte)0x00; } else { response[7]= (byte)0x02; } response[8] =
					 * power7; ByteBuffer outBuffer = ByteBuffer.wrap(response);
					 * one.getChannel().write(outBuffer); one.getChannel().close(); it1.remove();
					 * break; } } } System.out.println("server set status success !!!");
					 */
					break;
				case 8:
					/*
					 * byte check8 = data[7];
					 * 
					 * String suffix8 = CommonUtils.printHexStringMerge(data,8,4);
					 * 
					 * // Dao_Alarm dao_Alarm = new Dao_Alarm(); // row_count =
					 * dao_AeratorStatus1.insertOne(String.valueOf(id), way,power7, "2"); Date now2
					 * = new Date(); for(int i=0;i<msg_list.size();i++) {
					 * if((now2.getTime()-msg_list.get(i).getStart_time())/1000>20) {
					 * msg_list.get(i).getChannel().close(); msg_list.remove(i); } }
					 * Iterator<MsgStruct> it2 = msg_list.iterator(); while(it2.hasNext()) {
					 * MsgStruct one = it2.next(); if((now2.getTime()-one.getStart_time())/1000>20)
					 * { one.getChannel().close(); it2.remove(); } else { byte[] device_sn_byte =
					 * CommonUtils.getByteArray(id);
					 * if(one.getId()==id&one.getWay()==way&one.getOrder()==4) { response = new
					 * byte[8]; response[0]=(byte)0x5B; response[1]=(byte)0xB5;
					 * response[2]=(byte)0x01; response[3]=device_sn_byte[0];
					 * response[4]=device_sn_byte[1]; response[5]=device_sn_byte[2];
					 * response[6]=(byte)way; //0 success 1 offline 2 fail response[7]= (byte)0x00;
					 * ByteBuffer outBuffer = ByteBuffer.wrap(response);
					 * one.getChannel().write(outBuffer); one.getChannel().close(); it2.remove();
					 * break; } } } System.out.println("server set auto success !!!");
					 */
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
					/*
					 * byte sensor_status = data[7];
					 * 
					 * byte check12 = data[8];
					 * 
					 * String suffix12 = CommonUtils.printHexStringMerge(data,9,4);
					 * 
					 * row_count = 1; Date now12 = new Date(); for(int i=0;i<msg_list.size();i++) {
					 * if((now12.getTime()-msg_list.get(i).getStart_time())/1000>20) {
					 * msg_list.get(i).getChannel().close(); msg_list.remove(i); } }
					 * Iterator<MsgStruct> it12 = msg_list.iterator(); while(it12.hasNext()) {
					 * MsgStruct one = it12.next();
					 * if((now12.getTime()-one.getStart_time())/1000>20) { one.getChannel().close();
					 * it12.remove(); } else { byte[] device_sn_byte = CommonUtils.getByteArray(id);
					 * if(one.getId()==id&one.getWay()==way&one.getOrder()==5) { response = new
					 * byte[8]; response[0]=(byte)0x5B; response[1]=(byte)0xB5;
					 * response[2]=(byte)0x05; response[3]=device_sn_byte[0];
					 * response[4]=device_sn_byte[1]; response[5]=device_sn_byte[2];
					 * response[6]=(byte)way; if(row_count==1)//0 success 1 offline 2 fail {
					 * response[7]= (byte)0x00; } else { response[7]= (byte)0x02; } ByteBuffer
					 * outBuffer = ByteBuffer.wrap(response); one.getChannel().write(outBuffer);
					 * one.getChannel().close(); it12.remove(); break; } } }
					 * System.out.println("server set sensor success !!!");
					 */
					break;
				case 13:// 0D
					/*
					 * byte check13 = data[7];
					 * 
					 * String suffix13 = CommonUtils.printHexStringMerge(data,8,4);
					 * 
					 * 
					 * Date now3 = new Date(); for(int i=0;i<msg_list.size();i++) {
					 * if((now3.getTime()-msg_list.get(i).getStart_time())/1000>20) {
					 * msg_list.get(i).getChannel().close(); msg_list.remove(i); } }
					 * Iterator<MsgStruct> it3 = msg_list.iterator(); while(it3.hasNext()) {
					 * MsgStruct one = it3.next(); if((now3.getTime()-one.getStart_time())/1000>20)
					 * { one.getChannel().close(); it3.remove(); } else { byte[] device_sn_byte =
					 * CommonUtils.getByteArray(id);
					 * if(one.getId()==id&one.getWay()==way&one.getOrder()==3) { response = new
					 * byte[8]; response[0]=(byte)0x5B; response[1]=(byte)0xB5;
					 * response[2]=(byte)0x03; response[3]=device_sn_byte[0];
					 * response[4]=device_sn_byte[1]; response[5]=device_sn_byte[2];
					 * response[6]=(byte)way; //0 success 1 offline 2 fail response[7]= (byte)0x00;
					 * ByteBuffer outBuffer = ByteBuffer.wrap(response);
					 * one.getChannel().write(outBuffer); one.getChannel().close(); it3.remove();
					 * break; } } } System.out.println("server set correct success !!!");
					 */
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
					// clientMap.remove(String.valueOf(id));
				}

			}

		} else
			System.out.println("hahaha");
	}
}
