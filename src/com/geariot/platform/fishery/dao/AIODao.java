package com.geariot.platform.fishery.dao;

import java.util.List;

import com.geariot.platform.fishery.entities.AIO;

public interface AIODao {

	void save(AIO aio);

	int delete(int AIOid);
	
	int delete(String deviceSns);

	AIO findAIOById(int AIOId);
	
	AIO findAIOByDeviceSns(String deviceSns);

	List<AIO> queryAIOByNameAndRelation(String relation, String name, int from, int pageSize);
	
	List<AIO> findAIOsByPondId(int pondId);
	
	List<AIO> queryAIOByNameAndRelation(String relation, String name);

	long queryAIOByNameAndRelationCount(String relation, String name);

}
