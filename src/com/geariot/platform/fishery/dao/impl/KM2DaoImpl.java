package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.KM2Dao;
import com.geariot.platform.fishery.entities.KM2;
@Repository
public class KM2DaoImpl implements KM2Dao {
	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	@Override
	public void save(KM2 km2) {
		// TODO Auto-generated method stub
		this.getSession().save(km2);
	}

}
