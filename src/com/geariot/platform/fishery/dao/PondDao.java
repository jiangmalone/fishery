/**
 * 
 */
package com.geariot.platform.fishery.dao;

import java.util.List;

import com.geariot.platform.fishery.entities.Company;
import com.geariot.platform.fishery.entities.Fish_Category;
import com.geariot.platform.fishery.entities.Pond;
import com.geariot.platform.fishery.model.Equipment;

/**
 * @author mxy940127
 *
 */
public interface PondDao {
	
	void save(Pond pond);
	
	boolean checkPondExistByNameAndRelation(String name, String relation);
	
	int delete(int pondId);
	
	void merge(Pond pond);
	
	Pond findPondByPondId(int pondId);
	
	List<Pond> queryPondByNameAndRelation(String relation, String name, int from, int pageSize);
	
	List<Pond> queryPondByNameAndRelation(String relation, String name);
	
	long queryPondByNameAndRelationCount(String relation, String name);
	
	List<Equipment> findEquipmentByPondId(int pondId, int from, int pageSize);
	
	long equipmentByPondIdCount(int pondId);
	
	List<Fish_Category> list();
	
	List<Equipment> adminFindEquipmentAll(int from, int pageSize);
	
	long adminFindEquipmentCountAll();
	
	List<Equipment> adminFindEquipmentByCo(List<Company> companies , int from, int pageSize);
	
	long adminFindEquipmentCountCo(List<Company> companies);
	
	List<Equipment> adminFindEquipmentBySn(String device_sn);
	
	long adminFindEquipmentCountSn(String device_sn);
	
	List<Equipment> adminFindEquipmentDouble(String device_sn, List<Company> companies , int from, int pageSize);
	
	long adminFindEquipmentCountDouble(String device_sn, List<Company> companies);
	
	List<Equipment> equipmentRelation(String relationId, int from, int pageSize);
	
	long equipmentRelationCount(String relationId);
}
