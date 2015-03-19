package com.jjurm.talentum.fencingmaster.game;

import com.jjurm.talentum.fencingmaster.rgb.RGBFilter;

public interface Signalizable {

	public void signal(RGBFilter filter, int millis);
	public void signal(RGBFilter filter);
	
	public void signalOff();
	
}
