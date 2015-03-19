package com.jjurm.talentum.fencingmaster.game;

import java.util.concurrent.atomic.AtomicBoolean;

import com.jjurm.talentum.fencingmaster.GameEventsHandler;
import com.jjurm.talentum.fencingmaster.enums.Foot;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.enums.State;
import com.jjurm.talentum.fencingmaster.enums.UserAction;
import com.jjurm.talentum.fencingmaster.rgb.RGBController;
import com.sun.net.httpserver.HttpExchange;

public abstract class FMState implements GameEventsHandler, StrategyPatternStateInterface {

	protected final Game game;
	protected final RGBController rgb;
	
	protected final AtomicBoolean reacting = new AtomicBoolean(true);
	
	public FMState(Game game) {
		this.game = game;
		this.rgb = game.getRGB();
		created();
	}
	
	protected void created() {};
	
	@Override
	public final void begin() {
		begin0();
		reacting.set(true);
	}
	@Override
	public final void end() {
		reacting.set(false);
		end0();
	}
	
	protected abstract void begin0();
	protected abstract void end0();
	
	@Override
	public void userAction(UserAction userAction, State state) {};
	
	@Override
	public final void plateTouched(Side side) {
		if (reacting.compareAndSet(true, false)) {
			plateTouched0(side);
			reacting.set(true);
		}
		//plateTouched0(side);
	}
	
	@Override
	public final void footChanged(Foot foot, State state) {
		if (reacting.compareAndSet(true, false)) {
			footChanged0(foot, state);
			reacting.set(true);
		}
		//footChanged0(foot, state);
	}
	
	protected void plateTouched0(Side side) {};
	protected void footChanged0(Foot foot, State state) {};
	
	@Override
	public byte[] webRequest(HttpExchange e) {
		return Game.webRequestDefault(e);
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
	
}
