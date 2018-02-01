package com.geariot.platform.fishery.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.Sensor_ControllerDao;
import com.geariot.platform.fishery.entities.Sensor_Controller;
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

	@Override
	public Sensor_Controller findBySensorIdAndPort(int sensorId, int port) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Sensor_Controller");
		Query query = queryUtils.addInteger("sensorId", sensorId)
						.addInteger("sensor_port", port).getQuery();
		return (Sensor_Controller) query.uniqueResult();
	}

	@Override
	public void deleteRecord(int id) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "delete from Sensor_Controller");
		Query query = queryUtils.addInteger("id", id).getQuery();
		query.executeUpdate();
	}

	@Override
	public void save(Sensor_Controller sensor_Controller) {
		getSession().save(sensor_Controller);
	}

	@Override
	public Sensor_Controller findByControllerIdAndPort(int controllerId,int controller_port) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Sensor_Controller");
		Query query = queryUtils.addInteger("controllerId", controllerId)
						.addInteger("controller_port", controller_port)
						.getQuery();
		return (Sensor_Controller) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Sensor_Controller> list(int sensorId) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Sensor_Controller");
		Query query = queryUtils.addInteger("sensorId", sensorId).getQuery();
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Sensor_Controller> controller(int controllerId) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Sensor_Controller");
		Query query = queryUtils.addInteger("controllerId", controllerId).getQuery();
		return query.list();
	}

	@Override
	public void deleteController(int controllerId) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "delete from Sensor_Controller");
		Query query = queryUtils.addInteger("controllerId", controllerId).getQuery();
		query.executeUpdate();
	}
	
	
}
