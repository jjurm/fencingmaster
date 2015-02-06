package com.jjurm.talentum.fencingmaster.rgb;

import java.util.concurrent.Future;

import com.pi4j.io.gpio.GpioPinDigitalOutput;

public interface GpioPinThreadedDigitalOutputAction {

	public Future<?> action(GpioPinDigitalOutput pin);
	
}
