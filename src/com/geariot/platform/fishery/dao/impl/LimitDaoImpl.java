package com.geariot.platform.fishery.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.fishery.dao.LimitDao;
import com.geariot.platform.fishery.entities.Limit_Install;
import com.geariot.platform.fishery.utils.Constants;
import com.geariot.platform.fishery.utils.QueryUtils;
@Repository
@Transactional 
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
		String hql = "delete from Limit_Install where device_sn = :device_sn";
		this.getSession().createQuery(hql).setString("device_sn",device_sn).executeUpdate();
	}
	
	@Override
	public void deleteByDevice_snandWay(String device_sn,int way) {
		/*String hql = "delete from Limit_Install where device_sn = :device_sn and way = :way";
		this.getSession().createQuery(hql).setString("device_sn",device_sn).setInteger("way", way)										
										.executeUpdate();*/
		String hql = "delete from Limit_Install where device_sn = :device_sn and way = :way";
		this.getSession().createQuery(hql).setString("device_sn",device_sn).setInteger("way", way)									
										.executeUpdate();
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
	public List<Limit_Install> queryLimitByDeviceSn(String device_sn) {
		QueryUtils qutils = new QueryUtils(getSession(), "limit_install");
		Query query = qutils.addString("device_sn", device_sn)
		.getQuery();
		return query.list();
	}

	@Override
	public void updateLimit(Limit_Install limit_Install) {
		/*String sql = "update limit_install set high_limit=:high_limit,low_limit=:low_limit,up_limit=:up_limit where device_sn = :device_sn and way = :way";
		getSession().createSQLQuery(sql)
		.setFloat("high_limit", limit_Install.getHigh_limit())
		.setFloat("low_limit", limit_Install.getLow_limit())
		.setFloat("up_limit", limit_Install.getUp_limit())
		.setString("device_sn", limit_Install.getDevice_sn())
		.setInteger("way", limit_Install.getWay()).executeUpdate();*/
		this.getSession().merge(limit_Install);
	}

	
	@Override
	public Limit_Install findLimitByDeviceSnsAndWay(String device_sn, int way) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Limit_Install");
		Query query = queryUtils.addString("device_sn", device_sn).addInteger("way", way).getQuery();
		return  (Limit_Install) query.uniqueResult();
	}

	@Override
	public void delAuto(String device_sn, int way) {
		String sql = "update limit_install set valid=false where device_sn= :device_sn and way = :way";
		this.getSession().createSQLQuery(sql).setString("device_sn", device_sn)
						.setInteger("way", way).setCacheable(Constants.SELECT_CACHE).executeUpdate();		
	}

	@Override
	public void openAuto(String device_sn, int way) {
		String sql = "update limit_install set valid=true where device_sn= :device_sn and way = :way";
		this.getSession().createSQLQuery(sql).setString("device_sn", device_sn)
						.setInteger("way", way).setCacheable(Constants.SELECT_CACHE).executeUpdate();
		
	}

}
