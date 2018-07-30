package com.geariot.platform.fishery.wxutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.fishery.dao.WXUserDao;
import com.geariot.platform.fishery.dao.WXUser_unionDao;
import com.geariot.platform.fishery.entities.WXUser;
import com.geariot.platform.fishery.entities.WXUser_union;
import com.geariot.platform.fishery.service.UserService;
import com.geariot.platform.fishery.service.WebServiceService;

import net.sf.json.JsonConfig;
@Service
@Transactional
public class WeChatOpenIdExchange {
	
	/*private static Logger logger = LogManager.getLogger(WebServiceService.class);
	@Autowired
	private static WXUser_unionDao wxUser_unionDao;
	@Autowired
	private static UserService userService;
	
	public static String getPublicOpenId(String openId) {
		String unionId ="";
		System.out.println(openId);
		if(userService.findUsersByOpenId(openId) != null) {
			List<WXUser> wxuser =userService.findUsersByOpenId(openId);
			unionId = wxuser.get(0).getUnionid();
		}
		
		
		//openId相同，其unionId必然相同
		
		if(unionId !=null) {//在小程序用户表中unionId不为空
			WXUser_union wxuser_union = wxUser_unionDao.getByUnionId(unionId);
			if(wxuser_union == null) {//公众号中unionId为空
				//未查询到unionId，进入
				logger.debug("未获取unionId，开始更新公众号库");				
				Map<String, Object>	publicOpenIds =  getAllPublicUser();	
				List<String> openIdList = (List<String>) publicOpenIds.get("openid");
				for(String publicOpenId:openIdList) {
					String uId =  getPublicUserUnionId(publicOpenId);
					WXUser_union wxu = new WXUser_union();
					wxu.setOpenId(publicOpenId);
					wxu.setUnionId(uId);
					logger.debug("公众号库中已有数据不更新");
					if(wxUser_unionDao.getByUnionId(uId) == null) {
						wxUser_unionDao.save(wxu);
					}					
				}
				WXUser_union wu = wxUser_unionDao.getByUnionId(unionId);
				return wu==null?"":wu.getOpenId();
			} else {
				return wxuser_union.getOpenId();
			}	
		}		
		return null;
	}
	
	public static Map<String, Object> getAllPublicUser() {
		logger.debug("开始获取全部公众号用户 ");
		Map<String, Object> returnmap = new HashMap<>();
		JSONObject obj = WechatConfig.getAccessTokenForInteface();
		String access_token = (String) obj.get("access_token");
		JSONObject AllUserOpenId = WechatConfig.returnAllUserOpenId(access_token, null);
		System.out.println("获得全部用户数据"+AllUserOpenId);
		int total = (int) AllUserOpenId.get("total");
		int count = (int) AllUserOpenId.get("count");
		JSONObject data =  (JSONObject) AllUserOpenId.get("data");
		JSONArray openIds = data.getJSONArray("openid") ;
		List<String> openIdList = new ArrayList<>();
		for(int i=0;i<openIds.length();i++) {
			openIdList.add(openIds.getString(i));
		}
		System.out.println(AllUserOpenId.get("data"));
		System.out.println(data.get("openid"));
		System.out.println(AllUserOpenId.get("count"));

		returnmap.put("total", total);
		returnmap.put("count", count);
		returnmap.put("openid", openIdList);
		return returnmap;
	}
	
	public static String getPublicUserUnionId(String openId) {
		logger.debug("根据公众号openid："+openId+"获取用户unionid");
		JSONObject obj = WechatConfig.getAccessTokenForInteface();
		String access_token = (String) obj.get("access_token");
		JSONObject info = WechatConfig.getWXUserInfo(access_token, openId);
		String unionid = (String) info.get("unionid");		
		return unionid;
	}
	*/

	

}
