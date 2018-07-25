package com.geariot.platform.fishery.service;

import com.geariot.platform.fishery.dao.*;
import com.geariot.platform.fishery.entities.*;
import com.geariot.platform.fishery.model.Equipment;
import com.geariot.platform.fishery.model.RESCODE;
import com.geariot.platform.fishery.utils.Constants;
import com.geariot.platform.fishery.utils.EightInteger;
import com.geariot.platform.fishery.utils.FishCateList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PondService {

	@Autowired
	private PondDao pondDao;

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
		Controller controller = null;
		for (Integer pondId : pondIds) {
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

	public Map<String, Object> modifyPond(Pond pond) {
		Pond exist = pondDao.findPondByPondId(pond.getId());
		if (exist == null) {
			return RESCODE.POND_NOT_EXIST.getJSONRES();
		} else {
			if (pondDao.checkPondExistByNameAndRelation(pond.getName(), pond.getRelation())) {
				if (pond.getName().equals(exist.getName())) {
					pondDao.update(pond);
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

	public Map<String, Object> pondEquipment(int pondId, int page, int number) {
		int from = (page - 1) * number;
		Sensor sensor = null;
		Pond pond = pondDao.findPondByPondId(pondId);
		List<Equipment> equipments = pondDao.findEquipmentByPondId(pondId, from, number);
		for(Equipment equipment : equipments){
			if(equipment.getDevice_sn().substring(0, 2).equals("03")){
				sensor = sensorDao.findSensorByDeviceSns(equipment.getDevice_sn());
				if(sensor != null){
					equipment.setSensorId(sensor.getId());
				}
			}
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

	/*public Map<String, Object> appHomepage(String relation) {
		if(! relation.contains("WX")){
			return RESCODE.WRONG_PARAM.getJSONRES();
		}
		Sensor_Data sensor_Data = null;
		Sensor_Data oneWay = null;
		Sensor_Data twoWay = null;
		AIO aioTemp = null;
		List<Pond> ponds = pondDao.queryPondByNameAndRelation(relation, null);
		for (Pond pond : ponds) {
			List<AIO> temp = new ArrayList<>();
			// 塘口内添加sensor的list
			List<Sensor> sensors = sensorDao.findSensorsByPondId(pond.getId());
			for (Sensor sensor : sensors) {
				sensor_Data = sensor_DataDao.findDataByDeviceSns(sensor.getDevice_sn());
				if (sensor_Data == null) {
					sensor.setOxygen(0);
					sensor.setpH_value(0);
					sensor.setWater_temperature(0);
					sensor.setWayStatus(sensor.getStatus());
				} else {
					sensor.setOxygen(sensor_Data.getOxygen());
					sensor.setpH_value(sensor_Data.getpH_value());
					sensor.setWater_temperature(sensor_Data.getWater_temperature());
					sensor.setWayStatus(sensor.getStatus());
				}
			}
			pond.setSensors(sensors);
			List<AIO> aios = aioDao.findAIOsByPondId(pond.getId());
			for (AIO aio : aios) {
				oneWay = sensor_DataDao.findDataByDeviceSnAndWay(aio.getDevice_sn(), 1);
				AeratorStatus oneStatus = statusDao.findByDeviceSnAndWay(aio.getDevice_sn(), 1);
				if(oneWay == null){
					StringBuffer sb = new StringBuffer(aio.getStatus());
					char a = sb.charAt(0);
					aio.setOxygen(0);
					aio.setWater_temperature(0);
					aio.setpH_value(0);
					aio.setWay(1);
					aio.setTimed(oneStatus.isTimed());
					aio.setOnoff(oneStatus.isOn_off());
					aio.setWayStatus(Integer.parseInt(String.valueOf(a)));
				}else{
					StringBuffer sb = new StringBuffer(aio.getStatus());
					char a = sb.charAt(0);
					aio.setWater_temperature(oneWay.getWater_temperature());
					aio.setOxygen(oneWay.getOxygen());
					aio.setpH_value(oneWay.getpH_value());
					aio.setWay(1);
					aio.setTimed(oneStatus.isTimed());
					aio.setOnoff(oneStatus.isOn_off());
					aio.setWayStatus(Integer.parseInt(String.valueOf(a)));
				}
				twoWay = sensor_DataDao.findDataByDeviceSnAndWay(aio.getDevice_sn(), 2);
				AeratorStatus twoStatus = statusDao.findByDeviceSnAndWay(aio.getDevice_sn(), 2);
				aioTemp = new AIO();
				aioTemp.setId(EightInteger.eightInteger());
				aioTemp.setDevice_sn(aio.getDevice_sn());
				aioTemp.setName(aio.getName());
				aioTemp.setPondId(aio.getPondId());
				aioTemp.setRelation(aio.getRelation());
			//	aioTemp.setType(aio.getType());
				aioTemp.setStatus(aio.getStatus());
				aioTemp.setWay(2);
				if(twoWay == null){
					StringBuffer sbs = new StringBuffer(aio.getStatus());
					char b = sbs.charAt(1);
					aioTemp.setOxygen(0);
					aioTemp.setWater_temperature(0);
					aioTemp.setpH_value(0);
					aioTemp.setTimed(twoStatus.isTimed());
					aioTemp.setOnoff(twoStatus.isOn_off());
					aioTemp.setWayStatus(Integer.parseInt(String.valueOf(b)));
				}else{
					StringBuffer sbs = new StringBuffer(aio.getStatus());
					char b = sbs.charAt(1);
					aioTemp.setWater_temperature(twoWay.getWater_temperature());
					aioTemp.setOxygen(twoWay.getOxygen());
					aioTemp.setpH_value(twoWay.getpH_value());
					aioTemp.setTimed(twoStatus.isTimed());
					aioTemp.setOnoff(twoStatus.isOn_off());
					aioTemp.setWayStatus(Integer.parseInt(String.valueOf(b)));
				}
				temp.add(aioTemp);
			}
			aios.addAll(temp);
			pond.setAios(aios);
		}
		return RESCODE.SUCCESS.getJSONRES(ponds);
	}*/

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
		long count = pondDao.equipmentRelationCount(relation);
		int size = (int) Math.ceil(count / (double) number);
		return RESCODE.SUCCESS.getJSONRES(equipments, size, count);
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
