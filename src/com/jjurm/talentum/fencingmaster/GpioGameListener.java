package com.jjurm.talentum.fencingmaster;

import com.jjurm.talentum.fencingmaster.enums.UserAction;
import com.jjurm.talentum.fencingmaster.enums.Foot;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.enums.State;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public abstract class GpioGameListener implements GpioPinListenerDigital {

	protected GameEventsHandler handler;
	
	public GpioGameListener(GameEventsHandler handler) {
		this.handler = handler;
	}
	
	public static class UserActionListener extends GpioGameListener {

		protected UserAction userAction;
		
		public UserActionListener(GameEventsHandler handler, UserAction userAction) {
			super(handler);
			this.userAction = userAction;
		}

		@Override
		public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
			State state = event.getState().isLow() ? State.INTERACTING : State.NON_INTERACTING;
			handler.userAction(userAction, state);
		}
		
	}
	
	public static class PlateListener extends GpioGameListener {

		protected Side side;
		
		public PlateListener(GameEventsHandler handler, Side side) {
			super(handler);
			this.side = side;
		}

		@Override
		public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
			if (event.getState().isLow()) {
				handler.plateTouched(side);
			}
		}
		
	}
	
	public static class FootListener extends GpioGameListener {
		
		protected Foot foot;
		
		public FootListener(GameEventsHandler handler, Foot foot) {
			super(handler);
			this.foot = foot;
		}

		@Override
		public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
			State state = event.getState().isLow() ? State.INTERACTING : State.NON_INTERACTING;
			handler.footChanged(foot, state);
		}
		
	}

}
