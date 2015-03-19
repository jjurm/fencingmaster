package com.jjurm.talentum.fencingmaster.game;

import com.jjurm.talentum.fencingmaster.enums.Foot;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.enums.State;

public class WaitingForUserActionGameState extends GameState {

	Signalizable signalizable;
	Runnable action;

	FootTracker footTracker = new FootTracker();

	public WaitingForUserActionGameState(Signalizable signalizable, Runnable action) {
		this.signalizable = signalizable;
		this.action = action;
	}

	@Override
	public void begin() {
	}

	@Override
	public void end() {
		if (signalizable != null)
			signalizable.signalOff();
	}

	@Override
	public void plateTouched(Side side) {
	}

	@Override
	public void footChanged(Foot foot, State state) {
		footTracker.footChanged(foot, state);
		if (footTracker.getState(Foot.FRONT) == State.INTERACTING
				&& footTracker.getState(Foot.BACK) == State.INTERACTING) {
			if (action != null) {
				action.run();
				action = null;
			}
		}
	}

}
