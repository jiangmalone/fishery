package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.DP2Dao;
import com.geariot.platform.fishery.entities.DP2;
@Repository
public class DP2DaoImpl implements DP2Dao {
	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	@Override
	public void save(DP2 dp2) {
		// TODO Auto-generated method stub
		this.getSession().save(dp2);
	}

}
