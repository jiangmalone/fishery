/**
 * 
 */
package com.geariot.platform.fishery.dao.impl;



import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.PondDao;
import com.geariot.platform.fishery.entities.Pond;
import com.geariot.platform.fishery.utils.QueryUtils;

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

	@Override
	public boolean findPondByNameAndRelationId(String name, String relation) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Pond");
		Query query = queryUtils.addString("name", name)
						.addString("relation", relation)
						.getQuery();
		return !query.list().isEmpty();
	}

}
