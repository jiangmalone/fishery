package com.geariot.platform.fishery.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.fishery.dao.SensorDao;
import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.Sensor;
import com.geariot.platform.fishery.utils.QueryUtils;
@Transactional
@Repository
public class SensorDaoImpl implements SensorDao {

	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void save(Sensor sensor) {
		getSession().save(sensor);

	}

	@Override
	public int delete(int sensorid) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "delete from Sensor");
		Query query = queryUtils.addInteger("id", sensorid).getQuery();
		return query.executeUpdate();
	}

	@Override
	public Sensor findSensorById(int SensorId) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Sensor");
		Query query = queryUtils.addInteger("id", SensorId).getQuery();
		return (Sensor) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Sensor> querySensorByNameAndRelation(String relation, String name, int from, int pageSize) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Sensor");
		Query query = queryUtils.addStringLike("name", name).addString("relation", relation).setFirstResult(from)
				.setMaxResults(pageSize).getQuery();
		return query.list();
	}

	@Override
	public long querySensorByNameAndRelationCount(String relation, String name) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "select count(*) from Sensor");
		Query query = queryUtils.addStringLike("name", name).addString("relation", relation).getQuery();
		return (long) query.uniqueResult();
	}

	@Override
	public int delete(String deviceSns) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "delete from Sensor");
		Query query = queryUtils.addString("device_sn", deviceSns).getQuery();
		return query.executeUpdate();
	}

	@Override
	public Sensor findSensorByDeviceSns(String deviceSns) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Sensor");
		Query query = queryUtils.addString("device_sn", deviceSns).getQuery();
		return (Sensor) query.uniqueResult();
	}

	
	@Override
	public Sensor findSensorByDeviceSnAndWay(String device_sn, int way) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Sensor");
		Query query = queryUtils.addString("device_sn", device_sn).addInteger("way", way).getQuery();
		return (Sensor) query.uniqueResult();
	}
	//@SuppressWarnings("unchecked")
	@Override
	public List<Sensor> findSensorsByPondId(int pondId) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Sensor");
		Query query = queryUtils.addInteger("pondId", pondId).getQuery();
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Sensor> querySensorByNameAndRelation(String relation, String name) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Sensor");
		Query query = queryUtils.addStringLike("name", name).addString("relation", relation).getQuery();
		return query.list();
	}

	@Override
	public void updateSensor(Sensor sensor) {
		this.getSession().merge(sensor);
	}
	
	@Override
	public void updateSensorByDevicesn(Sensor sensor) {
		String sql = "update sensor set pondId = :pondId , name = :name where device_sn = :device_sn";
		getSession().createSQLQuery(sql).setString("device_sn", sensor.getDevice_sn())
										.setInteger("pondId", sensor.getPondId())
										.setString("name", sensor.getName())
										.executeUpdate();
	}

	@Override
	public void updateByPondId(int pondId) {
		String sql = "update sensor set pondId = 0 , port_status = '00' where pondId = :pondId";
		getSession().createSQLQuery(sql).setInteger("pondId", pondId).executeUpdate();
	}

	@Override
	public void deleteByRelation(String relation) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "delete from Sensor");
		Query query = queryUtils.addString("relation", relation).getQuery();
		query.executeUpdate();
	}

	@Override
	public List<Sensor> findSensorsByRelation(String relation) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Sensor");
		Query query = queryUtils.addString("relation", relation).getQuery();
		return query.list();
	}

}
