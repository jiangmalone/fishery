package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.SelfTestDao;
import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.SelfTest;
import com.geariot.platform.fishery.utils.QueryUtils;
@Repository
public class SelfTestDaoImpl implements SelfTestDao {
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession(){
		return sessionFactory.getCurrentSession();
	}
	@Override
	public void save(SelfTest selfTest) {
		getSession().save(selfTest);

	}

	@Override
	public int delete(int id) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "delete from SelfTest");
		Query query = queryUtils.addInteger("id", id).getQuery();
		return query.executeUpdate();
	}

	@Override
	public SelfTest findSelfTestById(int id) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from SelfTest");
		Query query = queryUtils.addInteger("id", id).getQuery();
		return (SelfTest) query.uniqueResult();
	}

}
