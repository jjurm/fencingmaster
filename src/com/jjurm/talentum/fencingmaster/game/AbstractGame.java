package com.jjurm.talentum.fencingmaster.game;

import static com.jjurm.talentum.fencingmaster.enums.Color.BLUE;
import static com.jjurm.talentum.fencingmaster.utils.Utils.sleep;

import com.jjurm.talentum.fencingmaster.Config;
import com.jjurm.talentum.fencingmaster.StateHolder;
import com.jjurm.talentum.fencingmaster.audio.Sound;
import com.jjurm.talentum.fencingmaster.enums.Foot;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.enums.State;
import com.jjurm.talentum.fencingmaster.enums.UserAction;
import com.jjurm.talentum.fencingmaster.game.fmstate.Menu;
import com.jjurm.talentum.fencingmaster.rgb.RGBFilter;

public abstract class AbstractGame extends FMState implements StateHolder<GameState>, Signalizable {

	protected GameState gameState;
	
	public AbstractGame(Game game) {
		super(game);
	}
	
	@Override
	protected void plateTouched0(Side side) {
		if (gameState != null)
			gameState.plateTouched(side);
	}

	@Override
	protected void footChanged0(Foot foot, State state) {
		if (gameState != null)
			gameState.footChanged(foot, state);
	}
	
	protected void registerGameEntry(GameEntry.Type type, GameConditions conds) {
		GameEntry entry = new GameEntry(type, conds);
		game.historyAppend(entry);
	}

	@Override
	public void signal(RGBFilter filter, int millis) {
		filter.pulse(millis, true);
	}
	
	@Override
	public void signal(RGBFilter filter) {
		signal(filter, 1000);
	}
	
	@Override
	public void signalOff() {}
	
	protected final void nextRound() {
		nextRound0();
		reacting.set(true);
	}
	
	protected abstract void nextRound0();
	
	@Override
	protected void begin0() {
		try {
			signal(rgb.filter().color(BLUE).side(Config.AVAILABLE_SIDES));
		} catch (Exception e) {
			e.printStackTrace();
		}
		sleep(300);
		nextRound();
	}
	
	@Override
	protected void end0() {
		switchState(null);
		rgb.filter().set(false);
		Sound.BACK.play();
	}
	
	@Override
	public final void switchState(GameState newState) {
		GameState oldState = gameState;

		if (oldState != null)
			oldState.end();
		gameState = newState;
		if (newState != null)
			newState.begin();
	}
	
	@Override
	public void userAction(UserAction userAction, State state) {
		switch (userAction) {
		case BACKBUTTON:
			if (state == State.INTERACTING) {
				switchState(null);
				game.switchState(new Menu(game));
			}
		}
	}
	
	public AbstractGame getGameInstance() {
		return this;
	}
	
}
