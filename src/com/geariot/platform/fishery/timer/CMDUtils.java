package com.geariot.platform.fishery.timer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cmcc.iot.onenet.javasdk.api.cmds.QueryCmdsRespApi;
import cmcc.iot.onenet.javasdk.api.cmds.QueryCmdsStatus;
import cmcc.iot.onenet.javasdk.api.cmds.SendCmdsApi;
import cmcc.iot.onenet.javasdk.response.BasicResponse;
import cmcc.iot.onenet.javasdk.response.cmds.CmdsResponse;
import cmcc.iot.onenet.javasdk.response.cmds.NewCmdsResponse;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

public class CMDUtils {
	private static Logger logger = Logger.getLogger(CMDUtils.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static String apikey="7zMmzMWnY1jlegImd=m4p9EgZiI=";
	public static int sendStrCmd(String devId,String contents){
		logger.debug("开始向编号为"+devId+"的设备发送命令");
		/**
		 * 发送命令
		 * @param devId：接收该数据的设备ID（必选）,String
		 * @param qos:是否需要响应，默认为0,Integer
		 * 0：不需要响应，即最多发送一次，不关心设备是否响应；
		 * 1：需要响应，如果设备收到命令后没有响应，则会在下一次设备登陆时若命令在有效期内(有效期定义参见timeout参数）则会继续发送。
		 * 对响应时间无限制，多次响应以最后一次为准。
		 * 本参数仅当type=0时有效；
		 * @param timeOut:命令有效时间，默认0,Integer
		 * 0：在线命令，若设备在线,下发给设备，若设备离线，直接丢弃；
		 *  >0： 离线命令，若设备在线，下发给设备，若设备离线，在当前时间加timeout时间内为有效期，有效期内，若设备上线，则下发给设备。单位：秒，有效围：0~2678400。
		 *  本参数仅当type=0时有效；
		 * @param type://默认0。0：发送CMD_REQ包，1：发送PUSH_DATA包
		 * @param contents:用户自定义数据：json、string、二进制数据（小于64K）
		 * @param key:masterkey或者设备apikey
		 */
		
		SendCmdsApi api = new SendCmdsApi(devId, null, null, null,contents, apikey);
		String cmdUuid="";
		try{
			
			
			BasicResponse<NewCmdsResponse> response = api.executeApi();
			System.out.println(response.getJson());
			JSONObject tempjson = JSONObject.fromObject(response.getJson());

			JSONObject temp12 = tempjson.getJSONObject("data");
			String temp13 = temp12.getString("cmd_uuid");
			cmdUuid=temp13;
			Thread.currentThread().sleep(3500);
			/**
			 * 查询命令响应
			 * @param cmduuid:命令id,String
			 * @param key:masterkey或者设备apikey
			 */
			/*QueryCmdsRespApi api1=new QueryCmdsRespApi(cmdUuid,apikey);*/
			QueryCmdsStatus api1=new QueryCmdsStatus(cmdUuid,apikey);
			BasicResponse<CmdsResponse> response1 = api1.executeApi();
			if(response.errno==0) {
				if(response1.data.getStatus()==4) {
					return 0;
				}else {
					return 1;
				}
				
			}else {
				return 1;
			}	
			
		}catch(Exception e){
			System.out.println(e.getMessage());
			return 1;
		}
		

	}

}
