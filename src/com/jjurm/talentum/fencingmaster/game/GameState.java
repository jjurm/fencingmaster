package com.jjurm.talentum.fencingmaster.game;

import java.util.concurrent.atomic.AtomicBoolean;

import com.jjurm.talentum.fencingmaster.enums.Foot;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.enums.State;

public abstract class GameState implements StrategyPatternStateInterface {

	protected final AtomicBoolean reacting = new AtomicBoolean(true);
	
	public final void plateTouched(Side side) {
		if (reacting.get())
			plateTouched0(side);
	}
	
	public final void footChanged(Foot foot, State state) {
		if (reacting.get())
			footChanged0(foot, state);
	}
	
	protected abstract void plateTouched0(Side side);
	protected abstract void footChanged0(Foot foot, State state);
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
	
}
