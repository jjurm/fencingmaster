package com.jjurm.talentum.fencingmaster.game;

import com.jjurm.talentum.fencingmaster.enums.Foot;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.enums.State;

public abstract class GameState implements StrategyPatternStateInterface {

	public abstract void plateTouched(Side side);
	public abstract void footChanged(Foot foot, State state);
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
	
}
