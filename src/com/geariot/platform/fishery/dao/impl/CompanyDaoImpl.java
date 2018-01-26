package com.geariot.platform.fishery.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.CompanyDao;
import com.geariot.platform.fishery.entities.Company;
import com.geariot.platform.fishery.utils.Constants;
import com.geariot.platform.fishery.utils.QueryUtils;


@Repository
public class CompanyDaoImpl implements CompanyDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public Company findCompanyByName(String name) {
		String hql = "from Company where name= :name ";
		Company company= (Company) getSession().createQuery(hql).setString("name", name).setCacheable(Constants.SELECT_CACHE).uniqueResult();
		return company;
	}

	@Override
	public void deleteCompany(int companyId) {
		String hql = "delete from Company where id in :id";
		this.getSession().createQuery(hql).setInteger("id",companyId).executeUpdate();
		
	}

	@Override
	public void updateCompany(Company company) {
		this.getSession().merge(company);
		
	}

	@Override
	public void save(Company company) {
		this.getSession().save(company);
		
	}

	@Override
	public Company findCompanyById(int id) {
		String hql = "from Company where id= :id ";
		Company company= (Company) getSession().createQuery(hql).setInteger("id", id).setCacheable(Constants.SELECT_CACHE).uniqueResult();
		return company;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Company> queryList(String name, int page, int number) {
		QueryUtils qutils = new QueryUtils(getSession(), "from Company");
		Query query = qutils.addStringLike("name", name)
		.setFirstResult(page)
		.setMaxResults(number)
		.getQuery();
		return query.list();
	}

	@Override
	public long getQueryCount(String name) {
		QueryUtils qutils = new QueryUtils(getSession(), "select count(*) from Company");
		Query query = qutils.addStringLike("name", name)
		.getQuery();
		return (long) query.uniqueResult();
	}

	@Override
	public Company findCompanyByrelation(String relation) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Company");
		Query query = queryUtils.addString("relation", relation).getQuery();
		return (Company) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Company> companies(String name) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Company");
		Query query = queryUtils.addStringLike("name", name).getQuery();
		return query.list();
	}


}
