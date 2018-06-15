package com.geariot.platform.fishery.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.ControllerDao;
import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.Controller;
import com.geariot.platform.fishery.utils.QueryUtils;

@Repository
public class ControllerDaoImpl implements ControllerDao {

	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void save(Controller controller) {
		getSession().save(controller);

	}

	@Override
	public int delete(int controllerid) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "delete from Controller");
		Query query = queryUtils.addInteger("id", controllerid).getQuery();
		return query.executeUpdate();
	}

	@Override
	public Controller findControllerById(int ControllerId) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Controller");
		Query query = queryUtils.addInteger("id", ControllerId).getQuery();
		return (Controller) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Controller> queryControllerByNameAndRelation(String relation, String name, int from, int pageSize) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Controller");
		Query query = queryUtils.addStringLike("name", name).addString("relation", relation).setFirstResult(from)
				.setMaxResults(pageSize).getQuery();
		return query.list();
	}

	@Override
	public long queryControllerByNameAndRelationCount(String relation, String name) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "select count(*) from Controller");
		Query query = queryUtils.addStringLike("name", name).addString("relation", relation).getQuery();
		return (long) query.uniqueResult();
	}

	@Override
	public int delete(String deviceSns) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "delete from Controller");
		Query query = queryUtils.addString("device_sn", deviceSns).getQuery();
		return query.executeUpdate();
	}

	@Override
	public Controller findControllerByDeviceSns(String deviceSns) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Controller");
		Query query = queryUtils.addString("device_sn", deviceSns).getQuery();
		return (Controller) query.uniqueResult();
	}
	
	@Override
	public Controller findControllerByDeviceSnAndWay(String device_sn, int way) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Controller");
		Query query = queryUtils.addString("device_sn", device_sn).addInteger("way", way).getQuery();
		return (Controller) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Controller> queryControllerByNameAndRelation(String relation, String name) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Controller");
		Query query = queryUtils.addStringLike("name", name).addString("relation", relation).getQuery();
		return query.list();
	}

	@Override
	public void updateController(Controller controller) {
		this.getSession().merge(controller);
	}

	@Override
	public void updateByPondId(int pondId) {
		String sql = "update controller set pondId = 0 , port_status = '0000' where pondId = :pondId";
		getSession().createSQLQuery(sql).setInteger("pondId", pondId).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Controller> findByPondId(int pondId) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Controller");
		Query query = queryUtils.addInteger("pondId", pondId).getQuery();
		return query.list();
	}

	@Override
	public void deleteByRelation(String relation) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "delete from Controller");
		Query query = queryUtils.addString("relation", relation).getQuery();
		query.executeUpdate();
	}

	@Override
	public List<Controller> findByRelation(String relation) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Controller");
		Query query = queryUtils.addString("relation", relation).getQuery();
		return query.list();
	}

} 
