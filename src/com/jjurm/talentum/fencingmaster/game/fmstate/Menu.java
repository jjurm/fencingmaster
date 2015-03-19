package com.jjurm.talentum.fencingmaster.game.fmstate;

import static com.jjurm.talentum.fencingmaster.enums.Color.BLUE;
import static com.jjurm.talentum.fencingmaster.enums.Color.GREEN;
import static com.jjurm.talentum.fencingmaster.enums.Color.RED;
import static com.jjurm.talentum.fencingmaster.enums.Side.DOWN;
import static com.jjurm.talentum.fencingmaster.enums.Side.LEFT;
import static com.jjurm.talentum.fencingmaster.enums.Side.RIGHT;
import static com.jjurm.talentum.fencingmaster.enums.Side.UP;

import java.util.concurrent.TimeUnit;

import com.jjurm.talentum.fencingmaster.audio.Sound;
import com.jjurm.talentum.fencingmaster.enums.Foot;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.enums.State;
import com.jjurm.talentum.fencingmaster.enums.UserAction;
import com.jjurm.talentum.fencingmaster.game.FMState;
import com.jjurm.talentum.fencingmaster.game.Game;
import com.jjurm.talentum.fencingmaster.game.fmstate.games.ColorGame;
import com.jjurm.talentum.fencingmaster.game.fmstate.games.ColorWFootGame;
import com.jjurm.talentum.fencingmaster.game.fmstate.games.MemoryGame;
import com.jjurm.talentum.fencingmaster.game.fmstate.games.MemoryWFootGame;
import com.jjurm.talentum.fencingmaster.game.fmstate.games.PresentationMode;
import com.jjurm.talentum.fencingmaster.game.fmstate.games.TrainingGame;
import com.jjurm.talentum.fencingmaster.game.fmstate.games.TrainingWFootGame;

public class Menu extends FMState {

	public static final Side[] IMPLEMENTED_SIDES = { UP };

	protected boolean useFoot = true;

	public Menu(Game game) {
		super(game);
	}

	@Override
	public void userAction(UserAction userAction, State state) {
		if (reacting.compareAndSet(true, false)) {
			switch (userAction) {
			case BACKBUTTON:
				if (state == State.INTERACTING) {
					game.setPresentation(game.footTracker.getState(Foot.FRONT) == State.INTERACTING
							|| game.footTracker.getState(Foot.BACK) == State.INTERACTING);
					synchronized (game.shutdownFutureMonitor) {
						game.shutdownFuture = game.pool.schedule(() -> game.switchState(new Byebye(game, true)), 2000,
								TimeUnit.MILLISECONDS);
					}
					game.switchState(new SilentState(game));
				}
			}
			reacting.set(true);
		}
	}

	@Override
	protected void plateTouched0(Side side) {
		if (side == DOWN) {
			useFoot = !useFoot;
			updateUseFoot();
			Sound.CORRECT.play();
		} else {
			Sound.START.play();
			if (game.isPresentation()) {
				game.switchState(new PresentationMode(game));
			} else {
				switch (side) {
				case UP:
					if (useFoot)
						game.switchState(new TrainingWFootGame(game));
					else
						game.switchState(new TrainingGame(game));
					break;
				case RIGHT:
					if (useFoot)
						game.switchState(new MemoryWFootGame(game));
					else
						game.switchState(new MemoryGame(game));
					break;
				case LEFT:
					if (useFoot)
						game.switchState(new ColorWFootGame(game));
					else
						game.switchState(new ColorGame(game));
				case DOWN:
					// not possible
				default:
					break;
				}
			}
		}
	}

	protected void updateUseFoot() {
		rgb.filter().side(DOWN).color(RED, GREEN).set(useFoot);
	}

	@Override
	protected void begin0() {
		rgb.filter().side(RIGHT).color(BLUE).set(true);
		rgb.filter().side(LEFT).color(RED).set(true);
		rgb.filter().side(UP).color(GREEN).set(true);
		updateUseFoot();
	}

	@Override
	protected void end0() {
		rgb.filter().set(false);
	}

}
