package com.geariot.platform.fishery.dao.impl;

import com.geariot.platform.fishery.dao.DeviceDao;
import com.geariot.platform.fishery.entities.Device;
import com.geariot.platform.fishery.utils.QueryUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
}
