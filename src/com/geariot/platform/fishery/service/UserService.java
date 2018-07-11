package com.geariot.platform.fishery.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.geariot.platform.fishery.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.fishery.dao.AIODao;
import com.geariot.platform.fishery.dao.AeratorStatusDao;
import com.geariot.platform.fishery.dao.CompanyDao;
import com.geariot.platform.fishery.dao.ControllerDao;
import com.geariot.platform.fishery.dao.LimitDao;
import com.geariot.platform.fishery.dao.PondDao;
import com.geariot.platform.fishery.dao.PondFishDao;
import com.geariot.platform.fishery.dao.SensorDao;
import com.geariot.platform.fishery.dao.Sensor_ControllerDao;
import com.geariot.platform.fishery.dao.TimerDao;
import com.geariot.platform.fishery.dao.WXUserDao;
import com.geariot.platform.fishery.dao.DiagDao;
import com.geariot.platform.fishery.model.Equipment;
import com.geariot.platform.fishery.model.RESCODE;
import com.sun.media.jfxmedia.logging.Logger;

import cmcc.iot.onenet.javasdk.api.device.GetDevicesStatus;
import cmcc.iot.onenet.javasdk.api.device.GetLatesDeviceData;
import cmcc.iot.onenet.javasdk.response.BasicResponse;
import cmcc.iot.onenet.javasdk.response.device.DeciceLatestDataPoint;
import cmcc.iot.onenet.javasdk.response.device.DevicesStatusList;


@Service
@Transactional
public class UserService {
    @Autowired
    private CompanyDao companyDao;
    
    @Autowired
    private WXUserDao wxuserDao;
    
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
		WXUser exist = wxuserDao.findUserByOpenId(wxuser.getOpenId());
		if (exist != null) {
			return RESCODE.ACCOUNT_EXIST.getJSONRES();
		}
		wxuser.setCreateDate(new Date());
		wxuserDao.save(wxuser);
		wxuser.setRelation("WX"+wxuser.getId());
		return RESCODE.SUCCESS.getJSONRES(wxuser);
	}
	
	public WXUser findWXUser(String openId) {
		return wxuserDao.findUserByOpenId(openId);
				
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
		for (int WXUserId : WXUserIds) {
			WXUser exist = wxuserDao.findUserById(WXUserId);
			if (exist == null) {
				return RESCODE.DELETE_ERROR.getJSONRES();
			} else {
				//删名下一体机前需要将AeratorStatus记录删掉
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
		String key = "KMDJ=U3QacwRmoCdcVXrTW8D0V8=";
		Map<String, Object> map = new HashMap<>();
		List<Object> ol = new ArrayList<>();
 		WXUser wxu = wxuserDao.findUserByRelation(relation);
		List<Pond> pondList = pondDao.queryPondByRelation(relation);

		for(Pond pond:pondList) {
			System.out.println("池塘名:"+pond.getName());
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
				System.out.println("sensorlist"+sensorlist.size());
				for(Sensor sensor:sensorlist) {
					Map<String, Object> sensorMap=new HashMap<>();
					Map<String, Object> sensorDataMap=new HashMap<>();
					//获得传感器在线/离线状态
					GetDevicesStatus api = new GetDevicesStatus(sensor.getDevice_sn(),key);
			        BasicResponse<DevicesStatusList> response = api.executeApi();
			        if(response.errno == 0) {
			        	sensorMap.put("online", response.data.getDevices().get(0).getIsonline());
			        }else {
			        	sensorMap.put("online", false);
			        }
			        //获得传感器最新数据
			        GetLatesDeviceData lddapi = new GetLatesDeviceData(sensor.getDevice_sn(), key);
			        BasicResponse<DeciceLatestDataPoint> response2 = lddapi.executeApi();
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
			        if(response.errno == 0) {
			        	controllerMap.put("online", response.data.getDevices().get(0).getIsonline());			        	
			        }
			      //获得控制器最新数据
			        GetLatesDeviceData lddapi = new GetLatesDeviceData(controller.getDevice_sn(), key);
			        BasicResponse<DeciceLatestDataPoint> response2 = lddapi.executeApi();
			        if(response2.errno == 0) {
			        	List<cmcc.iot.onenet.javasdk.response.device.DeciceLatestDataPoint.DeviceItem.DatastreamsItem> DatastreamsList = response2.data.getDevices().get(0).getDatastreams();
				        for(int i=0;i<DatastreamsList.size();i++) {
				        	controllerDataMap.put(DatastreamsList.get(i).getId(), DatastreamsList.get(i).getValue());
				        }
				        controllerMap.put("data", controllerDataMap);
			        }else {
			        	controllerMap.put("data", false);
			        }
			        
			        List<Timer> timerList = timerDao.findTimerByDeviceSnAndWay(controller.getDevice_sn(), controller.getPort());
			        Limit_Install limit= limitDao.findLimitByDeviceSnsAndWay(controller.getDevice_sn(), controller.getPort());
			       // List<Limit_Install> limitList = limitDao.queryLimitByDeviceSn(controller.getDevice_sn());
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

}
