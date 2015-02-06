package com.jjurm.talentum.fencingmaster.game;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import com.jjurm.talentum.fencingmaster.GameEventsHandler;
import com.jjurm.talentum.fencingmaster.StateHolder;
import com.jjurm.talentum.fencingmaster.enums.Button;
import com.jjurm.talentum.fencingmaster.enums.Foot;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.enums.State;
import com.jjurm.talentum.fencingmaster.rgb.RGBController;
import com.sun.net.httpserver.HttpExchange;

public class Game implements GameEventsHandler, StateHolder<FMState> {

	protected final RGBController rgb;

	protected FMState fmState;
	
	protected ScheduledExecutorService pool;
	
	protected Future<?> shutdownFuture;

	public Game(RGBController rgb) {
		this.rgb = rgb;
		
		pool = Executors.newScheduledThreadPool(2);
	}

	public void shutdown() {
		if (fmState != null)
			fmState.terminate();
		pool.shutdown();
	}

	@Override
	public void switchState(FMState newState) {
		FMState oldState = fmState;

		if (oldState != null)
			oldState.end();
		fmState = newState;
		System.out.println("Game: "+String.valueOf(oldState)+" -> "+String.valueOf(newState));
		if (newState != null)
			newState.begin();
	}

	@Override
	public void plateTouched(Side side) {
 		System.out.println("Plate touched: "+side.toString());
		if (fmState != null)
			pool.execute(() -> fmState.plateTouched(side));
	}

	@Override
	public void buttonPressed(Button button, State state) {
		System.out.println("Button: "+button+", "+state);
		if (button == Button.BACKBUTTON && state == State.NON_INTERACTING) {
			if (shutdownFuture != null) {
				shutdownFuture.cancel(false);
			}
		}
		if (fmState != null)
			pool.execute(() -> fmState.buttonPressed(button, state));
	}

	@Override
	public void footChanged(Foot foot, State state) {
		System.out.println("Foot: "+foot+", "+state);
		if (fmState != null)
			pool.execute(() -> fmState.footChanged(foot, state));
	}

	@Override
	public String webRequest(HttpExchange e) {
		if (fmState != null)
			return fmState.webRequest(e);
		else
			return webRequestDefault(e);
	}

	public String webRequestDefault(HttpExchange e) {
		return "Path: " + e.getRequestURI().getPath();
	}

}
