package com.geariot.platform.fishery.dao;

import java.util.List;
import com.geariot.platform.fishery.entities.Diagnosing;

public interface DiagDao{

    List<Diagnosing> getDiagnosingByType(int type);
}
