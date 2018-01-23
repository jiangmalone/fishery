package com.geariot.platform.fishery.dao;

import com.geariot.platform.fishery.entities.Broken;

public interface BrokenDao {

	
	void save(Broken broken);
	
	Broken findByEntityModelAndEntityType(int EntityModel,int EntityType);
	
}
