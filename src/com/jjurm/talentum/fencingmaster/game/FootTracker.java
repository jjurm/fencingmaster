package com.jjurm.talentum.fencingmaster.game;

import java.util.HashMap;
import java.util.Map;

import com.jjurm.talentum.fencingmaster.enums.Condition;
import com.jjurm.talentum.fencingmaster.enums.Foot;
import com.jjurm.talentum.fencingmaster.enums.State;

public class FootTracker {

	protected Map<Foot, State> states = new HashMap<Foot, State>(2, 1);
	
	protected Foot[] lastFeet = new Foot[2];
	protected State[] lastFeetState = new State[2];
	
	public FootTracker() {
		reset();
	}
	
	public void reset() {
		lastFeet = new Foot[2];
		lastFeetState = new State[2];
	}
	
	public boolean footOk() {
		return lastFeet[0] == Foot.BACK && lastFeet[1] == Foot.FRONT && lastFeetState[0] == State.NON_INTERACTING
				&& lastFeetState[1] == State.NON_INTERACTING;
	}

	public Condition getFootCondition() {
		return footOk() ? Condition.CORRECT : Condition.INCORRECT;
	}
	
	public void footChanged(Foot foot, State state) {
		states.put(foot, state);
		
		lastFeet[1] = lastFeet[0];
		lastFeetState[1] = lastFeetState[0];
		lastFeet[0] = foot;
		lastFeetState[0] = state;
	}
	
	public State getState(Foot foot) {
		return states.getOrDefault(foot, State.DEFAULT);
	}
	
}
