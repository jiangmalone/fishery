package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.KM4Dao;
import com.geariot.platform.fishery.entities.KM4;
@Repository
public class KM4DaoImpl implements KM4Dao {
	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	@Override
	public void save(KM4 km4) {
		// TODO Auto-generated method stub
		this.getSession().save(km4);
	}

}
