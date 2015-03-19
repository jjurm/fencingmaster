package com.jjurm.talentum.fencingmaster.game.fmstate;

import static com.jjurm.talentum.fencingmaster.enums.Side.DOWN;
import static com.jjurm.talentum.fencingmaster.enums.Side.UP;
import static com.jjurm.talentum.fencingmaster.utils.Utils.sleep;

import com.jjurm.talentum.fencingmaster.game.FMState;
import com.jjurm.talentum.fencingmaster.game.Game;
import com.jjurm.talentum.fencingmaster.rgb.GroupedRGBFilter;
import com.jjurm.talentum.fencingmaster.rgb.RGBFilter;

public class TestState extends FMState {

	public TestState(Game game) {
		super(game);
	}

	@Override
	protected void begin0() {
		//player.play(Sound.CORRECT);
		RGBFilter f = new GroupedRGBFilter(rgb.filter().side(UP), rgb.filter().side(DOWN));
		for (int i = 0; i < 2; i++) {
			f.set(true);
			sleep(500);
			f.set(false);
			sleep(500);
		}
		game.switchState(new Menu(game));
	}

	@Override
	protected void end0() {
	}

}
