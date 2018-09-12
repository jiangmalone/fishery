package com.geariot.platform.fishery.dao.impl;

import com.geariot.platform.fishery.entities.Dev_Trigger;
import com.geariot.platform.fishery.dao.Dev_TriggerDao;
import com.geariot.platform.fishery.entities.Sensor_Controller;
import com.geariot.platform.fishery.utils.QueryUtils;

import cmcc.iot.onenet.javasdk.api.bindata.AddBindataApi;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class Dev_TriggerDaoImpl implements Dev_TriggerDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }
    @Override
    public void save(Dev_Trigger trigger) {
        getSession().save(trigger);

    }
    @Override
    public int deleteByTriggerId(String triggerid){
    	System.out.println("删除触发器");
        QueryUtils queryUtils = new QueryUtils(getSession(), "delete from Dev_Trigger");
        Query query = queryUtils.addString("triger_id", triggerid).getQuery();
        return query.executeUpdate();

    }

    @Override
    public int delete(String deviceSns) {
        QueryUtils queryUtils = new QueryUtils(getSession(), "delete from Dev_Trigger");
        Query query = queryUtils.addString("device_sn", deviceSns).getQuery();
        return query.executeUpdate();
    }



    @Override
    public Dev_Trigger findTriggerBytriggerId(int triggerId){
        QueryUtils queryUtils = new QueryUtils(getSession(), "from Dev_Trigger");
        Query query = queryUtils.addInteger("triger_id", triggerId).getQuery();
        return (Dev_Trigger) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Dev_Trigger> findDev_TriggerBydevsn(String deviceSns) {
        QueryUtils queryUtils = new QueryUtils(getSession(), "from Dev_Trigger");
        Query query = queryUtils.addString("device_sn", deviceSns).getQuery();
        return query.list();
    }
	@Override
	public List<Dev_Trigger> findDev_TriggerByDevsnAndWay(String deviceSns, int way) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Dev_Trigger");
        Query query = queryUtils.addString("device_sn", deviceSns).addInteger("way", way).getQuery();
        return (List<Dev_Trigger>) query.list();
	}
	/* (non-Javadoc)
	 * @see com.geariot.platform.fishery.dao.Dev_TriggerDao#findAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Dev_Trigger> findAll() {
		String hql = "from Dev_Trigger";
		return this.getSession().createQuery(hql).list();
	}


}
