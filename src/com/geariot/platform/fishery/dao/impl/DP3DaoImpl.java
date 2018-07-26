package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.DP3Dao;
import com.geariot.platform.fishery.entities.DP3;
@Repository
public class DP3DaoImpl implements DP3Dao {
	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	@Override
	public void save(DP3 dp3) {
		// TODO Auto-generated method stub
		this.getSession().save(dp3);
	}

}
