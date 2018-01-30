/**
 * 
 */
package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.AeratorStatusDao;
import com.geariot.platform.fishery.entities.AeratorStatus;
import com.geariot.platform.fishery.utils.QueryUtils;

/**
 * @author mxy940127
 *
 */
@Repository
public class AeratorStatusDaoImpl implements AeratorStatusDao{

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public void save(AeratorStatus status) {
		getSession().save(status);
	}

	@Override
	public AeratorStatus findByDeviceSnAndWay(String device_sn, int way) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from AeratorStatus");
		Query query = queryUtils.addString("device_sn", device_sn)
				.addInteger("way", way)
				.getQuery();
		return (AeratorStatus) query.uniqueResult();
	}

}
