package com.geariot.platform.fishery.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.fishery.dao.PondDao;
import com.geariot.platform.fishery.entities.Pond;
import com.geariot.platform.fishery.model.Equipment;
import com.geariot.platform.fishery.model.RESCODE;

@Service
@Transactional
public class PondService {

	@Autowired
	private PondDao pondDao;
	
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
	
	public Map<String, Object> queryPond(String relation, String name, int page, int number){
		int from = (page - 1) * number;
		List<Pond> ponds = pondDao.queryPondByNameAndRelation(relation, name, from, number);
		long count = this.pondDao.queryPondByNameAndRelationCount(relation, name);
		int size = (int) Math.ceil(count / (double) number);
		return RESCODE.SUCCESS.getJSONRES(ponds,size,count);
	}
	
	public Map<String,Object> pondEquipment(int pondId, int page, int number){
		int from = (page - 1) * number;
		List<Equipment> equipments = pondDao.findEquipmentByPondId(pondId, from, number);
		return RESCODE.SUCCESS.getJSONRES(equipments);
	}
}
