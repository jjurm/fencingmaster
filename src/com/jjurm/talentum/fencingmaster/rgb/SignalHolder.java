package com.jjurm.talentum.fencingmaster.rgb;

public class SignalHolder {

	RGBFilter signal;
	
	public void update(RGBFilter filter) {
		signal = filter;
		signal.set(true);
	}
	
	public void off() {
		if (signal != null) {
			signal.set(false);
			signal = null;
		}
	}
	
}
