/**
 * 
 */
package com.geariot.platform.fishery.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.geariot.platform.fishery.dao.LimitDao;
import com.geariot.platform.fishery.dao.SelfTestDao;
import com.geariot.platform.fishery.entities.Limit_Install;
import com.geariot.platform.fishery.entities.SelfTest;

/**
 * @author 84111
 *
 */
@Service
@Transactional
public class SocketSerivce {

	@Autowired
	private SelfTestDao selfTestDao;
	
	@Autowired
	private LimitDao limitDao;
	
	public int save(SelfTest selfTest) {
		selfTestDao.save(selfTest);
		return 1;
	}
	
	public int save(Limit_Install limit_Install) {
		limitDao.save(limit_Install);
		return 1;
	}
}
