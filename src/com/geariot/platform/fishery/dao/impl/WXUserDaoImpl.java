package com.geariot.platform.fishery.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.WXUserDao;
import com.geariot.platform.fishery.entities.WXUser;
import com.geariot.platform.fishery.utils.Constants;
import com.geariot.platform.fishery.utils.QueryUtils;
@Repository
public class WXUserDaoImpl implements WXUserDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
/*	@Override
	public WXUser findUserByOpenId(String openId) {
		String hql = "from WXUser where openId= :openId ";
		return (WXUser)getSession().createQuery(hql).setString("openId", openId).setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}*/

	@Override
	public WXUser findUserByPhone(String phone) {
		String hql = "from WXUser where phone= :phone ";
		return (WXUser) getSession().createQuery(hql).setString("phone", phone).setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@Override
	public void deleteUser(int WXUserId) {
		String hql = "delete from WXUser where id = :id";
		this.getSession().createQuery(hql).setInteger("id",WXUserId).executeUpdate();
	}

	@Override
	public void updateUser(WXUser oldWXUser) {
		this.getSession().merge(oldWXUser);
	}

	@Override
	public void save(WXUser wxUser) {
		this.getSession().save(wxUser);

	}

	@Override
	public WXUser findUserById(int Id) {
		String hql = "from WXUser where id= :id ";
		WXUser wxuser= (WXUser) getSession().createQuery(hql).setInteger("id", Id).setCacheable(Constants.SELECT_CACHE).uniqueResult();
		return wxuser;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WXUser> queryList(String name, int page, int number) {
		QueryUtils qutils = new QueryUtils(getSession(), "from WXUser");
		Query query = qutils.addStringLike("name", name)
		.setFirstResult(page)
		.setMaxResults(number)
		.getQuery();
		return query.list();
	}

	@Override
	public long getQueryCount(String name) {
		QueryUtils qutils = new QueryUtils(getSession(), "select count(*) from WXUser");
		Query query = qutils.addStringLike("name", name)
		.getQuery();
		return (long) query.uniqueResult();
	}

	@Override
	public void logout(String phone) {
		String sql = "update wxuser set openId='',headimgurl='', login=false where phone= :phone";
		this.getSession().createSQLQuery(sql).setString("phone", phone).setCacheable(Constants.SELECT_CACHE).executeUpdate();
	}

	@Override
	public WXUser findUserByRelation(String relation) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from WXUser");
		Query query = queryUtils.addString("relation", relation).getQuery();
		return (WXUser) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WXUser> wxUsers(String name) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from WXUser");
		Query query = queryUtils.addStringLike("name", name).getQuery();
		return query.list();
	}

	@Override
	public List<WXUser> findUsersByOpenId(String openId) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from WXUser");
		Query query = queryUtils.addString("openId", openId).getQuery();
		return  query.list();
	}



}
