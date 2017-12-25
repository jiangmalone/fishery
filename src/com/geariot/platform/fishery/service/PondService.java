package com.geariot.platform.fishery.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.fishery.dao.PondDao;
import com.geariot.platform.fishery.entities.Pond;
import com.geariot.platform.fishery.model.RESCODE;

@Service
@Transactional
public class PondService {

	@Autowired
	private PondDao pondDao;
	
	public Map<String, Object> addPond(Pond pond){
		if(pondDao.findPondByNameAndRelationId(pond.getName(), pond.getRelation())){
			return RESCODE.POND_NAME_EXIST.getJSONRES();
		}else{
			pondDao.save(pond);
			return RESCODE.SUCCESS.getJSONRES(pond);
		}
	}
}
