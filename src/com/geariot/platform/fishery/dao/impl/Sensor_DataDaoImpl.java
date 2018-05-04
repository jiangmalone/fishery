package com.geariot.platform.fishery.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.Sensor_DataDao;
import com.geariot.platform.fishery.entities.Sensor_Data;
import com.geariot.platform.fishery.model.ExcelData;
import com.geariot.platform.fishery.utils.Constants;
import com.geariot.platform.fishery.utils.QueryUtils;

@Repository
public class Sensor_DataDaoImpl implements Sensor_DataDao {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession(){
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public Sensor_Data findDataByDeviceSns(String deviceSns) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Sensor_Data");
		Query query = queryUtils.addString("device_sn", deviceSns).addOrderByDesc("receiveTime").setMaxResults(1).getQuery();
		return (Sensor_Data) query.uniqueResult();
		
	}

	@Override
	public List<ExcelData> getExcelData(String device_sn, Date startTime, Date endTime) {
		List<ExcelData> dataList = new ArrayList<>();
		String hql = "from Sensor_Data ";
		QueryUtils queryUtils = new QueryUtils(getSession(), hql);
		Query query = queryUtils.addString("device_sn", device_sn)
				.addDateInScope("receiveTime", startTime, endTime)
				.addOrderByDesc("receiveTime").getQuery();
				
		@SuppressWarnings("unchecked")
		List<Sensor_Data> list = query.list();   
		if( !list.isEmpty() ){
				for(Sensor_Data obj : list){  
					ExcelData data = new ExcelData();
					data.setSID(String.valueOf(obj.getId()));
					data.setDevice_sn(obj.getDevice_sn());
					data.setOxygen(String.valueOf(obj.getOxygen()));
					data.setpH_value(String.valueOf(obj.getpH_value()));
					data.setReceiveTime(obj.getReceiveTime());
					data.setWater_temperature(String.valueOf(obj.getWater_temperature()));
					dataList.add(data);
				}
			}   
		return dataList;
	}

	@Override
	public void updateData(Sensor_Data sensor_Data) {
		this.getSession().merge(sensor_Data);
	}

	@Override
	public void save(Sensor_Data sensor_Data) {
		this.getSession().save(sensor_Data);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Sensor_Data> today(String device_sn) {
		String hql = "from Sensor_Data where device_sn = :device_sn and date(receiveTime) = curdate() order by receiveTime";
		return getSession().createQuery(hql).setString("device_sn", device_sn).setCacheable(Constants.SELECT_CACHE).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Sensor_Data> sevenData(String device_sn,int day) {
		String hql = "select * from sensor_data where device_sn = :device_sn and DATE_SUB(CURDATE(),INTERVAL day = :day Day) <= date(receiveTime)";
		return getSession().createSQLQuery(hql).setString("device_sn", device_sn).setInteger("day",day).setResultTransformer(Transformers.aliasToBean(Sensor_Data.class)).setCacheable(Constants.SELECT_CACHE).list();
	}

	@Override
	public Sensor_Data findDataByDeviceSnAndWay(String device_sn, int way) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Sensor_Data");
		Query query = queryUtils.addString("device_sn", device_sn).addInteger("way", way)
				.addOrderByDesc("receiveTime").setMaxResults(1).getQuery();
		return (Sensor_Data) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Sensor_Data> today(String device_sn, int way) {
		String hql = "from Sensor_Data where device_sn = :device_sn and way = :way and date(receiveTime) = curdate() order by receiveTime";
		return getSession().createQuery(hql).setString("device_sn", device_sn).setInteger("way", way).setCacheable(Constants.SELECT_CACHE).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Sensor_Data> sevenData(String device_sn, int way,int day) {
		String hql = "select * from sensor_data where device_sn = :device_sn and way = :way and DATE_SUB(CURDATE(),INTERVAL :day Day) <= date(receiveTime)";
		return getSession().createSQLQuery(hql).setString("device_sn", device_sn).setInteger("way", way).setInteger("day",day).setResultTransformer(Transformers.aliasToBean(Sensor_Data.class)).setCacheable(Constants.SELECT_CACHE).list();
	}

}
