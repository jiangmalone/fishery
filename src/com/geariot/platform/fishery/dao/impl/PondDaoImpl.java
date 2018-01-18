/**
 * 
 */
package com.geariot.platform.fishery.dao.impl;



import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.PondDao;
import com.geariot.platform.fishery.entities.Company;
import com.geariot.platform.fishery.entities.Fish_Category;
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
		StringBuilder sb = new StringBuilder(2048);
		sb.append("select a.device_sn as device_sn, a.name as name, a.status as status ");
		sb.append("from AIO a where a.pondId = :pondId ");
		sb.append("UNION ALL ");
		sb.append("select b.device_sn as device_sn, b.name as name, b.status as status ");
		sb.append("from Sensor b where b.pondId = :pondId ");
		sb.append("UNION ALL ");
		sb.append("select c.device_sn as device_sn, c.name as name, c.status as status ");
		sb.append("from Controller c where c.pondId = :pondId");
		return getSession().createSQLQuery(sb.toString())
				.setResultTransformer(Transformers.aliasToBean(Equipment.class))
				.setInteger("pondId", pondId).setFirstResult(from)
				.setMaxResults(pageSize).list();
	}

	@Override
	public long equipmentByPondIdCount(int pondId) {
		StringBuilder sb = new StringBuilder(2048);
		sb.append("select count(*) from (");
		sb.append("select a.device_sn as device_sn, a.name as name, a.status as status ");
		sb.append("from AIO a where a.pondId = :pondId ");
		sb.append("UNION ALL ");
		sb.append("select b.device_sn as device_sn, b.name as name, b.status as status ");
		sb.append("from Sensor b where b.pondId = :pondId ");
		sb.append("UNION ALL ");
		sb.append("select c.device_sn as device_sn, c.name as name, c.status as status ");
		sb.append("from Controller c where c.pondId = :pondId) ");
		sb.append("as total");
		System.out.println(sb.toString());
		BigInteger big =  (BigInteger) getSession().createSQLQuery(sb.toString())
				.setInteger("pondId", pondId).uniqueResult();
		return big.longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Fish_Category> list() {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Fish_Category");
		Query query = queryUtils.getQuery();
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pond> queryPondByNameAndRelation(String relation, String name) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Pond");
		Query query = queryUtils.addStringLike("name", name)
						.addString("relation",relation)
						.getQuery();
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Equipment> adminFindEquipmentAll(int from, int pageSize) {
		StringBuilder sb = new StringBuilder(2048);
		sb.append("select a.device_sn as device_sn, a.name as name, a.status as status ");
		sb.append("from AIO a ");
		sb.append("UNION ALL ");
		sb.append("select b.device_sn as device_sn, b.name as name, b.status as status ");
		sb.append("from Sensor b ");
		sb.append("UNION ALL ");
		sb.append("select c.device_sn as device_sn, c.name as name, c.status as status ");
		sb.append("from Controller c");
		return getSession().createSQLQuery(sb.toString())
				.setResultTransformer(Transformers.aliasToBean(Equipment.class))
				.setFirstResult(from)
				.setMaxResults(pageSize).list();
	}

	@Override
	public long adminFindEquipmentCountAll() {
		StringBuilder sb = new StringBuilder(2048);
		sb.append("select count(*) from (");
		sb.append("select a.device_sn as device_sn, a.name as name, a.status as status ");
		sb.append("from AIO a ");
		sb.append("UNION ALL ");
		sb.append("select b.device_sn as device_sn, b.name as name, b.status as status ");
		sb.append("from Sensor b ");
		sb.append("UNION ALL ");
		sb.append("select c.device_sn as device_sn, c.name as name, c.status as status ");
		sb.append("from Controller c) ");
		sb.append("as total");
		BigInteger big =  (BigInteger) getSession().createSQLQuery(sb.toString()).uniqueResult();
		return big.longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Equipment> adminFindEquipmentByCo(List<Company> companies, int from, int pageSize) {
		List<String> strings = new ArrayList<>();
		for(Company company : companies){
			strings.add(company.getRelationId());
		}
		StringBuilder sb = new StringBuilder(2048);
		sb.append("select a.device_sn as device_sn, a.name as name, a.status as status ");
		sb.append("from AIO a where relationId in :relationIds ");
		sb.append("UNION ALL ");
		sb.append("select b.device_sn as device_sn, b.name as name, b.status as status ");
		sb.append("from Sensor b where relationId in :relationIds ");
		sb.append("UNION ALL ");
		sb.append("select c.device_sn as device_sn, c.name as name, c.status as status ");
		sb.append("from Controller c where relationId in :relationIds ");
		return getSession().createSQLQuery(sb.toString())
				.setResultTransformer(Transformers.aliasToBean(Equipment.class))
				.setParameterList("relationIds", strings)
				.setFirstResult(from)
				.setMaxResults(pageSize).list();
	}

	@Override
	public long adminFindEquipmentCountCo(List<Company> companies) {
		List<String> strings = new ArrayList<>();
		for(Company company : companies){
			strings.add(company.getRelationId());
		}
		StringBuilder sb = new StringBuilder(2048);
		sb.append("select count(*) from (");
		sb.append("select a.device_sn as device_sn, a.name as name, a.status as status ");
		sb.append("from AIO a where relationId in :relationIds ");
		sb.append("UNION ALL ");
		sb.append("select b.device_sn as device_sn, b.name as name, b.status as status ");
		sb.append("from Sensor b where relationId in :relationIds ");
		sb.append("UNION ALL ");
		sb.append("select c.device_sn as device_sn, c.name as name, c.status as status ");
		sb.append("from Controller c where relationId in :relationIds) ");
		sb.append("as total");
		BigInteger big =  (BigInteger) getSession().createSQLQuery(sb.toString()).setParameterList("relationIds", strings).uniqueResult();
		return big.longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Equipment> adminFindEquipmentBySn(String device_sn) {
		StringBuilder sb = new StringBuilder(2048);
		sb.append("select a.device_sn as device_sn, a.name as name, a.status as status ");
		sb.append("from AIO a where device_sn like :device_sn ");
		sb.append("UNION ALL ");
		sb.append("select b.device_sn as device_sn, b.name as name, b.status as status ");
		sb.append("from Sensor b where device_sn like :device_sn ");
		sb.append("UNION ALL ");
		sb.append("select c.device_sn as device_sn, c.name as name, c.status as status ");
		sb.append("from Controller c where device_sn like :device_sn ");
		return getSession().createSQLQuery(sb.toString()).setResultTransformer(Transformers.aliasToBean(Equipment.class))
				.setString("device_sn", "%"+device_sn+"%").list();
	}

	@Override
	public long adminFindEquipmentCountSn(String device_sn) {
		StringBuilder sb = new StringBuilder(2048);
		sb.append("select count(*) from (");
		sb.append("select a.device_sn as device_sn, a.name as name, a.status as status ");
		sb.append("from AIO a where device_sn like :device_sn ");
		sb.append("UNION ALL ");
		sb.append("select b.device_sn as device_sn, b.name as name, b.status as status ");
		sb.append("from Sensor b where device_sn like :device_sn ");
		sb.append("UNION ALL ");
		sb.append("select c.device_sn as device_sn, c.name as name, c.status as status ");
		sb.append("from Controller c where device_sn like :device_sn) ");
		sb.append("as total");
		BigInteger big =  (BigInteger) getSession().createSQLQuery(sb.toString()).setString("device_sn", "%"+device_sn+"%").uniqueResult();
		return big.longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Equipment> adminFindEquipmentDouble(String device_sn, List<Company> companies, int from, int pageSize) {
		List<String> strings = new ArrayList<>();
		for(Company company : companies){
			strings.add(company.getRelationId());
		}
		StringBuilder sb = new StringBuilder(2048);
		sb.append("select a.device_sn as device_sn, a.name as name, a.status as status ");
		sb.append("from AIO a where device_sn like :device_sn and relationId in :relationIds ");
		sb.append("UNION ALL ");
		sb.append("select b.device_sn as device_sn, b.name as name, b.status as status ");
		sb.append("from Sensor b where device_sn like :device_sn and relationId in :relationIds ");
		sb.append("UNION ALL ");
		sb.append("select c.device_sn as device_sn, c.name as name, c.status as status ");
		sb.append("from Controller c where device_sn like :device_sn and relationId in :relationIds ");
		return getSession().createSQLQuery(sb.toString())
				.setResultTransformer(Transformers.aliasToBean(Equipment.class))
				.setString("device_sn", "%"+device_sn+"%")
				.setParameterList("relationIds", strings)
				.setFirstResult(from)
				.setMaxResults(pageSize)
				.list();
	}

	@Override
	public long adminFindEquipmentCountDouble(String device_sn, List<Company> companies) {
		List<String> strings = new ArrayList<>();
		for(Company company : companies){
			strings.add(company.getRelationId());
		}
		StringBuilder sb = new StringBuilder(2048);
		sb.append("select count(*) from (");
		sb.append("select a.device_sn as device_sn, a.name as name, a.status as status ");
		sb.append("from AIO a where device_sn like :device_sn and relationId in :relationIds ");
		sb.append("UNION ALL ");
		sb.append("select b.device_sn as device_sn, b.name as name, b.status as status ");
		sb.append("from Sensor b where device_sn like :device_sn and relationId in :relationIds ");
		sb.append("UNION ALL ");
		sb.append("select c.device_sn as device_sn, c.name as name, c.status as status ");
		sb.append("from Controller c where device_sn like :device_sn and relationId in :relationIds) ");
		sb.append("as total");
		BigInteger big =  (BigInteger) getSession().createSQLQuery(sb.toString())
				.setString("device_sn", "%"+device_sn+"%")
				.setParameterList("relationIds", strings)
				.uniqueResult();
		return big.longValue();
	}

}
