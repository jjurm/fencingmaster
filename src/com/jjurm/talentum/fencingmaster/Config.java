package com.jjurm.talentum.fencingmaster;

import static com.jjurm.talentum.fencingmaster.enums.Side.DOWN;
import static com.jjurm.talentum.fencingmaster.enums.Side.LEFT;
import static com.jjurm.talentum.fencingmaster.enums.Side.RIGHT;
import static com.jjurm.talentum.fencingmaster.enums.Side.UP;

import com.jjurm.talentum.fencingmaster.enums.Side;

public abstract class Config {

	public static final Side[] AVAILABLE_SIDES = {RIGHT, UP, LEFT, DOWN};
	
	public static final int CONST_waitingForGameState_min = 1000;
	public static final int CONST_waitingForGameState_max = 2000;
	public static final int CONST_hit_timeout = 3000;
	
	public static final int socketPort = 7070;
	
}
