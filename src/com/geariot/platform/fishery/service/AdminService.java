/**
 * 
 */
package com.geariot.platform.fishery.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.fishery.dao.AdminDao;
import com.geariot.platform.fishery.entities.Admin;
import com.geariot.platform.fishery.model.RESCODE;
import com.geariot.platform.fishery.utils.MD5;

import net.sf.json.JsonConfig;

/**
 * @author mxy940127
 *
 */
@Service
@Transactional
public class AdminService {

	@Autowired
	private AdminDao adminDao;
	
	public Map<String, Object> add(Admin admin) {
		Admin exist = adminDao.findAdminByAccount(admin.getAccount());
		if(exist != null){
			return RESCODE.ACCOUNT_EXIST.getJSONRES();
		}
		else {
			admin.setPassword(MD5.compute(admin.getPassword()));
			adminDao.save(admin);
			return RESCODE.SUCCESS.getJSONRES(admin);
		}
	}

	public Map<String, Object> login(String account, String password) {
		Admin exist = adminDao.findAdminByAccount(account);
		if(exist == null){
			return RESCODE.ACCOUNT_NOT_EXIST.getJSONRES();
		}else{
			if(exist.getPassword().equals(MD5.compute(password))){
				return RESCODE.SUCCESS.getJSONRES();
			}else{
				return RESCODE.PSW_ERROR.getJSONRES();
			}
		}
	}

	public Map<String, Object> logout() {
		return RESCODE.SUCCESS.getJSONRES();
	}

	public Map<String, Object> modifyPSW(int adminId, String password) {
		Admin exist = adminDao.findAdminByAdminId(adminId);
		if(exist == null){
			return RESCODE.ACCOUNT_NOT_EXIST.getJSONRES();
		}else{
			exist.setPassword(MD5.compute(password));
			return RESCODE.SUCCESS.getJSONRES();
		}
	}

}
