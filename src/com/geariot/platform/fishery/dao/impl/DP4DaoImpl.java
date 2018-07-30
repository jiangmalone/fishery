package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.DP4Dao;
import com.geariot.platform.fishery.entities.DP4;
@Repository
public class DP4DaoImpl implements DP4Dao {
	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	@Override
	public void save(DP4 dp4) {
		// TODO Auto-generated method stub
		this.getSession().save(dp4);
	}

}
