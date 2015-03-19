package com.jjurm.talentum.fencingmaster.game.fmstate;

import static com.jjurm.talentum.fencingmaster.enums.Color.GREEN;
import static com.jjurm.talentum.fencingmaster.enums.Color.RED;
import static com.jjurm.talentum.fencingmaster.enums.Side.LEFT;
import static com.jjurm.talentum.fencingmaster.enums.Side.RIGHT;

import com.jjurm.talentum.fencingmaster.audio.Sound;
import com.jjurm.talentum.fencingmaster.enums.UserAction;
import com.jjurm.talentum.fencingmaster.enums.State;
import com.jjurm.talentum.fencingmaster.game.FMState;
import com.jjurm.talentum.fencingmaster.game.Game;

public class SilentState extends FMState {

	public SilentState(Game game) {
		super(game);
	}

	@Override
	protected void begin0() {
		Sound.SHUTDOWN.play();
		if (game.isPresentation()) {
			rgb.filter().color(RED, GREEN).side(RIGHT, LEFT).pulse(300, false);
		}
	}

	@Override
	protected void end0() {
	}
	
	@Override
	public void userAction(UserAction userAction, State state) {
		switch (userAction) {
		case BACKBUTTON:
			if (state == State.INTERACTING) {
				game.switchState(new Welcome(game, game.isPresentation()));
			}
			break;
		}
	}

}
