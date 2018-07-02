package com.geariot.platform.fishery.dao.impl;

import java.util.List;

import com.geariot.platform.fishery.entities.PondFish;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.DiagDao;
import com.geariot.platform.fishery.entities.Diagnosing;
import com.geariot.platform.fishery.utils.QueryUtils;

@Repository
public class DiagDaoImpl implements DiagDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }


    @Override
    public List<Diagnosing> getDiagnosingByType(int type) {
        QueryUtils queryUtils = new QueryUtils(getSession(), "from Dianosing");
        Query query = queryUtils.addInteger("type", type)
                .getQuery();
        return query.list();
    }

}
