package com.jjurm.talentum.fencingmaster.game;

import static com.jjurm.talentum.fencingmaster.enums.Color.BLUE;
import static com.jjurm.talentum.fencingmaster.enums.Color.GREEN;
import static com.jjurm.talentum.fencingmaster.enums.Color.RED;

import com.jjurm.talentum.fencingmaster.enums.Button;
import com.jjurm.talentum.fencingmaster.enums.Color;
import com.jjurm.talentum.fencingmaster.enums.Foot;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.enums.State;

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
	public void terminate() {
	}
	
	@Override
	protected void plateTouched0(Side side) {
		rgb.filter().side(side).color(GREEN).pulse(200, true);
	}
	
	@Override
	protected void footChanged0(Foot foot, State state) {
		Side side = foot == Foot.FRONT ? Side.UP : Side.DOWN;
		Color color = state == State.INTERACTING ? BLUE : RED;
		rgb.filter().side(side).color(color).pulse(200, true);
	}
	
	@Override
	public void buttonPressed(Button button, State state) {
		game.switchState(new Menu(game));
	}

}
