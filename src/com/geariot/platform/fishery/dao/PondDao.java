/**
 * 
 */
package com.geariot.platform.fishery.dao;

import java.util.List;

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
	
	long queryPondByNameAndRelationCount(String relation, String name);
	
	List<Equipment> findEquipmentByPondId(int pondId, int from, int pageSize);
	
	long equipmentByPondIdCount(int pondId);
}
