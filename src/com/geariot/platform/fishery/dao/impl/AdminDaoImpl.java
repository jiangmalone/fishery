/**
 * 
 */
package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.AdminDao;
import com.geariot.platform.fishery.entities.Admin;
import com.geariot.platform.fishery.utils.QueryUtils;

/**
 * @author mxy940127
 *
 */
@Repository
public class AdminDaoImpl implements AdminDao{

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public Admin findAdminByAccount(String account) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Admin");
		Query query = queryUtils.addString("account", account).getQuery();
		return (Admin) query.uniqueResult();
	}

	@Override
	public void save(Admin admin) {
		getSession().save(admin);
	}

}
