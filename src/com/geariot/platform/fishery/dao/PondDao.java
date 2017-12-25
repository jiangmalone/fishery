/**
 * 
 */
package com.geariot.platform.fishery.dao;

import com.geariot.platform.fishery.entities.Pond;

/**
 * @author mxy940127
 *
 */
public interface PondDao {
	
	void save(Pond pond);
	
	boolean findPondByNameAndRelationId(String name, String relation);
}
