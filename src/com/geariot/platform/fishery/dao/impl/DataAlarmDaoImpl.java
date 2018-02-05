package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.DataAlarmDao;
import com.geariot.platform.fishery.entities.DataAlarm;
@Repository
public class DataAlarmDaoImpl implements DataAlarmDao {
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	@Override
	public void save(DataAlarm dataAlarm) {
		// TODO Auto-generated method stub
          getSession().save(dataAlarm);
	}

}
