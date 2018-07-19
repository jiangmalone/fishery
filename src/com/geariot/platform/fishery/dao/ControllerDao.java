package com.geariot.platform.fishery.dao;

import java.util.List;

import com.geariot.platform.fishery.entities.Controller;

public interface ControllerDao {

	void save(Controller controller);

	int delete(int controllerId);
	
	void updateController(Controller controller);
	
	int delete(String deviceSns);

	Controller findControllerById(int controllerId);
	
	List<Controller> findControllerByDeviceSns(String deviceSns);
	
	List<Controller> findControllerByDeviceSnAndWay(String deviceSn,int way);

	List<Controller> queryControllerByNameAndRelation(String relation, String name, int from, int pageSize);
	
	List<Controller> queryControllerByNameAndRelation(String relation, String name); 

	long queryControllerByNameAndRelationCount(String relation, String name);

	void updateByPondId(int pondId);
	
	List<Controller> findByPondId(int pondId);
	
	List<Controller> findByRelation(String relation);
	
	void deleteByRelation(String relation);
	
	List<Controller> findControllerByPondId(int PondId);
	
	//获得全部Controller
	List<Controller> getAllControllers();

	
}
