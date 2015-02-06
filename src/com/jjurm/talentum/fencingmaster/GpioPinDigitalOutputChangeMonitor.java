package com.jjurm.talentum.fencingmaster;

import static com.jjurm.talentum.fencingmaster.utils.Utils.sleep;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class GpioPinDigitalOutputChangeMonitor implements Runnable {

	protected GpioPinDigitalInput pin;
	protected PinState lastState;
	protected List<GpioPinListenerDigital> listeners = new ArrayList<GpioPinListenerDigital>();
	
	private Thread thread;
	protected final AtomicBoolean run = new AtomicBoolean(false);
	
	public GpioPinDigitalOutputChangeMonitor(GpioPinDigitalInput pin) {
		this.pin = pin;
		thread = new Thread(this);
		lastState = pin.getState();
	}
	
	public void addListener(GpioPinListenerDigital listener) {
		listeners.add(listener);
	}
	
	public void removeListener(GpioPinListenerDigital listener) {
		listeners.remove(listener);
	}

	public synchronized void start() {
		run.set(true);
		thread.start();
	}
	
	public synchronized void stop() {
		run.set(false);
	}
	
	@Override
	public synchronized void run() {
		while (run.get()) {
			PinState state = pin.getState();
			if (state != lastState) {
				GpioPinDigitalStateChangeEvent event = new GpioPinDigitalStateChangeEvent(null, pin, state);
				for (GpioPinListenerDigital listener : listeners) {
					listener.handleGpioPinDigitalStateChangeEvent(event);
				}
			}
			sleep(2);
		}
	}
	
}
