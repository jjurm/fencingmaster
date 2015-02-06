package com.jjurm.talentum.fencingmaster.game;

import static com.jjurm.talentum.fencingmaster.enums.Color.BLUE;
import static com.jjurm.talentum.fencingmaster.enums.Color.GREEN;
import static com.jjurm.talentum.fencingmaster.enums.Color.RED;
import static com.jjurm.talentum.fencingmaster.enums.Side.DOWN;
import static com.jjurm.talentum.fencingmaster.enums.Side.LEFT;
import static com.jjurm.talentum.fencingmaster.enums.Side.RIGHT;
import static com.jjurm.talentum.fencingmaster.enums.Side.UP;
import static com.jjurm.talentum.fencingmaster.utils.Utils.sleep;

import java.util.concurrent.TimeUnit;

import com.jjurm.talentum.fencingmaster.enums.Button;
import com.jjurm.talentum.fencingmaster.enums.Color;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.enums.State;

public class Menu extends FMState {

	public static final Side[] IMPLEMENTED_SIDES = {UP};
	
	public Menu(Game game) {
		super(game);
	}

	@Override
	public void buttonPressed(Button button, State state) {
		if (reacting.compareAndSet(true, false)) {
			switch (button) {
			case BACKBUTTON:
				if (state == State.INTERACTING) {
					game.shutdownFuture = game.pool.schedule(() -> game.switchState(new Byebye(game, true)), 2000, TimeUnit.MILLISECONDS);
					game.switchState(new SilentState(game));
				}
			}
		}
	}
	
	@Override
	protected void plateTouched0(Side side) {
		switch (side) {
		case UP:
			game.switchState(new TrainingFMState(game, true));
			break;
		case LEFT:
			game.switchState(new DebugFmState(game));
			break;
		case DOWN:
			game.switchState(new TrainingFMState(game, false));
			break;
		default:
			//rgb.filter().color(BLUE).side(side).pulse(500, true);
			break;
		}
		System.out.println(side);
	}
	
	@Override
	protected void begin0() {
		Side[] sides = {UP, RIGHT, DOWN, LEFT};
		for (Side side : sides) {
			rgb.filter().side(side).color(GREEN).set(true);
			sleep(150);
		}
		sleep(300);
		rgb.filter().set(false);
		//rgb.filter().color(GREEN).sideI(IMPLEMENTED_SIDES).set(false);
		rgb.filter().side(RIGHT).color(BLUE).set(true);
		rgb.filter().side(DOWN).color(RED).set(true);
		rgb.filter().side(LEFT).color(new Color[]{GREEN, RED}).set(true);
		rgb.filter().side(UP).color(GREEN).set(true);
	}

	@Override
	protected void end0() {
		rgb.filter().set(false);
	}
	
	@Override
	public void terminate() {
		game.switchState(new Byebye(game, true));
	}

}
