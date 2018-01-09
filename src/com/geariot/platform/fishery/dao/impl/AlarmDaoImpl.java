package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.geariot.platform.fishery.dao.AlarmDao;
import com.geariot.platform.fishery.entities.Alarm;

public class AlarmDaoImpl implements AlarmDao {
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public void save(Alarm alarm) {
		// TODO Auto-generated method stub
      this.getSession().save(alarm);
	}

}
