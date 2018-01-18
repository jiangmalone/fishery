package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.Sensor_ControllerDao;
import com.geariot.platform.fishery.utils.QueryUtils;

@Repository
public class Sensor_ControllerDaoImpl implements Sensor_ControllerDao{

	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public int delete(int sensorId) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "delete from Sensor_Controller");
		Query query = queryUtils.addInteger("sensorId", sensorId).getQuery();
		return query.executeUpdate();
	}
	
	
}
