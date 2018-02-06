package com.geariot.platform.fishery.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.DataAlarmDao;
import com.geariot.platform.fishery.entities.Company;
import com.geariot.platform.fishery.entities.DataAlarm;
import com.geariot.platform.fishery.entities.Pond;
import com.geariot.platform.fishery.utils.QueryUtils;
@Repository
public class DataAlarmDaoImpl implements DataAlarmDao {
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	@Override
	public void save(DataAlarm dataAlarm) {
		// TODO Auto-generated method stub
          getSession().save(dataAlarm);
	}
	@Override
	public DataAlarm findDataAlarmById(int id) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from DataAlarm");
		Query query = queryUtils.addInteger("id", id).getQuery();
		return (DataAlarm) query.uniqueResult();
	}
	@Override
	public DataAlarm findDataAlarmByRelation(String relation) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from DataAlarm");
		Query query = queryUtils.addString("relation", relation).getQuery();
		return (DataAlarm) query.uniqueResult();
	}
	@Override
	public void updateStatus(DataAlarm da) {
		// TODO Auto-generated method stub
		getSession().merge(da);
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<DataAlarm> queryDataAlarm(String relation) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from DataAlarm");
		Query query = queryUtils.addString("relation", relation)
						.addInteger("isWatch",0)
						.getQuery();
		return query.list();
	}


}
