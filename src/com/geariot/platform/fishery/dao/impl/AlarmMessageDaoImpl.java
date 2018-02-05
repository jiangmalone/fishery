package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.AlarmMessageDao;
import com.geariot.platform.fishery.entities.AlarmMessage;
@Repository
public class AlarmMessageDaoImpl implements AlarmMessageDao {
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	@Override
	public void save(AlarmMessage am) {
		// TODO Auto-generated method stub
             getSession().save(am);
	}

}
