/**
 *
 */
package com.geariot.platform.fishery.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.geariot.platform.fishery.dao.WXUserDao;
import com.geariot.platform.fishery.entities.WXUser;
import com.geariot.platform.fishery.model.RESCODE;
import com.geariot.platform.fishery.utils.HttpRequest;

import net.sf.json.JSONArray;

/**
 * @author mxy940127
 *
 */
@Service
@Transactional
public class WebServiceService {

	private Logger logger = LogManager.getLogger(WebServiceService.class);

	@Autowired
	private WXUserDao wxUserDao;

	/*public Map<String, Object> bindUser(String phone, String openId){
		logger.debug("绑定用户的openId：" + openId);
		if(openId == null){
			return RESCODE.WECHAT_GET_PARAM_WRONG.getJSONRES();
		}
		List<WXUser> userOpenId = wxUserDao.findUsersByOpenId(openId);
		WXUser userPhone = wxUserDao.findUserByPhone(phone);
		//本地数据库没有openId的记录
		if(userOpenId == null){
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		//本地根据openId找到一条数据，且这条数据的phone字段不为空。
		else if(userOpenId.getPhone() != null && !userOpenId.getPhone().isEmpty()){
			if(!userOpenId.getPhone().equals(phone)){
				return RESCODE.BINDED_WITH_OTHER_PHONE.getJSONRES();
			}
		}
		if(userPhone == null){
			return RESCODE.NOT_FOUND.getJSONRES();
		}else if(!openId.equalsIgnoreCase(userPhone.getOpenId())){
			if(userPhone.getOpenId() != null && !userPhone.getOpenId().isEmpty()){
				return RESCODE.PHONE_BINDED_BY_OTHER.getJSONRES();
			}else{
				userPhone.setOpenId(userOpenId.getOpenId());
				userPhone.setName(userOpenId.getName());
				wxUserDao.deleteUser(userOpenId.getId());
				return RESCODE.SUCCESS.getJSONRES(userPhone);
			}
		}
		return RESCODE.SUCCESS.getJSONRES(userOpenId);
	}
*/
	public boolean isExistUserOpenId(String openId) {
		if(openId == null || openId.length() < 0){
			return false;
		}else{
			List<WXUser> wxUser = wxUserDao.findUsersByOpenId(openId);
			return wxUser != null;
		}
	}

	public Map<String, Object> login(String phone, String openId, String headimgurl,String wxUserName) {
		WXUser wxUser = wxUserDao.findUserByPhone(phone);
		logger.debug(openId);
		if (wxUser == null) {
			WXUser wxUserNew = new WXUser();
			wxUserNew.setPhone(phone);
			wxUserNew.setHeadimgurl(headimgurl);
			wxUserNew.setOpenId(openId);
			wxUserNew.setLogin(true);
			wxUserNew.setCreateDate(new Date());
			wxUserNew.setName(wxUserName);
			wxUserDao.save(wxUserNew);
			wxUserNew.setRelation("WX"+wxUserNew.getId());
			return RESCODE.SUCCESS.getJSONRES(wxUserNew);
		} else {
			wxUser.setHeadimgurl(headimgurl);
			wxUser.setPhone(phone);
			wxUser.setOpenId(openId);
			wxUser.setLogin(true);
			wxUser.setName(wxUserName);
			return RESCODE.SUCCESS.getJSONRES(wxUser);
		}
	}

	public Map<String, Object> checkLogin(String phone) {
		WXUser wxUser = wxUserDao.findUserByPhone(phone);
		if(wxUser == null){
			return RESCODE.NOT_FOUND.getJSONRES();
		}else{
			if(wxUser.isLogin()){
				return RESCODE.SUCCESS.getJSONRES();
			}else{
				return RESCODE.NO_LOGIN.getJSONRES();
			}
		}
	}

	public Map<String, Object> deletWXUser(String phone) {
		wxUserDao.logout(phone);
		return RESCODE.SUCCESS.getJSONRES();
	}
	
	public String getLocation(Float lon,Float lat) {
		
		String mapApiKey = "1fb41392b17b0575e03b5374cb7b029f";
		String adcodeUrl = "http://restapi.amap.com/v3/geocode/regeo";
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
	private Map<String, Object> setWeatherHead() {
		Map<String, Object> head = new HashMap<String, Object>();
		head.put("Content-Type", "application/json");
		return head;
	}
}
