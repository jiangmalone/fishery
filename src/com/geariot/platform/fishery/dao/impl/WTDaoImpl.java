package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.WTDao;
import com.geariot.platform.fishery.entities.WT;
@Repository
public class WTDaoImpl implements WTDao {
	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	@Override
	public void save(WT wt) {
		this.getSession().save(wt);
		
	}

}
