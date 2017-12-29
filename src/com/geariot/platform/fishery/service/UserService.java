package com.geariot.platform.fishery.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.fishery.dao.CompanyDao;
import com.geariot.platform.fishery.dao.WXUserDao;
import com.geariot.platform.fishery.entities.Company;
import com.geariot.platform.fishery.entities.WXUser;
import com.geariot.platform.fishery.model.RESCODE;


import net.sf.json.JsonConfig;

@Service
@Transactional
public class UserService {
    @Autowired
    private CompanyDao companyDao;
    
    @Autowired
    private WXUserDao wxuserDao;
    
	public Map<String, Object> addWXUser(WXUser wxuser) {
		WXUser exist = wxuserDao.findUserByOpenId(wxuser.getOpenId());
		if (exist != null) {
			return RESCODE.ACCOUNT_EXIST.getJSONRES();
		}
		wxuser.setCreateDate(new Date());
		wxuserDao.save(wxuser);
		return RESCODE.SUCCESS.getJSONRES(wxuser);
	}

	public Map<String, Object> addCompany(Company company) {
		Company exist = companyDao.findCompanyByName(company.getName());
		if (exist != null) {
			return RESCODE.ACCOUNT_EXIST.getJSONRES();
		}
		company.setCreateDate(new Date());
		companyDao.save(company);
		return RESCODE.SUCCESS.getJSONRES(company);
	}

	public Map<String, Object> deleteCompany(Integer[] companyIds) {
		for (int companyId : companyIds) {
			Company exist = companyDao.findCompanyById(companyId);
			if (exist == null) {
				return RESCODE.DELETE_ERROR.getJSONRES();
			} else {
				companyDao.deleteCompany(companyId);
			}
		}
		return RESCODE.SUCCESS.getJSONRES();
	}

	public Map<String, Object> deleteWXUser(Integer[] WXUserIds) {
		for (int WXUserId : WXUserIds) {
			WXUser exist = wxuserDao.findUserById(WXUserId);
			if (exist == null) {
				return RESCODE.DELETE_ERROR.getJSONRES();
			} else {
				wxuserDao.deleteUser(WXUserId);
			}
		}
		return RESCODE.SUCCESS.getJSONRES();
	}

	public Map<String, Object> modifyCompany(Company company) {
		Company exist = companyDao.findCompanyById(company.getId());
		if (exist == null) {
			return RESCODE.NOT_FOUND.getJSONRES();
		} 
		exist.setName(company.getName());
		exist.setPhone(company.getPhone());
		exist.setAddress(company.getAddress());
		exist.setLife(company.getLife());
		exist.setMail_address(company.getMail_address());
		companyDao.updateCompany(exist);
		
		return RESCODE.SUCCESS.getJSONRES(exist);
	}

	public Map<String, Object> modifyWXUser(WXUser wxuser) {
		WXUser exist = wxuserDao.findUserById(wxuser.getId());
		if (exist == null) {
			return RESCODE.NOT_FOUND.getJSONRES();
		} 
		
		exist.setName(wxuser.getName());
		exist.setPhone(wxuser.getPhone());
		exist.setAddress(wxuser.getAddress());
		exist.setLife(wxuser.getLife());
		exist.setSex(wxuser.getSex());
		wxuserDao.updateUser(exist);
		
		return RESCODE.SUCCESS.getJSONRES(exist);
	}

	public Map<String, Object> queryCompany(String name, int page, int number) {
		int from = (page - 1) * number;
		List<Company> list = companyDao.queryList(name, from, number);
		if (list == null || list.isEmpty()) {
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		long count = (long) companyDao.getQueryCount(name);
		int size = (int) Math.ceil(count / (double) number);
		return RESCODE.SUCCESS.getJSONRES(list,size,count);
	}

	public Map<String, Object> queryWXUser(String name, int page, int number) {
		int from = (page - 1) * number;
		List<WXUser> list = wxuserDao.queryList(name, from, number);
		if (list == null || list.isEmpty()) {
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		long count = (long) wxuserDao.getQueryCount(name);
		int size = (int) Math.ceil(count / (double) number);
		return RESCODE.SUCCESS.getJSONRES(list,size,count);
	}

}
