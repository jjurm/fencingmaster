package com.jjurm.talentum.fencingmaster.game.fmstate.games;

import static com.jjurm.talentum.fencingmaster.enums.Color.BLUE;
import static com.jjurm.talentum.fencingmaster.enums.Color.GREEN;
import static com.jjurm.talentum.fencingmaster.enums.Color.RED;
import static com.jjurm.talentum.fencingmaster.utils.Utils.shuffleArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.jjurm.talentum.fencingmaster.Config;
import com.jjurm.talentum.fencingmaster.enums.Color;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.game.Game;

public class ColorGame extends TrainingGame {

	Color[][] colors = new Color[][] { { RED }, { GREEN }, { BLUE }, { RED, GREEN } };
	Map<Side, Color[]> assignments = new HashMap<Side, Color[]>();
	
	protected Future<?> roundStart;
	protected Object roundStartMonitor = new Object();

	public ColorGame(Game game) {
		super(game);
		assignColors();
	}

	@Override
	protected void begin0() {
		for (Side side : Config.AVAILABLE_SIDES) {
			rgb.filter().side(side).color(assignments.get(side)).set(true);
		}
		synchronized (roundStartMonitor) {
			roundStart = game.pool.schedule(new Runnable() {
				@Override
				public void run() {
					rgb.filter().set(false);
					nextRound();
				}
			}, 3000, TimeUnit.MILLISECONDS);
		}
	}
	
	@Override
	protected void end0() {
		synchronized (roundStartMonitor) {
			if (roundStart != null) {
				roundStart.cancel(false);
				roundStart = null;
			}
		}
		super.end0();
	}

	protected void assignColors() {
		Side[] sides = Config.AVAILABLE_SIDES;
		Color[][] colorsList = Arrays.copyOf(this.colors, this.colors.length);
		shuffleArray(colorsList);
		for (int i = 0; i < sides.length; i++) {
			assignments.put(sides[i], colorsList[i]);
		}
	}

	@Override
	protected BoomedGameState newBoomedGameState() {
		return new BoomedColorGameState();
	}

	protected class BoomedColorGameState extends BoomedGameState {

		@Override
		protected void updateRandomSide() {
			Side randomSide = Side.randomAvailableSide();
			rgb.filter().side(Side.randomSide()).color(assignments.get(randomSide)).set(true);
			boomedSide = randomSide;
		}

	}

}
