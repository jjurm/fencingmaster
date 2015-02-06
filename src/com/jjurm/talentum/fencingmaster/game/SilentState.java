package com.jjurm.talentum.fencingmaster.game;

import com.jjurm.talentum.fencingmaster.enums.Button;
import com.jjurm.talentum.fencingmaster.enums.State;

public class SilentState extends FMState {

	public SilentState(Game game) {
		super(game);
	}

	@Override
	protected void begin0() {
	}

	@Override
	protected void end0() {
	}

	@Override
	public void terminate() {
	}
	
	@Override
	public void buttonPressed(Button button, State state) {
		switch (button) {
		case BACKBUTTON:
			if (state == State.INTERACTING)
				game.switchState(new Welcome(game));
			break;
		}
	}

}
