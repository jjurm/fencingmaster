package com.jjurm.talentum.fencingmaster;

import com.jjurm.talentum.fencingmaster.game.StrategyPatternStateInterface;


public interface StateHolder<T extends StrategyPatternStateInterface> {

	public void switchState(T newState);
	
}
