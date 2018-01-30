/**
 * 
 */
package com.geariot.platform.fishery.dao;

import com.geariot.platform.fishery.entities.AeratorStatus;

/**
 * @author mxy940127
 *
 */
public interface AeratorStatusDao {

	void save(AeratorStatus status);
	
	AeratorStatus findByDeviceSnAndWay(String device_sn, int way);
}
