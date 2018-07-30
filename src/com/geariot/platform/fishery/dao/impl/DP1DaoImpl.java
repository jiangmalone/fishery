package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.DP1Dao;
import com.geariot.platform.fishery.entities.DP1;
@Repository
public class DP1DaoImpl implements DP1Dao {
	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	@Override
	public void save(DP1 dp1) {
		// TODO Auto-generated method stub
		this.getSession().save(dp1);
	}

}
