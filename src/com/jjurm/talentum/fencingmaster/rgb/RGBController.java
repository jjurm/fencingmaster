package com.jjurm.talentum.fencingmaster.rgb;

import static com.jjurm.talentum.fencingmaster.utils.Utils.sleep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
	
	protected final ScheduledExecutorService pool;

	public RGBController(GpioController gpio, Pin[][] pins) {
		this.gpio = gpio;
		pool = Executors.newScheduledThreadPool(4);
		for (Side side : Side.values()) {
			for (Color color : Color.values()) {
				this.pins[side.index()][color.index()] = gpio.provisionDigitalOutputPin(pins[side.index()][color
						.index()]);
				//this.pins[side.index()][color.index()] = new EmptyGpio();
			}
		}
	}

	public RGBFilter filter() {
		return new RGBFilter(this);
	}

	public class RGBFilter {

		private RGBController rgb;

		private Side[] side = Side.ALL;
		private Color[] color = Color.ALL;

		private RGBFilter(RGBController rgb) {
			this.rgb = rgb;
		}

		public RGBFilter side(Side... side) {
			this.side = side;
			return this;
		}
		
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

		public RGBFilter color(Color... color) {
			this.color = color;
			return this;
		}
		
		public RGBFilter colorI(Color... color) {
			List<Color> skip = Arrays.asList(color);
			List<Color> newList = new ArrayList<Color>();
			for (Color c: Color.values()) {
				if (!skip.contains(c)) {
					newList.add(c);
				}
			}
			this.color = newList.toArray(new Color[0]);
			return this;
		}

		public GroupedFuture<?> iterateFuture(GpioPinThreadedDigitalOutputAction action) {
			List<Future<?>> futures = new ArrayList<Future<?>>();
			for (Side side : side) {
				for (Color color : color) {
					futures.add(action.action(rgb.pins[side.index()][color.index()]));
				}
			}
			return new GroupedFuture<Object>(futures);
		}
		
		public void iterate(GpioPinDigitalOutputAction action) {
			for (Side side : side) {
				for (Color color : color) {
					action.action(rgb.pins[side.index()][color.index()]);
				}
			}
		}

		public void setState(boolean state) {
			iterateFuture(pin -> {pin.setState(state); return null;});
		}

		public void set(boolean state) {
			setState(state);
		}

		@Deprecated
		public Future<?> pulse(long duration) {
			return pulse(duration, false);
		}

		public Future<?> pulse(long duration, boolean blocking) {
			return pulse(duration, PinState.HIGH, blocking);
		}

		public Future<?> pulse(long duration, PinState pulseState, boolean blocking) {
			/*GroupedFuture<?> f = iterate(pin -> pin.pulse(duration, pulseState, false));
			if (blocking)
				sleep(duration);
			return f;*/
			iterate(pin -> pin.setState(true));
			if (blocking) {
				sleep(duration);
				iterate(pin -> pin.setState(false));
				return new Future<Object>() {
					@Override
					public boolean cancel(boolean mayInterruptIfRunning) {
						return false;
					}
					@Override
					public boolean isCancelled() {
						return false;
					}
					@Override
					public boolean isDone() {
						return true;
					}
					@Override
					public Object get() throws InterruptedException, ExecutionException {
						return null;
					}
					@Override
					public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
							TimeoutException {
						return null;
					}
				};
			} else {
				return pool.schedule(() -> iterate(pin -> pin.setState(false)), duration, TimeUnit.MILLISECONDS);
			}
		}
		
		public Future<?> blink(long delay, long duration) {
			return iterateFuture(pin -> pin.blink(delay, duration));
		}
		
		public Future<?> blink(long delay, long duration, PinState blinkState) {
			return iterateFuture(pin -> pin.blink(delay, duration, blinkState));
		}

	}

}
