/**
 * 
 */
package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.PondDao;
import com.geariot.platform.fishery.entities.Pond;

/**
 * @author mxy940127
 *
 */

@Repository
public class PondDaoImpl implements PondDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession(){
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public void save(Pond pond) {
		getSession().save(pond);
	}

}
