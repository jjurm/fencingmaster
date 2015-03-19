package com.jjurm.talentum.fencingmaster.game.fmstate;

import static com.jjurm.talentum.fencingmaster.utils.Utils.sleep;

import com.jjurm.talentum.fencingmaster.audio.Sound;
import com.jjurm.talentum.fencingmaster.enums.Color;
import com.jjurm.talentum.fencingmaster.game.FMState;
import com.jjurm.talentum.fencingmaster.game.Game;

public class Welcome extends FMState {
	
	protected boolean isLong;
	
	public Welcome(Game game, boolean isLong) {
		super(game);
		this.isLong = isLong;
	}
	
	@Override
	protected void begin0() {
		Sound.BOOT.play();
		if (isLong) {
			for (Color color : Color.values()) {
				rgb.filter().color(color).pulse(500, true);
			}
		}
		game.switchState(new Menu(game));
		sleep(100);
	}

	@Override
	protected void end0() {
	}

}
