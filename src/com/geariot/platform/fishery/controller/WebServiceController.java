package com.geariot.platform.fishery.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.geariot.platform.fishery.utils.HttpRequest;
import com.geariot.platform.fishery.utils.MD5;
import com.geariot.platform.fishery.wxutils.WechatConfig;
import com.geariot.platform.fishery.wxutils.WechatLoginUse;
import com.geariot.platform.fishery.entities.WXUser;
import com.geariot.platform.fishery.model.RESCODE;
import com.geariot.platform.fishery.service.WebServiceService;
import com.geariot.platform.fishery.utils.Constants;

@RestController
@RequestMapping("/webService")
public class WebServiceController {

	@Autowired
	private WebServiceService webServiceService;

	private String mapApiKey = "1fb41392b17b0575e03b5374cb7b029f";

	private String weatherUrl = "http://restapi.amap.com/v3/weather/weatherInfo";

	private String weatherExtensions = "all";

	private String ContentType = "application/json";

	private String leancouldUrlRes = "https://leancloud.cn/1.1/requestSmsCode";

	private String leancouldUrlVer = "https://leancloud.cn/1.1/verifySmsCode";

	private String appid = "XPWUWPaoNKH8Liud35sER7Dc-gzGzoHsz";

	private String appkey = "hqSCLSLjTuCYphRJ5q9kiqJo";

	private static String BASEURL = "http://www.fishery.com/freelycar_wechat/index.html#/";

	private Logger logger = LogManager.getLogger(WebServiceController.class);

	@RequestMapping(value = "/weather", method = RequestMethod.GET)
	public String getWeatherTest(String city) {
		Map<String, Object> param = new HashMap<>();
		param.put("city", city);
		param.put("extensions", weatherExtensions);
		param.put("key", mapApiKey);
		Map<String, Object> head = setWeatherHead();
		String result = HttpRequest.getCall(weatherUrl, param, head);
		return result;

	}

	@RequestMapping(value = "/verification", method = RequestMethod.GET)
	public String getVerification(String phone) {
		JSONObject param = new JSONObject();
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
		return json.toString();
	}

	// OAuth2.0授权后的 重定向
	@RequestMapping(value = "/wechatlogin")
	public String wechatLogin(String htmlPage, String code) {
		return getWechatInfo(htmlPage, code);
	}

	// 微信jsapi
	@RequestMapping(value = "/wx/getJSSDKConfig", method = RequestMethod.GET)
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

	public String getWechatInfo(String htmlPage, String code) {
		String wechatInfo = WechatLoginUse.wechatInfo(code);
		JSONObject resultJson;
		try {
			resultJson = new JSONObject(wechatInfo);
			if (resultJson.get("message").equals("success")) {
				String openId = resultJson.getString("openid");
				String headimgurl = resultJson.getString("headimgurl");
				headimgurl = URLEncoder.encode(headimgurl, "utf-8");
				String ret = BASEURL + htmlPage;
				boolean wxUser = webServiceService.isExistUserOpenId(openId);
				if (!wxUser) {
					ret = BASEURL + "login?openid=" + openId + "&headimgurl=" + headimgurl+ "&directUrl=" + htmlPage;
					} 
				return "redirect:" + ret;
				}
			}catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
		return "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxfd188f8284ee297b&redirect_uri=http%3a%2f%2fwww.freelycar.com%2ffreelycar_wechat%2fapi%2fuser%2fwechatlogin%3FhtmlPage%3DpersonalInfo&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
		}

	// 注册验证结果请求
	@RequestMapping(value = "/verifySmsCode", method = RequestMethod.GET)
	public Map<String, Object> verifySmsCode(String phone, String smscode, String openId, String headimgurl) {
		JSONObject json = this.verifySmscode(phone, smscode);
		if (json.getString("error") != null) {
			logger.debug(phone + ";code:" + smscode + " 验证失败。。。");
			Map<String, Object> obj = new HashMap<>();
			obj.put(Constants.RESPONSE_CODE_KEY, json.getInt("code"));
			obj.put(Constants.RESPONSE_MSG_KEY, json.getInt("error"));
			return obj;
		} else {
			return webServiceService.login(phone,openId,headimgurl);
		}
	}

	@RequestMapping(value = "/checkLogin", method = RequestMethod.GET)
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
	public Map<String, Object> logout(String phone) {
		return webServiceService.deletWXUser(phone);
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
}
