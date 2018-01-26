package com.geariot.platform.fishery.dao;

import java.util.List;

import com.geariot.platform.fishery.entities.Company;

public interface CompanyDao {
	
	Company findCompanyByName(String name);

	Company findCompanyById(int id);
	
	Company findCompanyByrelation(String relation);

	void deleteCompany(int companyId);

	void updateCompany(Company company);

	void save(Company company);

	List<Company> queryList(String name, int page, int number);

	long getQueryCount(String name);

	List<Company> companies(String name);
}
