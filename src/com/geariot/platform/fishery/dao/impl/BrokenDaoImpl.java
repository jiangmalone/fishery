package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.BrokenDao;
import com.geariot.platform.fishery.entities.Broken;
import com.geariot.platform.fishery.utils.QueryUtils;

@Repository
public class BrokenDaoImpl implements BrokenDao{

	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public void save(Broken broken) {
		// TODO Auto-generated method stub
		getSession().save(broken);
	}

	@Override
	public Broken findByEntityModelAndEntityType(int EntityModel, int EntityType) {
		// TODO Auto-generated method stub
		
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Broken");
		Query query = queryUtils.addInteger("EntityModel", EntityModel)
				.addInteger("EntityType", EntityType)
				.getQuery();
		return (Broken) query.uniqueResult();
	}

}
