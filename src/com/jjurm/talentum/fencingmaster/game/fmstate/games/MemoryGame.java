package com.jjurm.talentum.fencingmaster.game.fmstate.games;

import static com.jjurm.talentum.fencingmaster.enums.Color.BLUE;
import static com.jjurm.talentum.fencingmaster.enums.Color.GREEN;
import static com.jjurm.talentum.fencingmaster.enums.Color.RED;

import java.util.concurrent.Future;
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

public class MemoryGame extends AbstractGame {

	protected final int BLINK_DURATION = 200;
	protected final int SUBLEVEL_COUNT = 2;

	protected int level = 3;
	protected int sublevel = SUBLEVEL_COUNT;
	protected Side[] targets;

	public MemoryGame(Game game) {
		super(game);
	}

	@Override
	protected void nextRound0() {
		switchState(new MemorizingGameState());
	}

	protected class MemorizingGameState extends GameState {

		protected int showIndex = 0;
		protected boolean showed = false;
		protected Future<?> future;

		protected void cancelSchedule() {
			if (future != null) {
				future.cancel(false);
				future = null;
			}
		}

		protected void schedule(Runnable command, long delay) {
			future = game.pool.schedule(command, delay, TimeUnit.MILLISECONDS);
		}

		Runnable command = new Runnable() {
			@Override
			public void run() {
				if (showed) {
					rgb.filter().color(BLUE).side(targets[showIndex]).set(false);
					showed = false;
					showIndex++;
					schedule(command, 200);
				} else { // not showed
					if (showIndex == level) {
						switchState(newHittingGameState());
					} else {
						Side side = Side.randomAvailableSide();
						targets[showIndex] = side;
						rgb.filter().color(BLUE).side(side).set(true);
						showed = true;
						schedule(command, 600);
					}
				}
			}
		};

		@Override
		public void begin() {
			targets = new Side[level];
			schedule(command, 300);
		}

		@Override
		public void end() {
			cancelSchedule();
			if (showed) {
				rgb.filter().color(BLUE).side(targets[showIndex]).set(false);
			}
		}

		@Override
		public void plateTouched(Side side) {
		}

		@Override
		public void footChanged(Foot foot, State state) {
		}

	}

	protected HittingGameState newHittingGameState() {
		return new HittingGameState();
	}

	protected class HittingGameState extends GameState {

		protected int targetIndex = 0;

		protected ScheduledFuture<?> hitEnd;
		protected final Object hitEndMonitor = new Object();
		protected long hittingStartedTime;
		protected long lastHitTime;

		protected Condition getHitFootCondition() {
			return Condition.NOT_USED;
		}

		protected Condition getOverallFootCondition() {
			return Condition.NOT_USED;
		}

		protected void scheduleHitEnd() {
			synchronized (hitEndMonitor) {
				cancelHitEnd();
				hitEnd = game.pool.schedule(new RoundEndRunnable(getGameInstance()) {
					@Override
					protected void run0() {
						registerGameEntry(
								GameEntry.Type.GAME,
								new GameConditions.Builder().reactionTime(-1).reactionTime(Condition.INCORRECT)
										.plate(Condition.INCORRECT).foot(getOverallFootCondition()).build());
						signal(rgb.filter().color(RED));
					}
				}, Config.CONST_hit_timeout, TimeUnit.MILLISECONDS);
			}
		}

		protected void cancelHitEnd() {
			synchronized (hitEndMonitor) {
				if (hitEnd != null) {
					hitEnd.cancel(false);
					hitEnd = null;
				}
			}
		}

		@Override
		public void begin() {
			hittingStartedTime = lastHitTime = System.currentTimeMillis();
			scheduleHitEnd();
			game.sendBoomedEvent();
		}

		@Override
		public void end() {
			cancelHitEnd();
		}

		protected void nextHit(Condition foot) {
		}

		protected int hitTime() { // returns reaction time
			long time = System.currentTimeMillis();
			int reactionTime = (int) (time - lastHitTime);
			lastHitTime = time;
			return reactionTime;
		}

		protected int getHittingTime() {
			return (int) (System.currentTimeMillis() - hittingStartedTime);
		}

		protected boolean isFinish() {
			return targetIndex == level;
		}

		@Override
		public void plateTouched(Side side) {
			boolean plateOk = side == targets[targetIndex];
			Condition foot = getHitFootCondition();
			if (plateOk) {
				targetIndex++;
				scheduleHitEnd();
				nextHit(foot);
				registerGameEntry(GameEntry.Type.GAMEPART,
						new GameConditions.Builder().reactionTime(hitTime()).plate(Condition.CORRECT).foot(foot)
								.build());
				if (isFinish()) {
					reacting.set(false);
					registerGameEntry(GameEntry.Type.GAME, new GameConditions.Builder().reactionTime(getHittingTime())
							.reactionTime(Condition.CORRECT).plate(Condition.CORRECT).foot(getOverallFootCondition())
							.build());
				}
				signalPlateOk(side, foot);
				if (isFinish()) {
					if (sublevel > 0) sublevel--;
					if (sublevel == 0) {
						sublevel = SUBLEVEL_COUNT;
						level++;
					}
					nextRound();
				}
			} else {
				reacting.set(false);
				registerGameEntry(GameEntry.Type.GAMEPART,
						new GameConditions.Builder().reactionTime(hitTime()).plate(Condition.INCORRECT).foot(foot)
						.build());
				registerGameEntry(GameEntry.Type.GAME, new GameConditions.Builder().reactionTime(getHittingTime())
						.plate(Condition.INCORRECT).foot(getOverallFootCondition()).build());
				Sound.WRONG.play();
				signal(new GroupedRGBFilter(rgb.filter().color(RED).side(side), rgb.filter().color(BLUE)
						.side(targets[targetIndex])));
				if (sublevel > 0) sublevel--;
				nextRound();
			}
		}

		protected void signalPlateOk(Side side, Condition foot) {
			if (isFinish()) {
				Sound.CORRECT2.play();
				signal(rgb.filter().color(GREEN));
			} else {
				Sound.CORRECT.play();
				rgb.filter().color(GREEN).side(side).pulse(BLINK_DURATION, true);
			}
		}

		@Override
		public void footChanged(Foot foot, State state) {
		}

	}

}
