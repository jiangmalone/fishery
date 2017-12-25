/**
 * 
 */
package com.geariot.platform.fishery.dao.impl;



import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.PondDao;
import com.geariot.platform.fishery.entities.Pond;
import com.geariot.platform.fishery.model.Equipment;
import com.geariot.platform.fishery.utils.QueryUtils;

/**
 * @author mxy940127
 *
 */

@Repository
public class PondDaoImpl implements PondDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession(){
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public void save(Pond pond) {
		getSession().save(pond);
	}

	@Override
	public boolean checkPondExistByNameAndRelation(String name, String relation) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Pond");
		Query query = queryUtils.addString("name", name)
						.addString("relation", relation)
						.getQuery();
		return !query.list().isEmpty();
	}

	@Override
	public int delete(int pondId) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "delete from Pond");
		Query query = queryUtils.addInteger("id", pondId).getQuery();
		return query.executeUpdate();
	}

	@Override
	public Pond findPondByPondId(int pondId) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Pond");
		Query query = queryUtils.addInteger("id", pondId).getQuery();
		return (Pond) query.uniqueResult();
	}

	@Override
	public void merge(Pond pond) {
		getSession().merge(pond);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pond> queryPondByNameAndRelation(String relation, String name, int from, int pageSize) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Pond");
		Query query = queryUtils.addStringLike("name", name)
						.addString("relation",relation)
						.setFirstResult(from)
						.setMaxResults(pageSize)
						.getQuery();
		return query.list();
	}

	@Override
	public long queryPondByNameAndRelationCount(String relation, String name) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "select count(*) from Pond");
		Query query = queryUtils.addStringLike("name", name)
						.addString("relation",relation)
						.getQuery();
		return (long) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Equipment> findEquipmentByPondId(int pondId, int from, int pageSize) {
		String hql = "select new Equipment("
				+ "a.device_sn , a.name , a.status) "
				+ "from A a where a.pondId = :pondId "
				+ "UNION ALL"
				+ " (select new Equipment("
				+ "b.device_sn , b.name , b.status) "
				+ "from Sensor b where b.pondId = :pondId) "
				+ "UNION ALL "
				+ "(select new Equipment("
				+ "c.device_sn , c.name , c.status) from Controller c "
				+ "where c.pondId = :pondId)";
		System.out.println(hql);
		
		return getSession().createSQLQuery(hql).setInteger("pondId", pondId).setFirstResult(from).setMaxResults(pageSize).list();
	}

}
