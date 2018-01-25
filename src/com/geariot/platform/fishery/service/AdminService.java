/**
 * 
 */
package com.geariot.platform.fishery.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.fishery.dao.AdminDao;
import com.geariot.platform.fishery.dao.CompanyDao;
import com.geariot.platform.fishery.entities.Admin;
import com.geariot.platform.fishery.entities.Company;
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
	
	@Autowired
	private CompanyDao companyDao;
	
	public Map<String, Object> add(Admin admin) {
		Admin exist = adminDao.findAdminByAccount(admin.getAccount());
		if(exist != null){
			return RESCODE.ACCOUNT_EXIST.getJSONRES();
		}
		else {
			admin.setPassword(MD5.compute(admin.getPassword()));
			if(admin.getCompanyId()>0){
				Company com = companyDao.findCompanyById(admin.getCompanyId());
				com.setAccount(admin.getAccount());
				com.setPassword(admin.getPassword());
				com.setComment(admin.getComment());
				com.setHasAccount(true);
			}
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
				return RESCODE.SUCCESS.getJSONRES(exist);
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
			if(exist.getCompanyId()>0){
				Company com = companyDao.findCompanyById(exist.getCompanyId());
				com.setAccount(exist.getAccount());
				com.setPassword(exist.getPassword());
				com.setComment(exist.getComment());
			}
			return RESCODE.SUCCESS.getJSONRES();
		}
	}

}
