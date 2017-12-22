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
	
	public Map<String, Object> test(){
		List<String> list = new ArrayList<>();
		list.add("鲫鱼");
		list.add("刀鱼");
		Pond pond = new Pond();
		pond.setArea(20.0f);
		pond.setDensity(1.2f);
		pond.setDepth(15f);
		pond.setLatitude(10.23548f);
		pond.setLongitude(75.1234f);
		pond.setName("nihao");
		pond.setWater_source("changjiang");
		pond.setAddress("南京小易信息");
		pond.setFish_category(list);
		pond.setSediment_thickness(0.5f);
		pondDao.save(pond);
		return RESCODE.SUCCESS.getJSONRES(pond);
	}
}
