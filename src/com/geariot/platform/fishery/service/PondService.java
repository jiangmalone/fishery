package com.geariot.platform.fishery.service;

import com.geariot.platform.fishery.dao.*;
import com.geariot.platform.fishery.entities.*;
import com.geariot.platform.fishery.model.RESCODE;
import com.geariot.platform.fishery.utils.Constants;
import com.geariot.platform.fishery.utils.EightInteger;
import com.geariot.platform.fishery.utils.FishCateList;

import cmcc.iot.onenet.javasdk.api.device.GetDevicesStatus;
import cmcc.iot.onenet.javasdk.api.device.GetLatesDeviceData;
import cmcc.iot.onenet.javasdk.response.BasicResponse;
import cmcc.iot.onenet.javasdk.response.device.DeciceLatestDataPoint;
import cmcc.iot.onenet.javasdk.response.device.DevicesStatusList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PondService {

	@Autowired
	private PondDao pondDao;
	
	@Autowired
	private DeviceDao deviceDao;

	@Autowired
	private FishCateDao fishCateDao;

	@Autowired
	private SensorDao sensorDao;

	@Autowired
	private AIODao aioDao;
	
	@Autowired
	private ControllerDao controllerDao;

	@Autowired
	private Sensor_DataDao sensor_DataDao;
	
	@Autowired
	private Sensor_ControllerDao sensor_ControllerDao;
	
	@Autowired
	private AeratorStatusDao statusDao;
	
	@Autowired
	private PondFishDao pondfishDao;
	
	@Autowired
	private WXUserDao wxUserDao;
	
	@Autowired
	private CompanyDao companyDao;

	@Autowired
	private Dev_TriggerDao triggerDao;
	
	@Autowired
	private EquipmentService equipmentService;
	
	private String key = "7zMmzMWnY1jlegImd=m4p9EgZiI=";
	
	private Logger logger = LogManager.getLogger(PondService.class);

	public Map<String, Object> addPond(Pond pond) {
		if (pondDao.checkPondExistByNameAndRelation(pond.getName(), pond.getRelation())) {
			return RESCODE.POND_NAME_EXIST.getJSONRES();
		} else {
			pondDao.save(pond);
			
			
			
			return RESCODE.SUCCESS.getJSONRES(pond);
		}
	}

//	private void changeControllerPortStatusClose(Controller controller, int port) {
//		StringBuffer sb = new StringBuffer(controller.getPort_status());
//		sb.setCharAt(port - 1, '0');
//		controller.setPort_status(sb.toString());
//	}
	
	public Map<String, Object> delPonds(Integer... pondIds) {
		logger.debug("开始删除塘口："+pondIds.toString());
	
		Controller controller = null;
		for (Integer pondId : pondIds) {
			logger.debug("开始删除塘口："+pondId);
			logger.debug("删除用户："+pondDao.findPondByPondId(pondId).getRelation()+"的塘口："+pondDao.findPondByPondId(pondId).getName());;
			// 删除塘口时需要先将塘口的鱼种子表置为空,否则无法删除
			//pondDao.findPondByPondId(pondId).setPondFishs(null);  
			pondfishDao.deleteByPondId(pondId);
			pondDao.delete(pondId);
			aioDao.updateByPondId(pondId);
			List<Sensor> sensors = sensorDao.findSensorsByPondId(pondId);
			for(Sensor sensor : sensors){
				List<Sensor_Controller> sensor_Controllers = sensor_ControllerDao.list(sensor.getId());
				for(Sensor_Controller sensor_Controller : sensor_Controllers){
					controller = controllerDao.findControllerById(sensor_Controller.getControllerId());
					if(controller == null){
						continue;
					}else{
					//	changeControllerPortStatusClose(controller, sensor_Controller.getController_port());
					}
				}
				sensor_ControllerDao.delete(sensor.getId());
			}
			sensorDao.updateByPondId(pondId);
		}
		return RESCODE.SUCCESS.getJSONRES();
	}

	/**
	 * 修改塘口
	 * 1.不修改鱼的种类
	 * 2.修改鱼的种类
	 * 		修改该塘口下传感器的触发器
	 * @param pond
	 * @return
	 */
	public Map<String, Object> modifyPond(Pond pond) {
		
		logger.debug("开始修改塘口："+pond.getId());
		Pond exist = pondDao.findPondByPondId(pond.getId());
		if (exist == null) {
			logger.debug("塘口："+pond.getId()+"不存在");
			return RESCODE.POND_NOT_EXIST.getJSONRES();
		} else {
			if (pondDao.checkPondExistByNameAndRelation(pond.getName(), pond.getRelation())) {
				if (pond.getName().equals(exist.getName())) {
					pondDao.update(pond);
					List<PondFish> pondfishes =  pondfishDao.getFishbyPondId(pond.getId());
					List<Sensor> sensorList = sensorDao.findSensorsByPondId(pond.getId());
					if(pondfishes != null && pondfishes.size()>0) {
						logger.debug("修改传感器的触发器");
						for(int i = 0 ; i<sensorList.size();i++) {
							equipmentService.deleteTriggerBySensorId(sensorList.get(i).getDevice_sn());
							equipmentService.addTrigerbyFishtype(sensorList.get(i).getDevice_sn(), pondfishes.get(0).getType());
						}
						
					}else {
						
						logger.debug("塘口中没有鱼/虾/蟹,删除传感器的触发器");
						for(int i = 0 ; i<sensorList.size();i++) {
							equipmentService.deleteTriggerBySensorId(sensorList.get(i).getDevice_sn());
						}						
					}
					
					return RESCODE.SUCCESS.getJSONRES(pond);
				} else {
					return RESCODE.POND_NAME_EXIST.getJSONRES();
				}
			} else {
				pondDao.update(pond);
				return RESCODE.SUCCESS.getJSONRES(pond);
			}
		}
	}

	public Map<String, Object> queryPond(String relation, String name, int page, int number) {
		int from = (page - 1) * number;
		List<Pond> ponds = pondDao.queryPondByNameAndRelation(relation, name, from, number);
		long count = this.pondDao.queryPondByNameAndRelationCount(relation, name);
		int size = (int) Math.ceil(count / (double) number);
		Map<String, Object> map = RESCODE.SUCCESS.getJSONRES(ponds, size, count);
		if(relation!=null&&relation.length()>0){
			if(relation.contains("WX")){
				WXUser wxUser = wxUserDao.findUserByRelation(relation);
				map.put("user", wxUser==null?"":wxUser.getName());
				return map;
			}
			if(relation.contains("CO")){
				Company company = companyDao.findCompanyByRelation(relation);
				map.put("user", company==null?"":company.getName());
				return map;
			}
			map.put("user", "");
			return map;
		}
		map.put("user", "");
		return map;
	}
	
	public Map<String, Object> WXqueryPond(String relation){
		List<Pond> ponds = pondDao.queryPondByRelation(relation);
		Map<String, Object> map = RESCODE.SUCCESS.getJSONRES(ponds);
		if(relation!=null&&relation.length()>0){
			if(relation.contains("WX")){
				WXUser wxUser = wxUserDao.findUserByRelation(relation);
				map.put("user", wxUser==null?"":wxUser.getName());
				return map;
			}
		}
		map.put("user", "");
		return map;
	}

	public Map<String, Object> pondEquipment(int pondId, int page, int number) {
		int from = (page - 1) * number;
		Sensor sensor = null;
		Pond pond = pondDao.findPondByPondId(pondId);
		List<Equipment> equipments = pondDao.findEquipmentByPondId(pondId, from, number);
		List<Equipment> equipmentsReturn = new ArrayList<>();
		for(Equipment equipment : equipments){
			equipment.setType(deviceDao.findDevice(equipment.getDevice_sn()).getType());
			String relation ="";
			if(sensorDao.findSensorByDeviceSns(equipment.getDevice_sn())!=null) {
				relation = sensorDao.findSensorByDeviceSns(equipment.getDevice_sn()).getRelation();
			}
			if(relation.contains("CO")) {
				equipment.setUserName(companyDao.findCompanyByRelation(relation).getName());
			}else if(relation.contains("WX")) {
				equipment.setUserName(wxUserDao.findUserByRelation(relation).getName());
			}			
			if(equipment.getType()==1) {
				//传感器				
				GetDevicesStatus api = new GetDevicesStatus(equipment.getDevice_sn(),key);
		        BasicResponse<DevicesStatusList> response = api.executeApi();
		        logger.debug("从onenet上获取传感器在线/离线状态");
		        if(response.errno == 0) {
		        	if(response.data.getDevices().get(0).getIsonline()) {
		        		equipment.setStatus(1);
		        	}else {
		        		equipment.setStatus(0);
		        	}
		        	
		        }else {
		        	logger.debug("未查询到传感器状态，显示离线");
		        	equipment.setStatus(2);
		        }
			}else if(equipment.getType()==3) {
				Map<String, Object> controllerMap=new HashMap<>();
				Map<String, Object> controllerDataMap=new HashMap<>();
				//获得设备离线/在线状态
				GetDevicesStatus api = new GetDevicesStatus(equipment.getDevice_sn(),key);
		        BasicResponse<DevicesStatusList> response = api.executeApi();
		        logger.debug("获取首页控制器状态："+response.getJson());

		        if(response.errno == 0) {
		        	if(response.data.getDevices().get(0).getIsonline()) {
		        		//设备在线
		        		//获得控制器最新数据
				        GetLatesDeviceData lddapi = new GetLatesDeviceData(equipment.getDevice_sn(), key);
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
							        	equipment.setStatus(1);//设备断电							        								        								        		
							        }else {
							        	if(controllerDataMap.get("DP"+(equipment.getWay()+1))!=null) {
							        		 String DP = (String) controllerDataMap.get("DP"+(equipment.getWay()+1));
								        	if(DP.equals("1")) {
								        		logger.debug("缺相1或正常0："+DP);
								        		equipment.setStatus(2);//设备缺相								        		
								        	}else {
								        		equipment.setStatus(4);//设备正常
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
		        		
		        	}else {
		        		equipment.setStatus(0);//设备离线
		        	}
		        	
		        	
			}else {
				equipment.setStatus(3);//数据异常
			}					
		}
			equipmentsReturn.add(equipment);
		}
		long count = this.pondDao.equipmentByPondIdCount(pondId);
		int size = (int) Math.ceil(count / (double) number);
		Map<String, Object> obj = RESCODE.SUCCESS.getJSONRES(equipments, size, count);
		obj.put("pond", pond);
		return obj;
	}

	public void initFishCate() {
		Fish_Category category = null;
		fishCateDao.clearFish();
		logger.debug("数据库鱼种清空,并准备重新导入");
		List<String> fish_cate = FishCateList.getFishNames();
		logger.debug("从配置文件中读取到鱼种共" + fish_cate.size() + "种");
		for (String string : fish_cate) {
			category = new Fish_Category();
			category.setFish_name(string);
			if(string.contains("虾"))
			category.setType(Constants.LOBSTER);
			else if(string.contains("蟹"))
				category.setType(Constants.CRAB);
			else
				category.setType(Constants.FISH);
			logger.debug("鱼种名称:" + string);
			fishCateDao.save(category);
		}
	}

	public Map<String, Object> fishCateList() {
		List<Fish_Category> list = pondDao.list();
		return RESCODE.SUCCESS.getJSONRES(list);
	}


	public Map<String, Object> pondDetail(int pondId) {
		Pond pond = pondDao.findPondByPondId(pondId);
		if (pond == null) {
			return RESCODE.NOT_FOUND.getJSONRES();
		} else {
			Map<String, Object> map = RESCODE.SUCCESS.getJSONRES(pond);
			String relation = pond.getRelation();
			if(relation!=null&&relation.length()>0){
				if(relation.contains("WX")){
					WXUser wxUser = wxUserDao.findUserByRelation(relation);
					map.put("user", wxUser==null?"":wxUser.getName());
					return map;
				}
				if(relation.contains("CO")){
					Company company = companyDao.findCompanyByRelation(relation);
					map.put("user", company==null?"":company.getName());
					return map;
				}
				map.put("user", "");
				return map;
			}
			map.put("user", "");
			return map;
		}
	}

	public Map<String, Object> relationEquipment(String relation, int page, int number) {
		int from = (page - 1) * number;
		List<Equipment> equipments = pondDao.equipmentRelation(relation, from, number);
		List<com.geariot.platform.fishery.entities.Equipment> equipmentList = new ArrayList<>();
		for(Equipment e:equipments) {
			com.geariot.platform.fishery.entities.Equipment equip = new com.geariot.platform.fishery.entities.Equipment();
			equip.setDevice_sn(e.getDevice_sn());
			equip.setName(e.getName());
			equip.setRelation(relation);
			if(sensorDao.findSensorByDeviceSns(e.getDevice_sn())!=null) {
				equip.setType(1);
			}else if(controllerDao.findControllerByDeviceSns(e.getDevice_sn())!=null){
				equip.setType(3);
			}else {
				equip.setType(0);
			}
			equipmentList.add(equip);
		}
		
		long count = pondDao.equipmentRelationCount(relation);
		int size = (int) Math.ceil(count / (double) number);
		return RESCODE.SUCCESS.getJSONRES(equipmentList, size, count);
	}
	
	public int pondHasSensor(int pondId) {
		List<Sensor> sensorList = sensorDao.findSensorsByPondId(pondId);
		if(sensorList.size()>0) {
			return 1;
		}else {
			return 0;
		}
		
	}

}
