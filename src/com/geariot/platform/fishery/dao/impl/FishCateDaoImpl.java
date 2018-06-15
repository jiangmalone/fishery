/**
 * 
 */
package com.geariot.platform.fishery.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.FishCateDao;
import com.geariot.platform.fishery.entities.Fish_Category;
import com.geariot.platform.fishery.utils.QueryUtils;

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

	@Override
	public void save(Fish_Category category) {
		getSession().save(category);
	}

	@Override
	public List<Fish_Category> getallfish() {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Fish_Category");
		Query query = queryUtils.getQuery();
		return query.list();
	}

}
