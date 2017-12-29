package com.geariot.platform.fishery.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.AIODao;
import com.geariot.platform.fishery.dao.ControllerDao;
import com.geariot.platform.fishery.dao.SensorDao;
import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.Controller;
import com.geariot.platform.fishery.entities.Pond;
import com.geariot.platform.fishery.entities.Sensor;
import com.geariot.platform.fishery.model.Equipment;
import com.geariot.platform.fishery.utils.QueryUtils;
@Repository
public class SensorDaoImpl implements SensorDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession(){
		return sessionFactory.getCurrentSession();
	}
	@Override
	public void save(Sensor sensor) {
		getSession().save(sensor);

	}

	@Override
	public int delete(int sensorid) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "delete from sensor");
		Query query = queryUtils.addInteger("id", sensorid).getQuery();
		return query.executeUpdate();
	}

	@Override
	public Sensor findSensorById(int SensorId) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Sensor");
		Query query = queryUtils.addInteger("id", SensorId).getQuery();
		return (Sensor) query.uniqueResult();
	}

	@Override
	public List<Sensor> querySensorByNameAndRelation(String relation, String name, int from, int pageSize) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Sensor");
		Query query = queryUtils.addStringLike("name", name)
						.addString("relationId",relation)
						.setFirstResult(from)
						.setMaxResults(pageSize)
						.getQuery();
		return query.list();
	}

	@Override
	public long querySensorByNameAndRelationCount(String relation, String name) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "select count(*) from Sensor");
		Query query = queryUtils.addStringLike("name", name)
						.addString("relationId",relation)
						.getQuery();
		return (long) query.uniqueResult();
	}
	@Override
	public int delete(String deviceSns) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "delete from sensor");
		Query query = queryUtils.addString("device_sn", deviceSns).getQuery();
		return query.executeUpdate();
	}
	@Override
	public Sensor findSensorByDeviceSns(String deviceSns) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Sensor");
		Query query = queryUtils.addString("device_sn", deviceSns).getQuery();
		return (Sensor) query.uniqueResult();
	}


}
