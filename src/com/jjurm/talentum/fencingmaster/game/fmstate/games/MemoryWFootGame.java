package com.jjurm.talentum.fencingmaster.game.fmstate.games;

import static com.jjurm.talentum.fencingmaster.enums.Color.RED;
import static com.jjurm.talentum.fencingmaster.enums.Side.DOWN;
import static com.jjurm.talentum.fencingmaster.enums.Side.UP;

import com.jjurm.talentum.fencingmaster.audio.Sound;
import com.jjurm.talentum.fencingmaster.enums.Condition;
import com.jjurm.talentum.fencingmaster.enums.Foot;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.enums.State;
import com.jjurm.talentum.fencingmaster.game.FootTracker;
import com.jjurm.talentum.fencingmaster.game.Game;
import com.jjurm.talentum.fencingmaster.game.WaitingForUserActionGameState;
import com.jjurm.talentum.fencingmaster.rgb.RGBFilter;
import com.jjurm.talentum.fencingmaster.rgb.SignalHolder;

public class MemoryWFootGame extends MemoryGame {

	public MemoryWFootGame(Game game) {
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
		switchState(new WaitingForUserActionGameState(this, () -> switchState(new MemorizingGameState())));
	}
	
	@Override
	protected HittingGameState newHittingGameState() {
		return new HittingWFootGameState();
	}
	
	protected class HittingWFootGameState extends HittingGameState {
		
		protected boolean overallFootOk = true;
		
		protected FootTracker feet = new FootTracker();
		
		@Override
		protected Condition getHitFootCondition() {
			return feet.getFootCondition();
		}
		
		@Override
		protected Condition getOverallFootCondition() {
			return overallFootOk ? Condition.CORRECT : Condition.INCORRECT;
		}
		
		@Override
		protected void nextHit(Condition foot) {
			feet.reset();
			overallFootOk &= foot.isOk();
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
				Sound.WRONG2.play();
				if (isFinish()) {
					signal(rgb.filter().color(RED).side(UP, DOWN));
				} else {
					rgb.filter().color(RED).side(UP, DOWN).pulse(BLINK_DURATION, true);
				}
			}
		}
		
	}
	

}
