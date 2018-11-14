package com.geariot.platform.fishery.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse.SmsSendDetailDTO;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.dyvmsapi.model.v20170525.SingleCallByTtsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.geariot.platform.fishery.model.RESCODE;
import com.geariot.platform.fishery.service.UserService;
import com.geariot.platform.fishery.service.WebServiceService;
import com.geariot.platform.fishery.utils.Constants;
import com.geariot.platform.fishery.utils.HttpRequest;
import com.geariot.platform.fishery.utils.MD5;
import com.geariot.platform.fishery.utils.VmsUtils;
import com.geariot.platform.fishery.wxutils.WechatConfig;

import net.sf.json.JSONArray;

@Controller
@RequestMapping("/webService")
public class WebServiceController {

	@Autowired
	private WebServiceService webServiceService;
	
	@Autowired
	private UserService userService;

	private String mapApiKey = "1fb41392b17b0575e03b5374cb7b029f";
	//根据adcode获取天气
	private String weatherUrl = "http://restapi.amap.com/v3/weather/weatherInfo";
	//根据经纬度获取adcode
	private String adcodeUrl = "http://restapi.amap.com/v3/geocode/regeo";
	
	private String key = "9c61f7bef1593d00ddbd8fad03070e25";

	private String weatherExtensions = "all";

	private String ContentType = "application/json";

	private String leancouldUrlRes = "https://leancloud.cn/1.1/requestSmsCode";

	private String leancouldUrlVer = "https://leancloud.cn/1.1/verifySmsCode";

	private String appid = "moO5MJxKXNizrMVJKIikuqyH-gzGzoHsz";

	private String appkey = "QlGVlWcxjennYwAafkOGBBPj";

	private static String BASEURL = "http://www.fisherymanager.net/fishery/index.html#/";
	


	private Logger logger = LogManager.getLogger(WebServiceController.class);

/*	@RequestMapping(value = "/weather", method = RequestMethod.GET)
	@ResponseBody
	public String getWeatherTest(String city) {
		Map<String, Object> param = new HashMap<>();
		param.put("city", city);
		param.put("extensions", weatherExtensions);
		param.put("key", mapApiKey);
		Map<String, Object> head = setWeatherHead();
		String result = HttpRequest.getCall(weatherUrl, param, head);
		return result;

	}*/
	
	@RequestMapping(value = "/weather", method = RequestMethod.GET,produces="text/html;charset=UTF-8")
	@ResponseBody
	public String getWeatherTest(Float lon,Float lat) {
		/*经度在前，纬度在后，经纬度间以“,”分割，经纬度小数点后不要超过 6 位。*/
		lon = (float)(Math.round(lon*1000000))/1000000;
		lat = (float)(Math.round(lat*1000000))/1000000;
		
		String location = lon+","+lat;
		
		/*"118.87474,32.13955"*/
		Map<String, Object> param = new HashMap<>();
	    param.put("key", mapApiKey);
		param.put("location", location);
		Map<String, Object> head = setWeatherHead();
		String result = HttpRequest.getCall(adcodeUrl, param, head);
		JSONArray json = JSONArray.fromObject("["+result+"]"); 
		net.sf.json.JSONObject object = json.getJSONObject(0);
		String str = object.get("regeocode").toString();
		JSONArray jso = JSONArray.fromObject("["+str+"]"); 
		net.sf.json.JSONObject objec = jso.getJSONObject(0);		
		String st = objec.get("addressComponent").toString();
		JSONArray js= JSONArray.fromObject("["+st+"]"); 
		net.sf.json.JSONObject obje = js.getJSONObject(0);

		Map<String, Object> param1 = new HashMap<>();
		param1.put("city", obje.get("adcode").toString());
		param1.put("extensions", "base");
		param1.put("key", mapApiKey);
		Map<String, Object> head1 = setWeatherHead();
		String result1 = HttpRequest.getCall(weatherUrl, param1, head1);
		return result1;

	}
	
	@RequestMapping(value = "/location", method = RequestMethod.GET)
	@ResponseBody
	public String getLocationTest(Float lon,Float lat) {
		/*经度在前，纬度在后，经纬度间以“,”分割，经纬度小数点后不要超过 6 位。*/
		lon = (float)(Math.round(lon*1000000))/1000000;
		lat = (float)(Math.round(lat*1000000))/1000000;
		
		String location = lon+","+lat;
		
		/*"118.87474,32.13955"*/
		Map<String, Object> param = new HashMap<>();
	    param.put("key", mapApiKey);
		param.put("location", location);
		Map<String, Object> head = setWeatherHead();
		String result = HttpRequest.getCall(adcodeUrl, param, head);
		JSONArray json =  JSONArray.fromObject("["+result+"]"); 
		net.sf.json.JSONObject objec = json.getJSONObject(0);		
		String st = objec.get("regeocode").toString();
		
		JSONArray json1 =  JSONArray.fromObject("["+st+"]"); 
		net.sf.json.JSONObject objec1 = json1.getJSONObject(0);		
		String st1 = objec1.get("formatted_address").toString();
		
		return st1;

	}

	@RequestMapping(value = "/verification", method = RequestMethod.GET)
	@ResponseBody
	public String getVerification(String phone) {
		logger.debug("用户首次登陆/退出再登陆，获得手机号"+phone);
		/*JSONObject param = new JSONObject();
		param.put("mobilePhoneNumber", phone);
		HttpEntity entity = HttpRequest.getEntity(param);
		Map<String, Object> head = setLeancloudHead();
		String result = HttpRequest.postCall(leancouldUrlRes, entity, head);
		logger.debug("leancloud的返回码：" + result);
		JSONObject json = null;
		try {
			json = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
			logger.error("解析发送验证码返回结果错误");
		}
		if (json.getString("error") != null) {
			json.put(Constants.RESPONSE_CODE_KEY, RESCODE.SUCCESS);
			json.put(Constants.RESPONSE_MSG_KEY, RESCODE.SUCCESS.getMsg());
		} else {
			json.put(Constants.RESPONSE_CODE_KEY, json.getInt("code"));
			json.put(Constants.RESPONSE_MSG_KEY, json.getString("error"));
		}
		return json.toString();*/
		SendSmsResponse response = webServiceService.sendSms(phone);
		JSONObject json = null;
		
		json = new JSONObject(response);
	
		
		if(response.getCode().equals("OK")){
			json.put(Constants.RESPONSE_CODE_KEY, RESCODE.SUCCESS);
			json.put(Constants.RESPONSE_MSG_KEY, RESCODE.SUCCESS.getMsg());
		}else {
			json.put(Constants.RESPONSE_CODE_KEY, json.getInt("code"));
			json.put(Constants.RESPONSE_MSG_KEY, json.getString(response.getCode()));
		}
		return json.toString();
	}


	@RequestMapping(value = "/getuserinfo", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> wechatLogin(String code) {
		return webServiceService.wechatLogin(code);		
	}
	

	@RequestMapping(value = "/getuserOpenId", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getuserOpenId(String code) {
		logger.debug("code:"+code);
		Map<String, Object> map =  webServiceService.getWechatInfo(code);
		if(map.get("openId")==null) {
			logger.debug("未获得openId");
			return RESCODE.NOT_FOUND.getJSONRES();
		}else {
			logger.debug("openId:"+map.get("openId"));
			return map;
		}
	}

	// 微信jsapi
	@RequestMapping(value = "/wx/getJSSDKConfig", method = RequestMethod.GET)
	@ResponseBody
	public String getJsSDKConfig(String targetUrl) {
		logger.debug("JSSDK Url:" + targetUrl);
		if (targetUrl == null || targetUrl.isEmpty()) {
			throw new RuntimeException("jsapiTicket获取失败,当前url为空！！");
		}
		String noncestr = UUID.randomUUID().toString();
		JSONObject ticketJson = WechatConfig.getJsApiTicketByWX();
		String ticket = ticketJson.getString("ticket");
		String timestamp = String.valueOf(System.currentTimeMillis());
		int index = targetUrl.indexOf("#");
		if (index > 0) {
			targetUrl = targetUrl.substring(0, index);
		}
		// 对给定字符串key手动排序
		String param = "jsapi_ticket=" + ticket + "&noncestr=" + noncestr + "&timestamp=" + timestamp + "&url="
				+ targetUrl;
		String signature = MD5.encode("SHA1", param);
		JSONObject jsSDKConfig = new JSONObject();
		jsSDKConfig.put("appId", WechatConfig.APP_ID);
		jsSDKConfig.put("nonceStr", noncestr);
		jsSDKConfig.put("timestamp", timestamp);
		jsSDKConfig.put("signature", signature);
		return jsSDKConfig.toString();
	}




	// 注册验证结果请求
	@RequestMapping(value = "/verifySmsCode", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> verifySmsCode(String phone, String smscode, String openId, String headimgurl,String wxUserName,String encryptedData,String iv,String session_key,String unionid) {
		logger.debug("++++++++++++++验证手机短信以及获取unionid++++++++++++++++");
		logger.debug("headimgurl： "+headimgurl+".");
		logger.debug("wxUserName： "+wxUserName+".");
		logger.debug("unionid： "+unionid+".");
		logger.debug("encryptedData： "+encryptedData+".");
		logger.debug("iv： "+iv+".");
		logger.debug("session_key： "+session_key+".");
		String newwxUserName = "";
		newwxUserName = filterEmoji(wxUserName);	
		logger.debug("过滤username中的表情符："+newwxUserName+".");
		/*try {
			wxUserName = new String(wxUserName.getBytes("ISO-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		/*try {
			wxUserName = new String(wxUserName.getBytes("utf-8"), "utf8mb4");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		SmsSendDetailDTO smsDetail = webServiceService.getCode(phone);
		if(smsDetail!=null) {
			logger.debug("smsDetail不为空");
			//最新短息消息
			String code = smsDetail.getOutId();
			logger.debug(code);
			String receiveDate = smsDetail.getReceiveDate();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int min =0;
			try {
				Date date = sdf.parse(receiveDate);
				Date now = new Date();
				long cost = now.getTime()-date.getTime();
				min = (int) (cost/1000/60);				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(min<5) {//短信有效时间
				logger.debug("短信在有效期内");
				if(smscode.equals(code)) {
					logger.debug(phone + ";code:" + smscode + " 验证成功。。。");
					if(unionid == null || unionid.equals("")) {
						//使用String encryptedData,String iv,String session_key解码获取unionid
					}			
					return webServiceService.login(phone,openId,headimgurl,newwxUserName,unionid);
				}
				
				
			}
		}
		//JSONObject json = this.verifySmscode(phone, smscode);
		//logger.debug("openId:"+openId);
		//if (json.getString("error") != null) {
			//验证失败
			logger.debug(phone + ";code:" + smscode + " 验证失败。。。");
			Map<String, Object> obj = new HashMap<>();
			obj.put(Constants.RESPONSE_CODE_KEY, "error");
			obj.put(Constants.RESPONSE_MSG_KEY, smsDetail.getErrCode());
			return obj;
		//}
		
	}
	@RequestMapping(value = "/getcode", method = RequestMethod.GET)
	@ResponseBody
	public void getCode(String phone) {
		webServiceService.getCode(phone);		
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> Login(String phone, String openId, String headimgurl,String wxUserName,String unionid){
		return webServiceService.login(phone, openId, headimgurl, wxUserName, unionid);
	}
	
	@RequestMapping(value = "/getAllPublicUser", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getAllPublicUser(){
		return userService.getAllPublicUser();
	}
	
	@RequestMapping(value = "/getPublicUserUnionId", method = RequestMethod.GET)
	@ResponseBody
	public String getPublicUserUnionId(){
		return userService.getPublicUserUnionId("orEjLv5S6uXJ1s8NS2P-PqWBF9jg");
	}
	
	@RequestMapping(value = "/getPublicOpenId", method = RequestMethod.GET)
	@ResponseBody
	public String getPublicOpenId(){
		return userService.getPublicOpenId("owhQb0frO0ALfKcfu81JWJv7k_zI");
	}

	@RequestMapping(value = "/checkLogin", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> checkLogin(String phone){
		return webServiceService.checkLogin(phone);
	}

	
	
	private JSONObject verifySmscode(String phone, String smscode) {
		Map<String, Object> head = setLeancloudHead();
		String result = HttpRequest.postCall(leancouldUrlVer + "/" + smscode + "?mobilePhoneNumber=" + phone, null,
				head);
		logger.debug("绑定手机短信验证, phone:" + phone + ", smscode:" + smscode + "。 短信验证结果：" + result);
		JSONObject json = null;
		try {
			json = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
			logger.error("解析验证验证码返回结果错误");
		}
		logger.debug("解析后结果：" + json.toString());
		return json;
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> logout(String phone) {
		logger.debug("手机号为"+phone+"的用户执行退出登录");
		synchronized (this) {
			return webServiceService.deletWXUser(phone);
		}
		
	}
	
	@RequestMapping(value = "/singleCallByTts", method = RequestMethod.GET)
	@ResponseBody
	public void singleCallByTts(String phone) {
		String json = "{\"deviceName\":\"" + "测试" + "\",\"way\":" + 1 + "}";
		try {
			logger.debug("准备启用阿里云语音服务");
			SingleCallByTtsResponse scbtr = VmsUtils.singleCallByTts(phone, "TTS_126866281", json);
			logger.debug(scbtr.getMessage());			
		} catch (ClientException e) {
			logger.debug("阿里云语音服务异常");
			e.printStackTrace();
		}
		
	}
	@RequestMapping(value = "/sms", method = RequestMethod.GET)
	@ResponseBody
	public SendSmsResponse sms(String phone) {
		return webServiceService.sendSms(phone);		
	}
	
	

	private Map<String, Object> setLeancloudHead() {
		Map<String, Object> head = new HashMap<String, Object>();
		head.put("X-LC-Id", appid);
		head.put("X-LC-Key", appkey);
		head.put("Content-Type", ContentType);
		return head;
	}

	private Map<String, Object> setWeatherHead() {
		Map<String, Object> head = new HashMap<String, Object>();
		head.put("Content-Type", ContentType);
		return head;
	}
	
	public static String filterEmoji(String nick_name) {
	    //nick_name 所获取的用户昵称 
	    if (nick_name == null) {
	        return nick_name;
	    }
	    Pattern emoji = Pattern.compile("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]|[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
	            Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
	    Matcher emojiMatcher = emoji.matcher(nick_name);
	    if (emojiMatcher.find()) {
	        //将所获取的表情转换为*
	        nick_name = emojiMatcher.replaceAll("*");
	        return nick_name;
	    }
	    return nick_name;
	}
}
