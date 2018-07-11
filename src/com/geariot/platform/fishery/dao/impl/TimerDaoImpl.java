package com.geariot.platform.fishery.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.TimerDao;
import com.geariot.platform.fishery.entities.Timer;
import com.geariot.platform.fishery.utils.Constants;
import com.geariot.platform.fishery.utils.QueryUtils;
@Repository
public class TimerDaoImpl implements TimerDao {


	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void save(Timer timer) {
		this.getSession().save(timer);
	}

	@Override
	public void delete(String device_sn) {
		String hql = "delete from Timer where device_sn = :device_sn";
		this.getSession().createQuery(hql).setString("device_sn", device_sn).executeUpdate();
	}

	@Override
	public Timer findTimerById(int timerId) {
		String hql = "from Timer where id= :id ";
		Timer timer = (Timer) getSession().createQuery(hql).setInteger("id", timerId).setCacheable(Constants.SELECT_CACHE).uniqueResult();
		return timer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Timer> findAllTimer() {
		String hql = "from Timer";
		return this.getSession().createQuery(hql).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Timer> queryTimerByDeviceSn(String device_sn, int from, int pageSize) {
		QueryUtils qutils = new QueryUtils(getSession(), " from Timer");
		Query query = qutils.addStringLike("device_sn", device_sn)
				.setFirstResult(from)
				.setMaxResults(pageSize)
				.getQuery();
		return query.list();
	}

	@Override
	public void updateTimer(Timer timer) {
		// TODO Auto-generated method stub
		this.getSession().merge(timer);
	}

	@Override
	public void delete(String device_sn, int way) {
		String hql = "delete from Timer where device_sn = :device_sn and way = :way";
		this.getSession().createQuery(hql).setString("device_sn", device_sn).setInteger("way", way).executeUpdate();
	}


	//	@Override
//	public Timer findTimerByDeviceSnAndWay(String device_sn, int way) {
//		String hql = "from Timer where device_sn= :device_sn and way = :way";
//		return (List<Timer>) this.getSession().createQuery(hql).setString("device_sn",device_sn)
//				.setInteger("way", way)
//				.uniqueResult();
//
//	}
	@Override
	public List<Timer> findTimerByDeviceSnAndWay(String device_sn, int way) {

		QueryUtils queryUtils = new QueryUtils(getSession(), "from Timer");
		Query query = queryUtils.addString("device_sn", device_sn).addInteger("way", way).getQuery();
		return  (List<Timer>)query.list();
	}
}