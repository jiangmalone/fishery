package com.geariot.platform.fishery.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.ControllerEventListener;

import com.geariot.platform.fishery.entities.*;

import org.apache.logging.log4j.LogManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aliyuncs.exceptions.ClientException;
import com.geariot.platform.fishery.dao.AIODao;
import com.geariot.platform.fishery.dao.AeratorStatusDao;
import com.geariot.platform.fishery.dao.CompanyDao;
import com.geariot.platform.fishery.dao.ControllerDao;
import com.geariot.platform.fishery.dao.Dev_TriggerDao;
import com.geariot.platform.fishery.dao.LimitDao;
import com.geariot.platform.fishery.dao.PondDao;
import com.geariot.platform.fishery.dao.PondFishDao;
import com.geariot.platform.fishery.dao.SensorDao;
import com.geariot.platform.fishery.dao.Sensor_ControllerDao;
import com.geariot.platform.fishery.dao.TimerDao;
import com.geariot.platform.fishery.dao.WXUserDao;
import com.geariot.platform.fishery.dao.WXUser_unionDao;
import com.geariot.platform.fishery.dao.DiagDao;
import com.geariot.platform.fishery.model.Equipment;
import com.geariot.platform.fishery.model.RESCODE;
import com.geariot.platform.fishery.utils.VmsUtils;
import com.geariot.platform.fishery.wxutils.WeChatOpenIdExchange;
import com.geariot.platform.fishery.wxutils.WechatAlarmMessage;
import com.geariot.platform.fishery.wxutils.WechatConfig;
import com.geariot.platform.fishery.wxutils.WechatSendMessageUtils;
import com.geariot.platform.fishery.wxutils.WechatTemplateMessage;
import com.sun.media.jfxmedia.logging.Logger;

import cmcc.iot.onenet.javasdk.api.device.GetDevicesStatus;
import cmcc.iot.onenet.javasdk.api.device.GetLatesDeviceData;
import cmcc.iot.onenet.javasdk.response.BasicResponse;
import cmcc.iot.onenet.javasdk.response.device.DeciceLatestDataPoint;
import cmcc.iot.onenet.javasdk.response.device.DevicesStatusList;


@Service
@Transactional
public class UserService {
	
	private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(UserService.class);
	
    @Autowired
    private CompanyDao companyDao;
    
    @Autowired
    private WXUserDao wxuserDao;
    
    @Autowired
    private WXUser_unionDao wxUser_unionDao;
    
    @Autowired
    private PondDao pondDao;
    
    @Autowired
    private AIODao aioDao;
    
    @Autowired
    private AeratorStatusDao aeratorStatusDao;
    
    @Autowired
    private LimitDao limitDao;
    
    @Autowired
    private SensorDao sensorDao;
    
    @Autowired
    private Dev_TriggerDao triggerDao;
    
    @Autowired
    private TimerDao timerDao;
    
    @Autowired
    private ControllerDao controllerDao;
    
    @Autowired
    private Sensor_ControllerDao sensor_ControllerDao;
    
    @Autowired
    private PondFishDao pondFishDao;

	@Autowired
	private DiagDao diagDao;
    
    @Autowired
    private PondService pondService;
    
    @Autowired
    private EquipmentService equipmentService;
    
    
	public Map<String, Object> addWXUser(WXUser wxuser) {
		/*List<WXUser> exist = wxuserDao.findUsersByOpenId(wxuser.getOpenId());*/
		WXUser exist = wxuserDao.findUserByPhone(wxuser.getPhone());
		if (exist != null) {
			return RESCODE.ACCOUNT_EXIST.getJSONRES();
		}
		wxuser.setCreateDate(new Date());
		wxuser.setLogin(false);
		wxuserDao.save(wxuser);
		wxuser.setRelation("WX"+wxuser.getId());
		return RESCODE.SUCCESS.getJSONRES(wxuser);
	}
	
	public List<WXUser> findWXUser(String openId) {
		return wxuserDao.findUsersByOpenId(openId);
				
	}

	public Map<String, Object> addCompany(Company company) {
		Company exist = companyDao.findCompanyByName(company.getName());
		if (exist != null) {
			return RESCODE.ACCOUNT_EXIST.getJSONRES();
		}
		company.setCreateDate(new Date());
		company.setHasAccount(false);
		companyDao.save(company);
		company.setRelation("CO"+company.getId());
		return RESCODE.SUCCESS.getJSONRES(company);
	}

	public Map<String, Object> deleteCompany(Integer[] companyIds) {
		for (int companyId : companyIds) {
			Company exist = companyDao.findCompanyById(companyId);
			if (exist == null) {
				return RESCODE.DELETE_ERROR.getJSONRES();
			} else {
				List<AIO> aios = aioDao.queryAIOByNameAndRelation(exist.getRelation(), null);
				for(AIO aio : aios){
					aeratorStatusDao.delete(aio.getDevice_sn());
					limitDao.delete(aio.getDevice_sn());
					timerDao.delete(aio.getDevice_sn());
				}
				aioDao.deleteByRelation(exist.getRelation());
				//删除控制器和传感器前需要将之间的绑定关系删掉,再删两者
				List<Sensor> sensors = sensorDao.querySensorByNameAndRelation(exist.getRelation(), null);
				for(Sensor sensor : sensors){
					limitDao.delete(sensor.getDevice_sn());
					timerDao.delete(sensor.getDevice_sn());
					sensor_ControllerDao.delete(sensor.getId());
				}
				List<Controller> controllers = controllerDao.queryControllerByNameAndRelation(exist.getRelation(), null);
				for(Controller Controller : controllers){
					sensor_ControllerDao.deleteController(Controller.getId());
				}
				sensorDao.deleteByRelation(exist.getRelation());
				controllerDao.deleteByRelation(exist.getRelation());
				List<Pond> ponds = pondDao.queryPondByNameAndRelation(exist.getRelation(), null);
				for(Pond pond : ponds){
					pondFishDao.deleteByPondId(pond.getId());
				}
				pondDao.deleteByRelation(exist.getRelation());
				companyDao.deleteCompany(companyId);
			}
		}
		return RESCODE.SUCCESS.getJSONRES();
	}

	public Map<String, Object> deleteWXUser(Integer[] WXUserIds) {
		logger.debug("进入删除用户");
		for (int WXUserId : WXUserIds) {
			WXUser exist = wxuserDao.findUserById(WXUserId);
			if (exist == null) {
				return RESCODE.DELETE_ERROR.getJSONRES();
			} else {			
				String relation = exist.getRelation();			
				//删除传感器
				List<Sensor> sensorList = sensorDao.findSensorsByRelation(relation);
				for(Sensor sensor : sensorList) {
					equipmentService.delEquipment(sensor.getDevice_sn());					
				}				
				//删除一体机				
				List<AIO> aioList = aioDao.findAIOByRelation(relation);
				for(AIO aio:aioList) {
					equipmentService.delEquipment(aio.getDevice_sn());
				}
				//删除控制器
				List<Controller> controllerList = controllerDao.findByRelation(relation);
				for(Controller con:controllerList) {
					equipmentService.delEquipment(con.getDevice_sn());
				}
				//删除塘口				
				pondDao.deleteByRelation(relation);
				wxuserDao.deleteUser(WXUserId);
			}
		}
		return RESCODE.SUCCESS.getJSONRES();
	}

	public Map<String, Object> modifyCompany(Company company) {
		Company exist = companyDao.findCompanyById(company.getId());
		if (exist == null) {
			return RESCODE.NOT_FOUND.getJSONRES();
		} 
		exist.setName(company.getName());
		exist.setPhone(company.getPhone());
		exist.setAddress(company.getAddress());
		exist.setLife(company.getLife());
		exist.setMail_address(company.getMail_address());
		companyDao.updateCompany(exist);
		
		return RESCODE.SUCCESS.getJSONRES(exist);
	}

	public Map<String, Object> modifyWXUser(WXUser wxuser) {
		WXUser exist = wxuserDao.findUserById(wxuser.getId());
		if (exist == null) {
			return RESCODE.NOT_FOUND.getJSONRES();
		} 
		
		exist.setName(wxuser.getName());
		exist.setPhone(wxuser.getPhone());
		exist.setAddress(wxuser.getAddress());
		exist.setLife(wxuser.getLife());
		exist.setSex(wxuser.getSex());
		exist.setHeadimgurl(wxuser.getHeadimgurl());
		wxuserDao.updateUser(exist);
		
		return RESCODE.SUCCESS.getJSONRES(exist);
	}

	public Map<String, Object> queryCompany(String name, int page, int number) {
		int from = (page - 1) * number;
		List<Company> list = companyDao.queryList(name, from, number);
		if (list == null || list.isEmpty()) {
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		long count = (long) companyDao.getQueryCount(name);
		int size = (int) Math.ceil(count / (double) number);
		return RESCODE.SUCCESS.getJSONRES(list,size,count);
	}

	public Map<String, Object> queryWXUser(String name, int page, int number) {
		int from = (page - 1) * number;
		List<WXUser> list = wxuserDao.queryList(name, from, number);
		if (list == null || list.isEmpty()) {
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		long count = (long) wxuserDao.getQueryCount(name);
		int size = (int) Math.ceil(count / (double) number);
		return RESCODE.SUCCESS.getJSONRES(list,size,count);
	}

	public Map<String, Object> WXUserDetail(int id) {
		WXUser wxUser = wxuserDao.findUserById(id);
		if (wxUser == null) {
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		return RESCODE.SUCCESS.getJSONRES(wxUser);
	}
	
	public Map<String, Object> CompanyDetail(int id) {
		Company company = companyDao.findCompanyById(id);
		if (company == null) {
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		int count = 0;
		List<String> relations = new ArrayList<>();
		relations.add(company.getRelation());
		Map<String, Object> map = RESCODE.SUCCESS.getJSONRES(company);
		List<Pond> ponds = pondDao.queryPondByNameAndRelation(company.getRelation(), null);
		List<Equipment> equipments = pondDao.adminFindEquipmentByName(relations, 0, 2000);
		for(Equipment equipment :equipments){
			if(equipment.getStatus().contains("0")){
				count++;
			}
		}
		map.put("pondCount", ponds.size());
		map.put("equip", count+"/"+equipments.size());
		return map;
	}

	public Map<String, Object> relationDetail(String relation) {
		String type = relation.substring(0, 2);
		switch(type){
			case "WX" : return WXUserDetail(Integer.parseInt(relation.substring(2)));
			case "CO" :	return CompanyDetail(Integer.parseInt(relation.substring(2)));
			default : return RESCODE.WRONG_PARAM.getJSONRES();
		}
	}
	public Map<String, Object> HomePageDetail(String relation) {
		logger.debug("根据微信用户："+relation+"，开始获取首页数据");
		WXUser wxUser = wxuserDao.findUserByRelation(relation);
		String key = "7zMmzMWnY1jlegImd=m4p9EgZiI=";
		Map<String, Object> map = new HashMap<>();
		List<Object> ol = new ArrayList<>();
 		WXUser wxu = wxuserDao.findUserByRelation(relation);
		List<Pond> pondList = pondDao.queryPondByRelation(relation);

		for(Pond pond:pondList) {
			logger.debug("用户的池塘："+pond.getName());			
			Map<String, Object> map11=new HashMap<>();
			map11.put("pondname", pond.getName());
			map11.put("address", pond.getAddress());
			map11.put("lat", pond.getLatitude());
			map11.put("lon", pond.getLongitude());
			List<Sensor> sensorlist = sensorDao.findSensorsByPondId(pond.getId());
			List<Map> sensorLM = new ArrayList<>();
			List<AIO> aioList = aioDao.findAIOsByPondId(pond.getId());
			List<Map> aioLM = new ArrayList<>();
			List<Controller> controllerList = controllerDao.findByPondId(pond.getId());
			List<Map> controllerLM = new ArrayList<>();
			//获得传感器离线/在线
			if(sensorlist!=null) {
				logger.debug("池塘："+pond.getName()+"，关联的传感器数量："+sensorlist.size());
				for(Sensor sensor:sensorlist) {
					Map<String, Object> sensorMap=new HashMap<>();
					Map<String, Object> sensorDataMap=new HashMap<>();
					//获得传感器在线/离线状态
					GetDevicesStatus api = new GetDevicesStatus(sensor.getDevice_sn(),key);
			        BasicResponse<DevicesStatusList> response = api.executeApi();
			        logger.debug("从onenet上获取传感器在线/离线状态");
			        if(response.errno == 0) {
			        	sensorMap.put("online", response.data.getDevices().get(0).getIsonline());
			        }else {
			        	logger.debug("未查询到传感器状态，显示离线");
			        	sensorMap.put("online", false);
			        }
			        //获得传感器最新数据
			        GetLatesDeviceData lddapi = new GetLatesDeviceData(sensor.getDevice_sn(), key);
			        BasicResponse<DeciceLatestDataPoint> response2 = lddapi.executeApi();
			        logger.debug("获得传感器最新数据:"+response2.getJson());
			        System.out.println(response2.getJson());
			        if(response2.errno == 0) {
			        	List<cmcc.iot.onenet.javasdk.response.device.DeciceLatestDataPoint.DeviceItem.DatastreamsItem> DatastreamsList = response2.data.getDevices().get(0).getDatastreams();
				        for(int i=0;i<DatastreamsList.size();i++) {				        	
				        	sensorDataMap.put(DatastreamsList.get(i).getId(), DatastreamsList.get(i).getValue());
				        }
			        	sensorMap.put("data", sensorDataMap);
			        }else {
			        	sensorMap.put("data", false);
			        }
			        sensorMap.put("sensor", sensor);
			        sensorLM.add(sensorMap);
				}
			}
			//获得一体机离线/在线，功能不完善，有待继续。。。。
			if(aioList!=null) {
				System.out.println("aioList"+aioList.size());
				for(AIO aio:aioList) {
					Map<String, Object> aioMap=new HashMap<>();
					GetDevicesStatus api = new GetDevicesStatus(aio.getDevice_sn(),key);
			        BasicResponse<DevicesStatusList> response = api.executeApi();
			        if(response.errno == 0) {
			        	aioMap.put("online", response.data.getDevices().get(0).getIsonline());
			        }
			        aioMap.put("aio", aio);
			        aioLM.add(aioMap);
				}
			}
			//获得控制器离线/在线
			if(controllerList!=null) {
				System.out.println("controllerList"+controllerList.size());
				for(Controller controller:controllerList) {
					Map<String, Object> controllerMap=new HashMap<>();
					Map<String, Object> controllerDataMap=new HashMap<>();
					//获得设备离线/在线状态
					GetDevicesStatus api = new GetDevicesStatus(controller.getDevice_sn(),key);
			        BasicResponse<DevicesStatusList> response = api.executeApi();
			        logger.debug("获取首页控制器状态："+response.getJson());
	
			        if(response.errno == 0) {
			        	controllerMap.put("online", response.data.getDevices().get(0).getIsonline());
			        	//获得控制器最新数据
				        GetLatesDeviceData lddapi = new GetLatesDeviceData(controller.getDevice_sn(), key);
				        BasicResponse<DeciceLatestDataPoint> response2 = lddapi.executeApi();
				        logger.debug("获取首页控制器数据："+response2.getJson());
				        if(response2.errno == 0) {
				        	List<cmcc.iot.onenet.javasdk.response.device.DeciceLatestDataPoint.DeviceItem.DatastreamsItem> datastreamsList = response2.data.getDevices().get(0).getDatastreams();
				        	if(datastreamsList!=null) {
				        		for(int i=0;i<datastreamsList.size();i++) {
						        	controllerDataMap.put(datastreamsList.get(i).getId(), datastreamsList.get(i).getValue());
						        }        	
						        controllerMap.put("data", controllerDataMap);	
						        String  PF =  (String) controllerDataMap.get("PF");						       
						        if(controllerDataMap.get("PF") !=null) {	
							        logger.debug("断电1或正常0："+PF);
							        if(PF.equals("1")) {
							        	controller.setStatus(2);							        								        								        		
							        }else {
							        	if(controllerDataMap.get("DP"+(controller.getPort()+1))!=null) {
							        		 String DP = (String) controllerDataMap.get("DP"+(controller.getPort()+1));
								        	if(DP.equals("1")) {
								        		logger.debug("缺相1或正常0："+DP);
								        		controller.setStatus(3);								        		
								        	}else {
								        		controller.setStatus(0);
								        	}
							        	}
							        }
						        }
				        	}else {
				        		controllerMap.put("data", false);
				        	}					        
				        }else {
				        	controllerMap.put("data", false);
				        }
			        }			      
			        List<Timer> timerList = timerDao.findTimerByDeviceSnAndWay(controller.getDevice_sn(), controller.getPort());
			        Limit_Install limit= limitDao.findLimitByDeviceSnsAndWay(controller.getDevice_sn(), controller.getPort());
			        String controllerKey = equipmentService.getControllerPortStatus(controller.getDevice_sn(), controller.getPort());
			        controllerMap.put("switch", controllerKey);
			        controllerMap.put("TimerList", timerList);
			        controllerMap.put("Limit", limit);
			        controllerMap.put("controller", controller);
			        controllerLM.add(controllerMap);
	
				}
			}
			
			
			
			map11.put("sensorlist", sensorLM);
			map11.put("aioList", aioLM);
			map11.put("controllerList", controllerLM);	
			ol.add(map11);	

		}
		map.put("myHome", ol);
	
		return map;
	}
	
	public int hasEquipment(String relation) {
		logger.debug("进入判断用户："+relation+"是否有设备");
		List<AIO> aioList = aioDao.findAIOByRelation(relation);
		List<Sensor> sensorList = sensorDao.findSensorsByRelation(relation);
		List<Controller> controllerList = controllerDao.findByRelation(relation);
		if(aioList.size()==0&&sensorList.size()==0&&controllerList.size()==0) {
			return 0;
		}else {
			return 1;
		}
	}

	public Map<String, Object> diagnosing(String relation) {
		logger.debug("进入用户"+relation+"的诊断");
		Map<String, Object> map = new HashMap<>();
		List<Pond> pondList = pondDao.queryPondByRelation(relation);
		List<Object> allbrokenpond = new ArrayList<>();
		for(Pond pond:pondList) {
			//System.out.println("池塘名:"+pond.getName());
			Map<String, Object> onemap=new HashMap<>();
			String pondname = pond.getName();
			List<Object> onepondbroken = new ArrayList<>();
			List<Sensor> sensorlist = sensorDao.findSensorsByPondId(pond.getId());
			if(sensorlist!=null) {
				//System.out.println("sensorlist"+sensorlist.size());
				for(Sensor sensor:sensorlist) {
					int brokentype= sensor.getStatus();
					if (brokentype!=0){
						List<Diagnosing> diagnos=diagDao.getDiagnosingByType(brokentype);
						if(diagnos!=null){
							Map<String, Object> onesensormap=new HashMap<>();
							String brokenname=diagnos.get(0).getBroken_name();
							List<String> solutions=new ArrayList<>(); ;
							for(Diagnosing dia:diagnos){
								solutions.add(dia.getSolution());
							}
							onesensormap.put("brokenname",brokenname);
							onesensormap.put("solutions",solutions);
							onepondbroken.add(onesensormap);
						}
					}
				}
			}
			onemap.put("pondname",pondname);
			onemap.put("brokenlist",onepondbroken);
			allbrokenpond.add(onemap);
		}
		map.put("brokenpondlist",allbrokenpond);
		return map;
	}
	
	public String getPublicOpenId(String openId) {
		logger.debug("getPublicOpenId+进入获取公众号openId");
		String unionId ="";		
		/*
		 * 在wxuser表中，根据小程序openid获得wxuser，查看wxuser是否有unionId
		 */
		if(wxuserDao.findUsersByOpenId(openId) != null && wxuserDao.findUsersByOpenId(openId).size()>0) {
			List<WXUser> wxuserList =wxuserDao.findUsersByOpenId(openId);
			for(WXUser wxuser:wxuserList) {
				if(wxuser.getUnionid() != null&&wxuser.getUnionid() !="") {
					unionId = wxuser.getUnionid();
					break;
				}				
			}			
		}		
		//openId相同，其unionId必然相同		
		if(unionId !=null && unionId != "") {//在小程序用户表中unionId不为空
			WXUser_union wxuser_union = wxUser_unionDao.getByUnionId(unionId);
			if(wxuser_union == null) {//公众号中unionId为空
				//未查询到unionId，进入
				logger.debug("getPublicOpenId+根据unionId未获取到公众号openid，更新公众号库");				
				Map<String, Object>	publicOpenIds =  getAllPublicUser();	
				List<String> openIdList = (List<String>) publicOpenIds.get("openid");				
				for(String publicOpenId:openIdList) {										
					if(wxUser_unionDao.getByUnionId(unionId) == null) {						
						if(wxUser_unionDao.getByOpenId(publicOpenId) == null) {
							logger.debug("公众号库中数据不存在，数据更新");
							WXUser_union wxu = new WXUser_union();
							String uid = getPublicUserUnionId(publicOpenId);
							wxu.setOpenId(publicOpenId);
							wxu.setUnionId(uid);
							wxUser_unionDao.save(wxu);
						}						
					}else {
						logger.debug("公众号库中已有数据不更新");
					}					
				}
				
				WXUser_union wu = wxUser_unionDao.getByUnionId(unionId);
				logger.debug("getPublicOpenId+更新数据库后获取用户公众号openId："+wu==null?"":wu.getOpenId());
				return wu==null?"":wu.getOpenId();
			} else {
				logger.debug("直接返回公众号openId:"+wxuser_union.getOpenId());
				return wxuser_union.getOpenId();
			}	
		}else {
			logger.debug("getPublicOpenId+wxuser表中无unionId");
			return null;
		}		
		
	}
	
	public static Map<String, Object> getAllPublicUser() {
		logger.debug("开始获取全部公众号用户 ");
		Map<String, Object> returnmap = new HashMap<>();
		JSONObject obj = WechatConfig.getAccessTokenForInteface();
		String access_token = (String) obj.get("access_token");
		JSONObject AllUserOpenId = WechatConfig.returnAllUserOpenId(access_token, null);
		logger.debug("获得全部用户数据"+AllUserOpenId);
		int total = (int) AllUserOpenId.get("total");
		int count = (int) AllUserOpenId.get("count");
		logger.debug("total"+total);
		logger.debug("count"+count);
		JSONObject data =  (JSONObject) AllUserOpenId.get("data");
		JSONArray openIds = data.getJSONArray("openid") ;
		List<String> openIdList = new ArrayList<>();
		for(int i=0;i<openIds.length();i++) {
			openIdList.add(openIds.getString(i));
		}

		returnmap.put("total", total);
		returnmap.put("count", count);
		returnmap.put("openid", openIdList);
		return returnmap;
	}
	
	public static String getPublicUserUnionId(String openId) {
		logger.debug("根据公众号openid："+openId+"，获取用户unionid。");
		JSONObject obj = WechatConfig.getAccessTokenForInteface();
		String access_token = (String) obj.get("access_token");
		logger.debug("access_token:"+access_token);
		JSONObject info = WechatConfig.getWXUserInfo(access_token, openId);
		logger.debug("info:"+info);
		String unionid = (String) info.get("unionid");
		logger.debug("用户unionid:"+unionid);
		return unionid;
	}

}
