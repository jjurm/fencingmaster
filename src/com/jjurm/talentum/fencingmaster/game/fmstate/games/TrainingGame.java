package com.jjurm.talentum.fencingmaster.game.fmstate.games;

import static com.jjurm.talentum.fencingmaster.enums.Color.BLUE;
import static com.jjurm.talentum.fencingmaster.enums.Color.GREEN;
import static com.jjurm.talentum.fencingmaster.enums.Color.RED;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.jjurm.talentum.fencingmaster.Config;
import com.jjurm.talentum.fencingmaster.audio.Sound;
import com.jjurm.talentum.fencingmaster.enums.Condition;
import com.jjurm.talentum.fencingmaster.enums.Foot;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.enums.State;
import com.jjurm.talentum.fencingmaster.game.AbstractGame;
import com.jjurm.talentum.fencingmaster.game.Game;
import com.jjurm.talentum.fencingmaster.game.GameConditions;
import com.jjurm.talentum.fencingmaster.game.GameEntry;
import com.jjurm.talentum.fencingmaster.game.GameState;
import com.jjurm.talentum.fencingmaster.game.RoundEndRunnable;
import com.jjurm.talentum.fencingmaster.rgb.GroupedRGBFilter;
import com.jjurm.talentum.fencingmaster.utils.Utils;

public class TrainingGame extends AbstractGame {

	public TrainingGame(Game game) {
		super(game);
	}

	protected void playSound(GameEntry gameEntry) {
		if (gameEntry.getGameConditions().isOk()) {
			Sound.CORRECT.play();
		} else {
			Sound.WRONG.play();
		}
	}

	@Override
	protected void nextRound0() {
		switchState(new WaitingForBoomGameState());
	}

	protected class WaitingForBoomGameState extends GameState {

		protected ScheduledFuture<?> boom;
		protected final Object boomMonitor = new Object();

		@Override
		public void begin() {
			synchronized (boomMonitor) {
				long randTime = Utils.rand.nextInt(Config.CONST_waitingForGameState_min,
						Config.CONST_waitingForGameState_max);
				boom = game.pool.schedule(new Runnable() {
					@Override
					public void run() {
						switchState(newBoomedGameState());
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
			registerGameEntry(
					GameEntry.Type.GAME,
					new GameConditions.Builder().reactionTime(-1).reactionTime(Condition.INCORRECT)
							.plate(Condition.INCORRECT).build());
			signal(rgb.filter().color(RED));
			nextRound();
		}

		@Override
		public void plateTouched(Side side) {
			userInterrupted();
		}

		@Override
		public void footChanged(Foot foot, State state) {
			// userInterrupted();
		}

	}

	protected BoomedGameState newBoomedGameState() {
		return new BoomedGameState();
	}

	protected class BoomedGameState extends GameState {

		protected ScheduledFuture<?> boomEnd;
		protected final Object boomEndMonitor = new Object();
		protected Side boomedSide;
		protected long boomedTime;

		protected void updateRandomSide() {
			Side randomSide = Side.randomAvailableSide();
			rgb.filter().side(randomSide).color(BLUE).set(true);
			boomedSide = randomSide;
		}

		protected Condition getFootCondition() {
			return Condition.NOT_USED;
		}

		protected void scheduleBoomEnd() {
			synchronized (boomEndMonitor) {
				boomEnd = game.pool.schedule(new RoundEndRunnable(getGameInstance()) {
					@Override
					protected void run0() {
						registerGameEntry(GameEntry.Type.GAME, new GameConditions.Builder().reactionTime(-1)
								.reactionTime(Condition.INCORRECT).plate(Condition.INCORRECT).foot(getFootCondition())
								.build());
						rgb.filter().set(false);
						signal(rgb.filter().color(RED));
					}
				}, Config.CONST_hit_timeout, TimeUnit.MILLISECONDS);
			}
		}

		protected void cancelBoomEnd() {
			synchronized (boomEndMonitor) {
				if (boomEnd != null) {
					boomEnd.cancel(false);
					boomEnd = null;
				}
			}
		}

		@Override
		public void begin() {
			updateRandomSide();
			boomedTime = System.currentTimeMillis();
			game.sendBoomedEvent();

			scheduleBoomEnd();
		}

		@Override
		public void end() {
			cancelBoomEnd();
		}

		protected int getReactingTime() {
			return (int) (System.currentTimeMillis() - boomedTime);
		}

		@Override
		public final void plateTouched(Side side) {
			cancelBoomEnd();
			boolean plateOk = boomedSide == side;
			Condition foot = getFootCondition();
			registerGameEntry(
					GameEntry.Type.GAME,
					new GameConditions.Builder().reactionTime(getReactingTime()).plate(Condition.get(plateOk))
							.foot(foot).build());
			rgb.filter().set(false);
			if (plateOk) {
				signalPlateOk(side, foot);
			} else {
				Sound.WRONG.play();
				signal(new GroupedRGBFilter(rgb.filter().color(BLUE).side(boomedSide), rgb.filter().color(RED)
						.side(side)));
			}
			nextRound();
		}

		protected void signalPlateOk(Side side, Condition foot) {
			Sound.CORRECT.play();
			signal(rgb.filter().color(GREEN).side(side));
		}

		@Override
		public void footChanged(Foot foot, State state) {
		}

	}

}
