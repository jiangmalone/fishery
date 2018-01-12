/**
 * 
 */
package com.geariot.platform.fishery.utils;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.geariot.platform.fishery.service.PondService;

/**
 * @author mxy940127
 *
 */
@Component
public class InitFishCates {

	private static final Logger log = LogManager.getLogger(InitFishCates.class);
	
	@Autowired
	private PondService pondService;
	
	@PostConstruct
	public void initFishCates(){
		if(Constants.RELOAD_FISH){
			log.debug("重新读取鱼种类列表");
			pondService.initFishCate();
			log.debug("鱼种类数据读取完毕");
		}else{
			return;
		}
	}
}
