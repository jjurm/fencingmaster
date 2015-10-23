package com.jjurm.talentum.fencingmaster;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinShutdown;
import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.trigger.GpioTrigger;

public class GpioPinDebounceWrapper implements GpioPinDigitalInput
{
	
	public static final int DEBOUNCE_TIME = 1000;

	protected GpioPinDigitalInput pin;
	
	protected long lastTime = 0;
	protected PinState lastState;
	protected final Object lastTimeMonitor = new Object();
	
	public GpioPinDebounceWrapper(GpioPinDigitalInput pin) {
		this.pin = pin;
	}

	@Override
	public int hashCode() {
		return pin.hashCode();
	}

	@Override
	public Pin getPin() {
		return pin.getPin();
	}

	@Override
	public GpioProvider getProvider() {
		return pin.getProvider();
	}

	@Override
	public void setName(String name) {
		pin.setName(name);
	}

	@Override
	public String getName() {
		return pin.getName();
	}

	@Override
	public void setTag(Object tag) {
		pin.setTag(tag);
	}

	@Override
	public Object getTag() {
		return pin.getTag();
	}

	@Override
	public void setProperty(String key, String value) {
		pin.setProperty(key, value);
	}

	@Override
	public boolean hasProperty(String key) {
		return pin.hasProperty(key);
	}

	@Override
	public boolean equals(Object obj) {
		return pin.equals(obj);
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		return pin.getProperty(key, defaultValue);
	}

	@Override
	public String getProperty(String key) {
		return pin.getProperty(key);
	}

	@Override
	public Map<String, String> getProperties() {
		return pin.getProperties();
	}

	@Override
	public void removeProperty(String key) {
		pin.removeProperty(key);
	}

	@Override
	public void clearProperties() {
		pin.clearProperties();
	}

	@Override
	public void export(PinMode mode) {
		pin.export(mode);
	}

	@Override
	public void unexport() {
		pin.unexport();
	}

	@Override
	public boolean isExported() {
		return pin.isExported();
	}

	@Override
	public void setMode(PinMode mode) {
		pin.setMode(mode);
	}

	@Override
	public PinMode getMode() {
		return pin.getMode();
	}

	@Override
	public boolean isMode(PinMode mode) {
		return pin.isMode(mode);
	}

	@Override
	public void setPullResistance(PinPullResistance resistance) {
		pin.setPullResistance(resistance);
	}

	@Override
	public PinPullResistance getPullResistance() {
		return pin.getPullResistance();
	}

	@Override
	public boolean isPullResistance(PinPullResistance resistance) {
		return pin.isPullResistance(resistance);
	}

	@Override
	public boolean isHigh() {
		return (getState() == PinState.HIGH);
	}

	@Override
	public boolean isLow() {
		return (getState() == PinState.LOW);
	}

	@Override
	public PinState getState() {
		// override
		synchronized (lastTimeMonitor) {
			long time = System.currentTimeMillis();
			if (time < lastTime + DEBOUNCE_TIME) { // in debounce period
				return lastState;
			} else { // after debounce
				lastState = pin.getState();
				lastTime = time;
				return lastState;
			}
		}
	}

	@Override
	public boolean isState(PinState state) {
		return (getState() == state);
	}

	@Override
	public boolean hasDebounce(PinState state) {
		return pin.hasDebounce(state);
	}

	@Override
	public int getDebounce(PinState state) {
		return pin.getDebounce(state);
	}

	@Override
	public void setDebounce(int debounce, PinState... state) {
		pin.setDebounce(debounce, state);
	}

	@Override
	public void setDebounce(int debounce) {
		pin.setDebounce(debounce);
	}

	@Override
	public void addListener(GpioPinListener... listener) {
		pin.addListener(listener);
	}

	@Override
	public void addListener(List<? extends GpioPinListener> listeners) {
		pin.addListener(listeners);
	}

	@Override
	public Collection<GpioPinListener> getListeners() {
		return pin.getListeners();
	}

	@Override
	public boolean hasListener(GpioPinListener... listener) {
		return pin.hasListener(listener);
	}

	@Override
	public void removeListener(GpioPinListener... listener) {
		pin.removeListener(listener);
	}

	@Override
	public void removeListener(List<? extends GpioPinListener> listeners) {
		pin.removeListener(listeners);
	}

	@Override
	public void removeAllListeners() {
		pin.removeAllListeners();
	}

	@Override
	public Collection<GpioTrigger> getTriggers() {
		return pin.getTriggers();
	}

	@Override
	public void addTrigger(GpioTrigger... trigger) {
		pin.addTrigger(trigger);
	}

	@Override
	public void addTrigger(List<? extends GpioTrigger> triggers) {
		pin.addTrigger(triggers);
	}

	@Override
	public void removeTrigger(GpioTrigger... trigger) {
		pin.removeTrigger(trigger);
	}

	@Override
	public void removeTrigger(List<? extends GpioTrigger> triggers) {
		pin.removeTrigger(triggers);
	}

	@Override
	public void removeAllTriggers() {
		pin.removeAllTriggers();
	}

	@Override
	public String toString() {
		return pin.toString();
	}

	@Override
	public GpioPinShutdown getShutdownOptions() {
		return pin.getShutdownOptions();
	}

	@Override
	public void setShutdownOptions(GpioPinShutdown options) {
		pin.setShutdownOptions(options);
	}

	@Override
	public void setShutdownOptions(Boolean unexport) {
		pin.setShutdownOptions(unexport);
	}

	@Override
	public void setShutdownOptions(Boolean unexport, PinState state) {
		pin.setShutdownOptions(unexport, state);
	}

	@Override
	public void setShutdownOptions(Boolean unexport, PinState state, PinPullResistance resistance) {
		pin.setShutdownOptions(unexport, state, resistance);
	}

	@Override
	public void setShutdownOptions(Boolean unexport, PinState state, PinPullResistance resistance, PinMode mode) {
		pin.setShutdownOptions(unexport, state, resistance, mode);
	}

	@Override
	public void export(PinMode mode, PinState defaultState) {
		pin.export(mode, defaultState);
	}

}
