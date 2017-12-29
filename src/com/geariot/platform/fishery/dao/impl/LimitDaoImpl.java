package com.geariot.platform.fishery.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.LimitDao;
import com.geariot.platform.fishery.entities.Limit_Install;
import com.geariot.platform.fishery.utils.Constants;
import com.geariot.platform.fishery.utils.QueryUtils;
@Repository
public class LimitDaoImpl implements LimitDao {

	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public void save(Limit_Install limit_Install) {
		this.getSession().save(limit_Install);
	}

	@Override
	public void delete(String device_sn) {
		String hql = "delete from Limit_Install where device_sn in :device_sn";
		this.getSession().createQuery(hql).setString("device_sn",device_sn).executeUpdate();
	}

	@Override
	public Limit_Install findLimitById(int limitId) {
		String hql = "from Limit_Install where id= :id ";
	 Limit_Install limit_Install= (Limit_Install) getSession().createQuery(hql).setInteger("id",limitId).setCacheable(Constants.SELECT_CACHE).uniqueResult();
		return limit_Install;
	}

	@Override
	public Limit_Install findLimitByDeviceSns(String device_sn) {
		String hql = "from Limit_Install where device_sn= :device_sn ";
		 Limit_Install limit_Install= (Limit_Install) getSession().createQuery(hql).setString("device_sn",device_sn).setCacheable(Constants.SELECT_CACHE).uniqueResult();
			return limit_Install;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Limit_Install> queryLimitByDeviceSn(String device_sn, int from, int pageSize) {
		QueryUtils qutils = new QueryUtils(getSession(), "Limit_Install");
		Query query = qutils.addStringLike("device_sn", device_sn)
		.setFirstResult(from)
		.setMaxResults(pageSize)
		.getQuery();
		return query.list();
	}

	@Override
	public void updateLimit(Limit_Install limit_Install) {
		// TODO Auto-generated method stub
		this.getSession().merge(limit_Install);
	}

}
