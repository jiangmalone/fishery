package com.geariot.platform.fishery.dao;

import java.util.List;

import com.geariot.platform.fishery.entities.Limit_Install;

public interface LimitDao {
	void save(Limit_Install limit_Install);
	
	void delete(String device_sn);

	Limit_Install findLimitById(int limitId);
	
	Limit_Install findLimitByDeviceSns(String device_sn);

	List<Limit_Install> queryLimitByDeviceSn(String device_sn, int from, int pageSize);

	void updateLimit(Limit_Install limit_Install);

	Limit_Install findLimitByDeviceSnsAndWay(String device_sn, int way);
}
