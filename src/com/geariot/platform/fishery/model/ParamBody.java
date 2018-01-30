/**
 * 
 */
package com.geariot.platform.fishery.model;

import com.geariot.platform.fishery.entities.Limit_Install;
import com.geariot.platform.fishery.entities.Timer;

/**
 * @author mxy940127
 *
 */
public class ParamBody {
	
	private Limit_Install limit_Install;
	private Timer[] timers;
	
	public Limit_Install getLimit_Install() {
		return limit_Install;
	}
	public void setLimit_Install(Limit_Install limit_Install) {
		this.limit_Install = limit_Install;
	}
	
	public Timer[] getTimers() {
		return timers;
	}
	public void setTimers(Timer[] timers) {
		this.timers = timers;
	}
}
