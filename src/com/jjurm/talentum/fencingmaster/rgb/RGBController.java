package com.jjurm.talentum.fencingmaster.rgb;

import static com.jjurm.talentum.fencingmaster.utils.Utils.sleep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.jjurm.talentum.fencingmaster.enums.Color;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

public class RGBController {

	public static int LED_FREQUENCY = 50;

	@SuppressWarnings("unused")
	private final GpioController gpio;
	private final GpioPinDigitalOutput[][] pins = new GpioPinDigitalOutput[4][3];

	protected static final ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);

	public RGBController(GpioController gpio, Pin[][] pins) {
		this.gpio = gpio;
		for (Side side : Side.values()) {
			for (Color color : Color.values()) {
				this.pins[side.index()][color.index()] = gpio.provisionDigitalOutputPin(pins[side.index()][color
						.index()]);
			}
		}
	}

	public RGBFilter filter() {
		return new DefaultRGBFilter(this);
	}

	public class DefaultRGBFilter implements RGBFilter {

		private RGBController rgb;

		private Side[] side = Side.ALL;
		private Color[] color = Color.ALL;

		private DefaultRGBFilter(RGBController rgb) {
			this.rgb = rgb;
		}

		@Override
		public RGBFilter side(Side... side) {
			this.side = side;
			return this;
		}

		@Override
		public RGBFilter sideI(Side... side) {
			List<Side> skip = Arrays.asList(side);
			List<Side> newList = new ArrayList<Side>();
			for (Side s : Side.values()) {
				if (!skip.contains(s)) {
					newList.add(s);
				}
			}
			this.side = newList.toArray(new Side[0]);
			return this;
		}

		@Override
		public RGBFilter color(Color... color) {
			this.color = color;
			return this;
		}

		@Override
		public RGBFilter colorI(Color... color) {
			List<Color> skip = Arrays.asList(color);
			List<Color> newList = new ArrayList<Color>();
			for (Color c : Color.values()) {
				if (!skip.contains(c)) {
					newList.add(c);
				}
			}
			this.color = newList.toArray(new Color[0]);
			return this;
		}

		void iterate(GpioPinDigitalOutputAction action) {
			for (Side side : side) {
				for (Color color : color) {
					action.action(rgb.pins[side.index()][color.index()]);
				}
			}
		}

		@Override
		public void setState(boolean state) {
			iterate(pin -> pin.setState(state));
		}

		@Override
		public void setState(PinState state) {
			iterate(pin -> pin.setState(state));
		}

		@Override
		public void set(boolean state) {
			setState(state);
		}

		@Override
		public void pulse(long duration, boolean blocking) {
			pulse(duration, PinState.HIGH, blocking);
		}

		@Override
		public void pulse(long duration, PinState pulseState, boolean blocking) {
			iterate(pin -> pin.setState(pulseState));
			PinState inverse = PinState.getInverseState(pulseState);
			if (blocking) {
				sleep(duration);
				iterate(pin -> pin.setState(inverse));
			} else {
				pool.schedule(() -> iterate(pin -> pin.setState(inverse)), duration, TimeUnit.MILLISECONDS);
			}
		}

		@Override
		public void blink(long delay, long duration) {
			iterate(pin -> pin.blink(delay, duration));
		}

		@Override
		public void blink(long delay, long duration, PinState blinkState) {
			iterate(pin -> pin.blink(delay, duration, blinkState));
		}

	}

}
