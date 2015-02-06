package com.jjurm.talentum.fencingmaster.game;

import static com.jjurm.talentum.fencingmaster.enums.Color.BLUE;
import static com.jjurm.talentum.fencingmaster.enums.Color.GREEN;
import static com.jjurm.talentum.fencingmaster.enums.Color.RED;
import static com.jjurm.talentum.fencingmaster.enums.Side.DOWN;
import static com.jjurm.talentum.fencingmaster.enums.Side.UP;
import static com.jjurm.talentum.fencingmaster.utils.Utils.sleep;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jjurm.talentum.fencingmaster.Main;
import com.jjurm.talentum.fencingmaster.StateHolder;
import com.jjurm.talentum.fencingmaster.enums.Button;
import com.jjurm.talentum.fencingmaster.enums.Foot;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.enums.State;
import com.jjurm.talentum.fencingmaster.rgb.RGBController;
import com.jjurm.talentum.fencingmaster.utils.RandomGenerator;

public class TrainingFMState extends FMState implements StateHolder<GameState> {

	public final int CONST_waitingForGameState_min = 1000;
	public final int CONST_waitingForGameState_max = 2000;
	public final int CONST_boomed_time = 3000;

	protected GameState gameState;

	protected final boolean useFoot;

	protected final AtomicBoolean reacting = new AtomicBoolean(false);
	protected final RandomGenerator rand;
	protected final ScheduledExecutorService pool;

	protected ScheduledFuture<?> boom;
	protected final Object boomMonitor = new Object();
	protected ScheduledFuture<?> boomEnd;
	protected final Object boomEndMonitor = new Object();
	protected Side boomedSide;
	protected long boomedTime;
	protected Foot[] lastFeet = new Foot[2];
	protected State[] lastFeetState = new State[2];

	protected int scoreGood = 0;
	protected int scoreBad = 0;
	
	protected List<GameEntry> history = new ArrayList<GameEntry>();
	
	public TrainingFMState(Game game, boolean useFoot) {
		super(game);
		this.useFoot = useFoot;

		rand = new RandomGenerator();
		pool = Executors.newScheduledThreadPool(1);
	}

	public void updateStats(int reactionTime, boolean plateOk, boolean footOk) {
		boolean ok = plateOk && footOk && reactionTime < CONST_boomed_time;
		GameEntry entry = new GameEntry(reactionTime, plateOk, footOk, ok);
		
		if (ok)
			scoreGood++;
		else
			scoreBad++;
		
		System.out.println("Game entry ("+reactionTime+", "+plateOk+", "+footOk+")");
		
		history.add(entry);
	}
	
	@Override
	public void switchState(GameState newState) {
		GameState oldState = gameState;

		if (oldState != null)
			oldState.end();
		gameState = newState;
		System.out.println("TrainigGame: " + String.valueOf(oldState) + " -> " + String.valueOf(newState));
		if (newState != null)
			newState.begin();
	}
	
	protected void setOrPulse(RGBController.RGBFilter filter) {
		setOrPulse(filter, 1000);
	}
	
	protected void setOrPulse(RGBController.RGBFilter filter, int millis) {
		if (useFoot)
			filter.set(true);
		else
			filter.pulse(millis, true);
	}

	@Override
	protected void begin0() {
		setOrPulse(rgb.filter().color(BLUE).side(Main.AVAILABLE_SIDES));
		sleep(300);
		if (useFoot) {
			switchState(new WaitingForUserActionGameState());
		} else {
			switchState(new WaitingForBoomGameState());
		}
	}

	@Override
	protected void end0() {
		switchState(null);
		pool.shutdownNow();
	}

	@Override
	public void terminate() {
		game.switchState(new Byebye(game, true));
	}

	@Override
	public void buttonPressed(Button button, State state) {
		switch (button) {
		case BACKBUTTON:
			if (state == State.INTERACTING) {
				switchState(null);
				game.switchState(new Menu(game));
			}
		}
	}

	@Override
	protected void plateTouched0(Side side) {
		if (reacting.compareAndSet(false, true)) {
			if (gameState != null)
				gameState.plateTouched(side);
			reacting.set(false);
		}
	}

	@Override
	protected void footChanged0(Foot foot, State state) {
		if (reacting.compareAndSet(false, true)) {
			if (gameState != null)
				gameState.footChanged(foot, state);
			reacting.set(false);
		}
	}

	protected class WaitingForUserActionGameState extends GameState {

		protected boolean[] pressed = new boolean[2];
		
		@Override
		public void begin() {
		}

		@Override
		public void end() {
			rgb.filter().set(false);
		}

		@Override
		protected void plateTouched0(Side side) {
		}

		@Override
		protected void footChanged0(Foot foot, State state) {
			pressed[foot.index()] |= state.value();
			if (pressed[0] && pressed[1]) {
				switchState(new WaitingForBoomGameState());
			}
		}

	}

	protected class WaitingForBoomGameState extends GameState {

		@Override
		public void begin() {
			synchronized (boomMonitor) {
				long randTime = rand.nextInt(CONST_waitingForGameState_min, CONST_waitingForGameState_max);
				boom = pool.schedule(new Runnable() {
					@Override
					public void run() {
						switchState(new BoomedGameState());
					}
				}, randTime, TimeUnit.MILLISECONDS);
			}
		}

		@Override
		public void end() {
			synchronized (boomMonitor) {
				if (boom != null) {
					boom.cancel(false);
					boom = null;
				}
			}
		}
		
		private void userInterrupted() {
			end();
			updateStats(-1, false, false);
			if (useFoot) {
				rgb.filter().color(RED).set(true);
				switchState(new WaitingForUserActionGameState());
			} else {
				rgb.filter().color(RED).pulse(1000, true);
				switchState(new WaitingForBoomGameState());
			}
		}

		@Override
		protected void plateTouched0(Side side) {
			userInterrupted();
		}

		@Override
		protected void footChanged0(Foot foot, State state) {
			//userInterrupted();
		}

	}

	protected class BoomedGameState extends GameState {

		private AtomicBoolean reacting = new AtomicBoolean(true);

		@Override
		public void begin() {
			Side randomSide = Side.ALL[rand.nextInt(Side.ALL.length)];
			rgb.filter().side(randomSide).color(BLUE).set(true);
			boomedSide = randomSide;
			boomedTime = System.currentTimeMillis();
			lastFeet = new Foot[2];
			lastFeetState = new State[2];

			synchronized (boomEndMonitor) {
				boomEnd = pool.schedule(new Runnable() {
					@Override
					public void run() {
						reacting.set(false);
						boolean footOk = lastFeet[0] == Foot.BACK && lastFeet[1] == Foot.FRONT
									&& lastFeetState[0] == State.NON_INTERACTING && lastFeetState[1] == State.NON_INTERACTING;
						updateStats(-1, false, footOk);
						rgb.filter().color(BLUE).set(false);
						setOrPulse(rgb.filter().color(RED));
						if (useFoot) {
							switchState(new WaitingForUserActionGameState());
						} else {
							switchState(new WaitingForBoomGameState());
						}
					}
				}, CONST_boomed_time, TimeUnit.MILLISECONDS);
			}
			
			lastFeet = new Foot[2];
		}

		@Override
		public void end() {
			//rgb.filter().color(BLUE).set(false);
			synchronized (boomEndMonitor) {
				if (boomEnd != null) {
					boomEnd.cancel(false);
					boomEnd = null;
				}
			}
		}

		@Override
		protected void plateTouched0(Side side) {
			if (reacting.compareAndSet(true, false)) {
				int reactingTime = (int) (System.currentTimeMillis() - boomedTime);
				boolean plateOk = boomedSide == side;
				boolean footOk = !useFoot || (lastFeet[0] == Foot.BACK && lastFeet[1] == Foot.FRONT
							&& lastFeetState[0] == State.NON_INTERACTING && lastFeetState[1] == State.NON_INTERACTING);
				updateStats(reactingTime, plateOk, footOk);
				if (plateOk) {
					rgb.filter().color(BLUE).set(false);
					if (footOk) {
						setOrPulse(rgb.filter().color(GREEN).side(side));
					} else {
						setOrPulse(rgb.filter().color(RED).side(new Side[]{UP, DOWN}));
					}
				} else {
					setOrPulse(rgb.filter().color(RED).side(side));
					if (!useFoot) {
						rgb.filter().color(BLUE).set(false);
					}
				}
				if (useFoot) {
					switchState(new WaitingForUserActionGameState());
				} else {
					switchState(new WaitingForBoomGameState());
				}
			}
		}

		@Override
		protected void footChanged0(Foot foot, State state) {
			if (useFoot && reacting.get()) {
				lastFeet[1] = lastFeet[0];
				lastFeet[0] = foot;
				lastFeetState[1] = lastFeetState[0];
				lastFeetState[0] = state;
			}
		}

	}

}
