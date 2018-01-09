package com.geariot.platform.fishery.dao;

import com.geariot.platform.fishery.entities.SelfTest;


public interface SelfTestDao {
	
	
	void save(SelfTest selfTest);

	int delete(int id);

	SelfTest findSelfTestById(int id);

	

}
