package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.PondFishDao;
import com.geariot.platform.fishery.utils.QueryUtils;


@Repository
public class PondFishDaoImpl implements PondFishDao {

	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public int deleteByPondId(int pondId) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "delete from PondFish");
		Query query = queryUtils.addInteger("pondId", pondId).getQuery();
		return query.executeUpdate();

	}

}
