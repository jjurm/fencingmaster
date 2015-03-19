package com.jjurm.talentum.fencingmaster.game.fmstate.games;

import static com.jjurm.talentum.fencingmaster.enums.Color.BLUE;
import static com.jjurm.talentum.fencingmaster.enums.Color.GREEN;
import static com.jjurm.talentum.fencingmaster.enums.Color.RED;
import static com.jjurm.talentum.fencingmaster.enums.Side.DOWN;
import static com.jjurm.talentum.fencingmaster.enums.Side.RIGHT;
import static com.jjurm.talentum.fencingmaster.enums.Side.UP;

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
import com.jjurm.talentum.fencingmaster.game.WaitingForUserActionGameState;
import com.jjurm.talentum.fencingmaster.rgb.GroupedRGBFilter;
import com.jjurm.talentum.fencingmaster.utils.Utils;

public class PresentationMode extends AbstractGame {

	RoundStates[] rounds = new RoundStates[] { new RoundStates(RIGHT, true, true), new RoundStates(DOWN, false, true),
			new RoundStates(UP, true, false) };
	int roundIndex = -1; // will be 0 after first increment

	public PresentationMode(Game game) {
		super(game);
	}

	@Override
	protected void nextRound0() {
		roundIndex++;
		if (roundIndex >= rounds.length)
			roundIndex = 0;
		switchState(new WaitingForUserActionGameState(this, () -> switchState(new WaitingForBoomGameState())));
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

		@Override
		public void plateTouched(Side side) {
			// ignore
		}

		@Override
		public void footChanged(Foot foot, State state) {
			// ignore
		}

		protected class BoomedGameState extends GameState {

			protected long boomedTime;

			@Override
			public void begin() {
				rgb.filter().side(rounds[roundIndex].boomedSide).color(BLUE).set(true);
				boomedTime = System.currentTimeMillis();
				game.sendBoomedEvent();
			}

			protected int getReactingTime() {
				return (int) (System.currentTimeMillis() - boomedTime);
			}

			@Override
			public void end() {
			}

			@Override
			public void plateTouched(Side side) {
				RoundStates st = rounds[roundIndex];
				Condition foot = Condition.get(st.footOk);
				registerGameEntry(GameEntry.Type.GAME, new GameConditions.Builder().reactionTime(getReactingTime())
						.plate(Condition.get(st.plateOk)).foot(foot).build());
				if (st.plateOk) {
					rgb.filter().color(BLUE).side(st.boomedSide).set(false);
					signalPlateOk(side, foot);
				} else {
					Sound.WRONG.play();
					signal(new GroupedRGBFilter(rgb.filter().color(BLUE).side(st.boomedSide), rgb.filter().color(RED)
							.side(side)));
				}
				nextRound();
			}

			protected void signalPlateOk(Side side, Condition foot) {
				if (foot.isOk()) {
					Sound.CORRECT.play();
					signal(rgb.filter().color(GREEN).side(side));
				} else {
					Sound.WRONG2.play();
					signal(rgb.filter().color(RED).side(UP, DOWN));
				}
			}

			@Override
			public void footChanged(Foot foot, State state) {
				// ignore
			}

		}

	}

	protected class RoundStates {

		Side boomedSide;

		boolean plateOk;
		boolean footOk;

		public RoundStates(Side boomedSide, boolean plateOk, boolean footOk) {
			super();
			this.boomedSide = boomedSide;
			this.plateOk = plateOk;
			this.footOk = footOk;
		}

	}

}
