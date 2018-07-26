package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.DOSDao;
import com.geariot.platform.fishery.entities.DOS;
@Repository
public class DOSDaoImpl implements DOSDao {
	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	@Override
	public void save(DOS dos) {
		this.getSession().save(dos);
		
	}


}
