/**
 * 
 */
package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.FishCateDao;

/**
 * @author mxy940127
 *
 */
@Repository
public class FishCateDaoImpl implements FishCateDao{

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public void clearFish() {
		String hql = "delete from Fish_Category";
		this.getSession().createQuery(hql).executeUpdate();
	}

}
