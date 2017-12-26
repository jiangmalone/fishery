/**
 * 
 */
package com.geariot.platform.fishery.dao;

import com.geariot.platform.fishery.entities.Admin;

/**
 * @author mxy940127
 *
 */
public interface AdminDao {
	
	Admin findAdminByAccount(String account);
	
	void save(Admin admin);
	
	Admin findAdminByAdminId(int adminId);
}
