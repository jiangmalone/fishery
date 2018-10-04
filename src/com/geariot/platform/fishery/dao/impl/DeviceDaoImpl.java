package com.geariot.platform.fishery.dao.impl;

import com.geariot.platform.fishery.dao.DeviceDao;
import com.geariot.platform.fishery.entities.Device;
import com.geariot.platform.fishery.entities.Equipment;
import com.geariot.platform.fishery.utils.QueryUtils;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DeviceDaoImpl implements DeviceDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }
    @Override
    public void save(Device device) {
        getSession().save(device);
    }

    @Override
    public int delete(String devicesn) {
        QueryUtils queryUtils = new QueryUtils(getSession(), "delete from Device");
        Query query = queryUtils.addString("device_sn", devicesn).getQuery();
        return query.executeUpdate();
    }

    @Override
    public Device findDevice(String deviceSns) {
        QueryUtils queryUtils = new QueryUtils(getSession(), "from Device");
        Query query = queryUtils.addString("device_sn", deviceSns).getQuery();
        return (Device) query.uniqueResult();
    }
	@Override
	public List<Device> getAllDevices() {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Device");
        Query query = queryUtils.addStringLike("device_sn", "%").getQuery();
        return  query.list();
	}
	@Override
	public void updateIsOnline(Device device) {
		this.getSession().merge(device);
	}
	@Override
	public List<Device> queryList(String device_sn,int page, int number) {
		QueryUtils qutils = new QueryUtils(getSession(), "from Device");
		Query query = qutils.addString("device_sn", device_sn)
		.setFirstResult(page)
		.setMaxResults(number)
		.getQuery();
		return query.list();
	}
	/*@Override
	public List<Device> findDevicesByCompanyRelation(String relation, int from, int number) {
		StringBuilder sb = new StringBuilder(2048);
		sb.append("select b.device_sn as device_sn, b.name as name, b.relation as relation, b.pondId as way ");
		sb.append("from Sensor b where b.relation in :relations ");
		sb.append("UNION ALL ");
		sb.append("select c.device_sn as device_sn, c.name as name, c.relation as relation, c.port as way ");
		sb.append("from Controller c where c.relation in :relations ");
		return getSession().createSQLQuery(sb.toString())
				.setResultTransformer(Transformers.aliasToBean(Equipment.class))
				.setParameterList("relation", relation)
				.setFirstResult(from)
				.setMaxResults(number).list();
	}*/
	@Override
	public List<Device> findDevicesByCompanyRelation(String relation, int page, int number) {
		QueryUtils qutils = new QueryUtils(getSession(), "from Device");
		Query query = qutils.addString("companyRelation", relation)
		.setFirstResult(page)
		.setMaxResults(number)
		.getQuery();
		return query.list();
	}
	@Override
	public List<Device> findDevicesByCompanyRelation(String relation) {
		QueryUtils qutils = new QueryUtils(getSession(), "from Device");
		Query query = qutils.addString("companyRelation", relation).getQuery();
		return query.list();
	}
}
