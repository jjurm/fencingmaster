package com.jjurm.talentum.fencingmaster.game;

import com.jjurm.talentum.fencingmaster.enums.Color;
import com.jjurm.talentum.fencingmaster.flags.Flag;


public class Welcome extends FMState {
	
	public Welcome(Game game) {
		super(game);
	}

	@Override
	protected void begin0() {
		for (Color color : Color.values()) {
			rgb.filter().color(color).pulse(500, true);
		}
		
		FMState menu = new Menu(game);
		game.switchState(menu);
	}

	@Override
	protected void end0() {
	}

	@Override
	public void terminate() {
		flags.add(Flag.TERMINATE);
	}

}
