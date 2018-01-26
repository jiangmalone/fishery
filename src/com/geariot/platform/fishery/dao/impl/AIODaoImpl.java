package com.geariot.platform.fishery.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.AIODao;
import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.Pond;
import com.geariot.platform.fishery.model.Equipment;
import com.geariot.platform.fishery.utils.QueryUtils;

@Repository
public class AIODaoImpl implements AIODao {

	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void save(AIO aio) {
		getSession().save(aio);

	}

	@Override
	public int delete(int AIOid) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "delete from AIO");
		Query query = queryUtils.addInteger("id", AIOid).getQuery();
		return query.executeUpdate();
	}

	@Override
	public AIO findAIOById(int AIOId) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from AIO");
		Query query = queryUtils.addInteger("id", AIOId).getQuery();
		return (AIO) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AIO> queryAIOByNameAndRelation(String relation, String name, int page, int number) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from AIO");
		Query query = queryUtils.addStringLike("name", name).addString("relation", relation).setFirstResult(page)
				.setMaxResults(number).getQuery();
		return query.list();
	}

	@Override
	public long queryAIOByNameAndRelationCount(String relation, String name) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "select count(*) from AIO");
		Query query = queryUtils.addStringLike("name", name).addString("relation", relation).getQuery();
		return (long) query.uniqueResult();
	}

	@Override
	public AIO findAIOByDeviceSns(String deviceSns) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from AIO");
		Query query = queryUtils.addString("device_sn", deviceSns).getQuery();
		return (AIO) query.uniqueResult();
	}

	@Override
	public int delete(String deviceSns) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "delete from AIO");
		Query query = queryUtils.addString("device_sn", deviceSns).getQuery();
		return query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AIO> findAIOsByPondId(int pondId) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from AIO");
		Query query = queryUtils.addInteger("pondId", pondId).getQuery();
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AIO> queryAIOByNameAndRelation(String relation, String name) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from AIO");
		Query query = queryUtils.addStringLike("name", name).addString("relation", relation).getQuery();
		return query.list();
	}

	@Override
	public void update(AIO aio) {
		// TODO Auto-generated method stub
		this.getSession().merge(aio);
	}

	@Override
	public AIO findAIOByDeviceSnAndWay(String device_sn, int way) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from AIO");
		Query query = queryUtils.addString("device_sn", device_sn).addInteger("way", way).getQuery();
		return (AIO) query.uniqueResult();
	}

}
