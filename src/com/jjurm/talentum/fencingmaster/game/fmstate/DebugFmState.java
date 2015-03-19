package com.jjurm.talentum.fencingmaster.game.fmstate;

import static com.jjurm.talentum.fencingmaster.enums.Color.BLUE;
import static com.jjurm.talentum.fencingmaster.enums.Color.GREEN;
import static com.jjurm.talentum.fencingmaster.enums.Color.RED;

import com.jjurm.talentum.fencingmaster.enums.UserAction;
import com.jjurm.talentum.fencingmaster.enums.Color;
import com.jjurm.talentum.fencingmaster.enums.Foot;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.enums.State;
import com.jjurm.talentum.fencingmaster.game.FMState;
import com.jjurm.talentum.fencingmaster.game.Game;

public class DebugFmState extends FMState {

	public DebugFmState(Game game) {
		super(game);
	}

	@Override
	protected void begin0() {
	}

	@Override
	protected void end0() {
	}
	
	@Override
	protected void plateTouched0(Side side) {
		rgb.filter().side(side).color(GREEN).pulse(200, false);
	}
	
	@Override
	protected void footChanged0(Foot foot, State state) {
		Side side = foot == Foot.FRONT ? Side.UP : Side.DOWN;
		Color color = state == State.INTERACTING ? BLUE : RED;
		rgb.filter().side(side).color(color).pulse(200, false);
	}
	
	@Override
	public void userAction(UserAction userAction, State state) {
		game.switchState(new Menu(game));
	}

}
