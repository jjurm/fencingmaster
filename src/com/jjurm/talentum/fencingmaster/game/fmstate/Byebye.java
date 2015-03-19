package com.jjurm.talentum.fencingmaster.game.fmstate;

import static com.jjurm.talentum.fencingmaster.enums.Color.RED;
import static com.jjurm.talentum.fencingmaster.enums.Side.DOWN;
import static com.jjurm.talentum.fencingmaster.enums.Side.LEFT;
import static com.jjurm.talentum.fencingmaster.enums.Side.RIGHT;
import static com.jjurm.talentum.fencingmaster.enums.Side.UP;

import com.jjurm.talentum.fencingmaster.game.FMState;
import com.jjurm.talentum.fencingmaster.game.Game;

public class Byebye extends FMState {

	private boolean shutdown;
	
	public Byebye(Game game, boolean shutdown) {
		super(game);
		this.shutdown = shutdown;
	}

	@Override
	protected void begin0() {
		for (int i = 0; i < 2; i++) {
			rgb.filter().color(RED).side(UP, DOWN).pulse(200, true);
			rgb.filter().color(RED).side(LEFT, RIGHT).pulse(200, true);
		}
		if (shutdown) {
			game.switchState(new Shutdown(game));
		} else {
			game.switchState(new SilentState(game));
		}
	}

	@Override
	protected void end0() {
	}

}
