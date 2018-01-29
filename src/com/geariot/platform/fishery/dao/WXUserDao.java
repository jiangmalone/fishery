package com.geariot.platform.fishery.dao;

import java.util.List;

import com.geariot.platform.fishery.entities.WXUser;

public interface WXUserDao {
	
	WXUser findUserByOpenId(String openId);
	
	WXUser findUserById(int Id);

	WXUser findUserByPhone(String phone);
	
	WXUser findUserByRelation(String relation);

	void deleteUser(int WXUserId);

	void updateUser(WXUser oldWXUser);

	void save(WXUser wxUser);

	List<WXUser> queryList(String name, int page, int number);

	long getQueryCount(String name);

	void logout(String phone);
}
