package com.jjurm.talentum.fencingmaster.game.fmstate.games;

import static com.jjurm.talentum.fencingmaster.enums.Color.RED;
import static com.jjurm.talentum.fencingmaster.enums.Side.DOWN;
import static com.jjurm.talentum.fencingmaster.enums.Side.UP;

import com.jjurm.talentum.fencingmaster.enums.Condition;
import com.jjurm.talentum.fencingmaster.enums.Foot;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.enums.State;
import com.jjurm.talentum.fencingmaster.game.FootTracker;
import com.jjurm.talentum.fencingmaster.game.Game;
import com.jjurm.talentum.fencingmaster.game.WaitingForUserActionGameState;
import com.jjurm.talentum.fencingmaster.rgb.RGBFilter;
import com.jjurm.talentum.fencingmaster.rgb.SignalHolder;

public class ColorWFootGame extends ColorGame {

	public ColorWFootGame(Game game) {
		super(game);
	}

	SignalHolder lastSignal = new SignalHolder();

	@Override
	public void signal(RGBFilter filter, int millis) {
		lastSignal.update(filter);
	}

	@Override
	public void signalOff() {
		lastSignal.off();
	}

	@Override
	protected void nextRound0() {
		switchState(new WaitingForUserActionGameState(this, () -> switchState(new WaitingForBoomGameState())));
	}
	
	@Override
	protected BoomedGameState newBoomedGameState() {
		return new BoomedColorWFootGameState();
	}
	
	protected class BoomedColorWFootGameState extends BoomedColorGameState {
		
		protected FootTracker feet = new FootTracker();
		
		@Override
		protected Condition getFootCondition() {
			return feet.getFootCondition();
		}
		
		@Override
		public void footChanged(Foot foot, State state) {
			feet.footChanged(foot, state);
		}
		
		@Override
		protected void signalPlateOk(Side side, Condition foot) {
			if (foot.isOk()) {
				super.signalPlateOk(side, foot);
			} else {
				signal(rgb.filter().color(RED).side(UP, DOWN));
			}
		}
		
	}

}
