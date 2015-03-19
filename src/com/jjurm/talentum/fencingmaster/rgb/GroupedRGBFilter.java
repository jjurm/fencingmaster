package com.jjurm.talentum.fencingmaster.rgb;

import static com.jjurm.talentum.fencingmaster.utils.Utils.sleep;

import java.util.concurrent.TimeUnit;

import com.jjurm.talentum.fencingmaster.enums.Color;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.pi4j.io.gpio.PinState;


public class GroupedRGBFilter implements RGBFilter {

	private final RGBFilter[] filters; 
	
	public GroupedRGBFilter(RGBFilter... filters) {
		this.filters = filters;
	}
	
	private void iterate(RGBFilterAction action) {
		for (RGBFilter filter : filters) {
			action.action(filter);
		}
	}
	
	@Override
	public RGBFilter side(Side... side) {
		iterate(f -> f.side(side));
		return this;
	}

	@Override
	public RGBFilter sideI(Side... side) {
		iterate(f -> f.sideI(side));
		return this;
	}

	@Override
	public RGBFilter color(Color... color) {
		iterate(f -> f.color(color));
		return this;
	}

	@Override
	public RGBFilter colorI(Color... color) {
		iterate(f -> f.colorI(color));
		return this;
	}

	@Override
	public void setState(boolean state) {
		iterate(f -> f.setState(state));
	}
	
	@Override
	public void setState(PinState state) {
		iterate(f -> f.setState(state));
	}

	@Override
	public void set(boolean state) {
		iterate(f -> f.set(state));
	}

	@Override
	public void pulse(long duration, boolean blocking) {
		pulse(duration, PinState.HIGH, blocking);
	}

	@Override
	public void pulse(long duration, PinState pulseState, boolean blocking) {
		iterate(f -> f.setState(pulseState));
		PinState inverse = PinState.getInverseState(pulseState);
		if (blocking) {
			sleep(duration);
			iterate(pin -> pin.setState(inverse));
		} else {
			RGBController.pool.schedule(() -> iterate(pin -> pin.setState(inverse)), duration, TimeUnit.MILLISECONDS);
		}
	}

	@Override
	public void blink(long delay, long duration) {
		iterate(f -> f.blink(delay, duration));
	}

	@Override
	public void blink(long delay, long duration, PinState blinkState) {
		iterate(f -> f.blink(delay, duration, blinkState));
	}
	
}
