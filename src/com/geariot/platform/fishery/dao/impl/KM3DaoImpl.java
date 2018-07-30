package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.KM3Dao;
import com.geariot.platform.fishery.entities.KM3;
@Repository
public class KM3DaoImpl implements KM3Dao {
	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	@Override
	public void save(KM3 km3) {
		// TODO Auto-generated method stub
		this.getSession().save(km3);
	}

}
