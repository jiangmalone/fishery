package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.fishery.dao.WXUser_unionDao;
import com.geariot.platform.fishery.entities.WXUser_union;
import com.geariot.platform.fishery.utils.Constants;
@Transactional
@Repository
public class WXUser_unionDaoImpl implements WXUser_unionDao {
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void save(WXUser_union wxuser_union) {
		this.getSession().save(wxuser_union);
	}

	@Override
	public WXUser_union getByUnionId(String unionId) {
		String hql = "from WXUser_union where unionId= :unionId ";
		return (WXUser_union) getSession().createQuery(hql).setString("unionId", unionId).setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}
	
	@Override
	public WXUser_union getByOpenId(String openId) {
		String hql = "from WXUser_union where openId= :openId ";
		return (WXUser_union) getSession().createQuery(hql).setString("openId", openId).setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

}
