package com.jjurm.talentum.fencingmaster.game.fmstate;

import static com.jjurm.talentum.fencingmaster.enums.Color.BLUE;
import static com.jjurm.talentum.fencingmaster.enums.Color.RED;
import static com.jjurm.talentum.fencingmaster.enums.Side.DOWN;
import static com.jjurm.talentum.fencingmaster.utils.Utils.sleep;

import java.io.IOException;
import java.util.concurrent.Future;

import com.jjurm.talentum.fencingmaster.Main;
import com.jjurm.talentum.fencingmaster.enums.State;
import com.jjurm.talentum.fencingmaster.enums.UserAction;
import com.jjurm.talentum.fencingmaster.game.FMState;
import com.jjurm.talentum.fencingmaster.game.Game;


public class Shutdown extends FMState {

	protected Future<?> exitFuture;
	
	public Shutdown(Game game) {
		super(game);
	}

	@Override
	protected void begin0() {
		shutdownRpi();
		/*exitFuture = game.pool.schedule(new Runnable() {
			@Override
			public void run() {
				reacting.set(false);
				rgb.filter().side(DOWN).color(RED).set(false);
				shutdownRpi();
				//shutdownProgram();
			}
		}, 2000, TimeUnit.MILLISECONDS);
		rgb.filter().color(RED).set(true);
		for (Side side : Side.ALL) {
			sleep(500);
			rgb.filter().color(RED).side(side).set(false);
		}
		reacting.set(false);*/
	}

	@Override
	protected void end0() {
	}
	
	@Override
	public void userAction(UserAction userAction, State state) {
		if (reacting.get()) {
			if (userAction == UserAction.BACKBUTTON && state == State.INTERACTING) {
				if (exitFuture != null) {
					exitFuture.cancel(false);
				}
				rgb.filter().color(RED).set(false);
				rgb.filter().side(DOWN).color(BLUE).set(true);
				sleep(1000);
				rgb.filter().side(DOWN).color(BLUE).set(false);
				shutdownProgram();
			}
		}
	}
	
	private void shutdownProgram() {
		Main.exit();
	}
	
	private void shutdownRpi() {
		try {
			Runtime.getRuntime().exec("sudo shutdown -h now");
		} catch (IOException e) {
			e.printStackTrace();
			Main.exit();
		}
	}
	
	private void rebootRpi() {
		try {
			Runtime.getRuntime().exec("sudo reboot");
		} catch (IOException e) {
			e.printStackTrace();
			Main.exit();
		}
	}

}
