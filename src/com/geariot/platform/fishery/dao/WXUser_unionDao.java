package com.geariot.platform.fishery.dao;

import java.util.List;

import com.geariot.platform.fishery.entities.WXUser_union;

public interface WXUser_unionDao {
	void save(WXUser_union wxuser_union);
	WXUser_union getByUnionId(String unionId);
	List<WXUser_union> getByOpenId(String openId);
}
