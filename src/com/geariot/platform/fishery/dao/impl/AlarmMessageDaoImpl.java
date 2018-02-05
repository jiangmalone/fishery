package com.geariot.platform.fishery.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.AlarmMessageDao;
import com.geariot.platform.fishery.entities.AlarmMessage;
import com.geariot.platform.fishery.utils.QueryUtils;
@Repository
public class AlarmMessageDaoImpl implements AlarmMessageDao {
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	@Override
	public void save(AlarmMessage am) {
		// TODO Auto-generated method stub
             getSession().save(am);
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<AlarmMessage> queryAlarmMessageByDeviceSn(String deviceSn) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from AlarmMessage");
		Query query = queryUtils.addString("deviceSn", deviceSn).getQuery();
		return query.list();
	}
	@Override
	public void updateStatus(AlarmMessage am) {
		// TODO Auto-generated method stub
		getSession().merge(am);
	}
	

}
