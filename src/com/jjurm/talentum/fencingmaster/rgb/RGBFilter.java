package com.jjurm.talentum.fencingmaster.rgb;

import com.jjurm.talentum.fencingmaster.enums.Color;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.pi4j.io.gpio.PinState;

public interface RGBFilter {
	
	public RGBFilter side(Side... side);
	public RGBFilter sideI(Side... side);
	public RGBFilter color(Color... color);
	public RGBFilter colorI(Color... color);
	
	public void setState(boolean state);
	public void setState(PinState state);
	public void set(boolean state);
	
	public void pulse(long duration, boolean blocking);
	public void pulse(long duration, PinState pulseState, boolean blocking);
	
	public void blink(long delay, long duration);
	public void blink(long delay, long duration, PinState blinkState);
	
}
