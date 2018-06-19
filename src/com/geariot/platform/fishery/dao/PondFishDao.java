package com.geariot.platform.fishery.dao;

import java.util.List;
import com.geariot.platform.fishery.entities.PondFish;

public interface PondFishDao {

	int deleteByPondId(int pondId);
	List<PondFish> getFishbyPondId(int pondId);
}
