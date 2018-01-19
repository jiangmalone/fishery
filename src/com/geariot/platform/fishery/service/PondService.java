package com.geariot.platform.fishery.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.fishery.dao.AIODao;
import com.geariot.platform.fishery.dao.FishCateDao;
import com.geariot.platform.fishery.dao.PondDao;
import com.geariot.platform.fishery.dao.SensorDao;
import com.geariot.platform.fishery.dao.Sensor_DataDao;
import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.Fish_Category;
import com.geariot.platform.fishery.entities.Pond;
import com.geariot.platform.fishery.entities.Sensor;
import com.geariot.platform.fishery.entities.Sensor_Data;
import com.geariot.platform.fishery.model.Equipment;
import com.geariot.platform.fishery.model.RESCODE;
import com.geariot.platform.fishery.utils.EightInteger;
import com.geariot.platform.fishery.utils.FishCateList;

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
	private Sensor_DataDao sensor_DataDao;
	
	private Logger logger = LogManager.getLogger(PondService.class);
	
	public Map<String, Object> addPond(Pond pond){
		if(pondDao.checkPondExistByNameAndRelation(pond.getName(), pond.getRelation())){
			return RESCODE.POND_NAME_EXIST.getJSONRES();
		}else{
			pondDao.save(pond);
			return RESCODE.SUCCESS.getJSONRES(pond);
		}
	}
	
	public Map<String, Object> delPonds(Integer... pondIds){
		for(Integer pondId : pondIds){
			//删除塘口时需要先将塘口的鱼种子表置为空,否则无法删除
			pondDao.findPondByPondId(pondId).setFish_categorys(null);
			pondDao.delete(pondId);
		}
		return RESCODE.SUCCESS.getJSONRES();
	}
	
	public Map<String, Object> modifyPond(Pond pond){
		Pond exist = pondDao.findPondByPondId(pond.getId());
		if(exist == null){
			return RESCODE.POND_NOT_EXIST.getJSONRES();
		}else{ 
			if(pondDao.checkPondExistByNameAndRelation(pond.getName(), pond.getRelation())){
				if(pond.getName().equals(exist.getName())){
					pondDao.merge(pond);
					return RESCODE.SUCCESS.getJSONRES(pond);
				}else{
					return RESCODE.POND_NAME_EXIST.getJSONRES();
				}
			}else{
				pondDao.merge(pond);
				return RESCODE.SUCCESS.getJSONRES(pond);
			}
		}
	}
	
	public Map<String, Object> queryPond(String relationId, String name, int page, int number){
		int from = (page - 1) * number;
		List<Pond> ponds = pondDao.queryPondByNameAndRelation(relationId, name, from, number);
		long count = this.pondDao.queryPondByNameAndRelationCount(relationId, name);
		int size = (int) Math.ceil(count / (double) number);
		return RESCODE.SUCCESS.getJSONRES(ponds,size,count);
	}
	
	public Map<String,Object> pondEquipment(int pondId, int page, int number){
		int from = (page - 1) * number;
		Pond pond = pondDao.findPondByPondId(pondId);
		List<Equipment> equipments = pondDao.findEquipmentByPondId(pondId, from, number);
		long count = this.pondDao.equipmentByPondIdCount(pondId);
		int size = (int) Math.ceil(count / (double) number);
		Map<String,Object> obj =  RESCODE.SUCCESS.getJSONRES(equipments,size,count);
		obj.put("pond", pond);
		return obj;
	}
	
	public void initFishCate(){
		Fish_Category category = null;
		fishCateDao.clearFish();
		logger.debug("数据库鱼种清空,并准备重新导入");
		List<String> fish_cate = FishCateList.getFishNames();
		logger.debug("从配置文件中读取到鱼种共"+fish_cate.size()+"种");
		for(String string : fish_cate){
			category = new Fish_Category();
			category.setFish_name(string);
			logger.debug("鱼种名称:"+string);
			fishCateDao.save(category);
		}
	}

	public Map<String, Object> fishCateList() {
		List<Fish_Category> list = pondDao.list();
		return RESCODE.SUCCESS.getJSONRES(list);
	}

	public Map<String, Object> WXqueryPond(String relationId) {
		Sensor_Data sensor_Data = null;
		AIO aioTemp = null;
		List<AIO> temp = new ArrayList<>();
		List<Pond> ponds = pondDao.queryPondByNameAndRelation(relationId, null);
		for(Pond pond : ponds){
			//塘口内添加sensor的list
			List<Sensor> sensors = sensorDao.findSensorsByPondId(pond.getId());
			for(Sensor sensor : sensors){
				sensor_Data = sensor_DataDao.findDataByDeviceSns(sensor.getDevice_sn());
				sensor.setOxygen(sensor_Data.getOxygen());
				sensor.setpH_value(sensor_Data.getpH_value());
				sensor.setWater_temperature(sensor_Data.getWater_temperature());
			}
			pond.setSensors(sensors);
			List<AIO> aios = aioDao.findAIOsByPondId(pond.getId());
			for(AIO aio : aios){
				sensor_Data = sensor_DataDao.findDataByDeviceSns(aio.getDevice_sn());
				if(sensor_Data.getWay() == 2){
					aioTemp = new AIO();
					aioTemp.setId(EightInteger.eightInteger());
					aioTemp.setDevice_sn(aio.getDevice_sn());
					aioTemp.setName(aio.getName());
					aioTemp.setPondId(aio.getPondId());
					aioTemp.setRelationId(aio.getRelationId());
					aioTemp.setType(aio.getType());
					aioTemp.setStatus(aio.getStatus());
					if(aio.getType() == 2){
						aioTemp.setpH_value(sensor_Data.getpH_value());
					}
					aioTemp.setOxygen(sensor_Data.getOxygen());
					aioTemp.setWater_temperature(sensor_Data.getWater_temperature());
					temp.add(aioTemp);
				}else{
					aio.setWater_temperature(sensor_Data.getWater_temperature());
					aio.setOxygen(sensor_Data.getOxygen());
					if(aio.getType() == 2){
						aioTemp.setpH_value(sensor_Data.getpH_value());
					}
				}
			}
			aios.addAll(temp);
			pond.setAios(aios);
		}
		return RESCODE.SUCCESS.getJSONRES(ponds);
	}

	public Map<String, Object> pondDetail(int pondId) {
		Pond pond = pondDao.findPondByPondId(pondId);
		if(pond == null){
			return RESCODE.NOT_FOUND.getJSONRES();
		}else{
			return RESCODE.SUCCESS.getJSONRES(pond);
		}
	}
	
	
}
