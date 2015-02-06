package com.jjurm.talentum.fencingmaster.enums;

public enum Side {

	RIGHT(0), UP(1), LEFT(2), DOWN(3);
	
	private int index;
	
	public static final Side[] ALL = {RIGHT, UP, LEFT, DOWN};
	
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
			return new Side[]{side};
		}
	}
	
}
