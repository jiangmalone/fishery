/**
 * 
 */
package com.geariot.platform.fishery.dao;

import java.util.List;

import com.geariot.platform.fishery.entities.Fish_Category;

/**
 * @author mxy940127
 *
 */
public interface FishCateDao {

	void clearFish();
	
	void save(Fish_Category category);
	List<Fish_Category> getallfish();
}
