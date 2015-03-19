package com.jjurm.talentum.fencingmaster.enums;

import com.jjurm.talentum.fencingmaster.Config;
import com.jjurm.talentum.fencingmaster.utils.RandomGenerator;

public enum Side {

	RIGHT(0), UP(1), LEFT(2), DOWN(3);

	private int index;

	public static final Side[] ALL = { RIGHT, UP, LEFT, DOWN };

	private static final RandomGenerator rand = new RandomGenerator();

	public static Side randomSide() {
		return ALL[rand.nextInt(ALL.length)];
	}
	
	public static Side randomAvailableSide() {
		return Config.AVAILABLE_SIDES[rand.nextInt(Config.AVAILABLE_SIDES.length)];
	}

	private Side(int index) {
		this.index = index;
	}

	public int index() {
		return this.index;
	}

	public static Side[] array(Side side) {
		if (side == null) {
			return ALL;
		} else {
			return new Side[] { side };
		}
	}

}
