package com.jjurm.talentum.fencingmaster.enums;

public enum Foot {

	FRONT(0, "front"), BACK(1, "back");
	
	private int index;
	private String name;
	
	public static final Foot[] ALL = {FRONT, BACK};
	
	private Foot(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	public int index() {
		return this.index;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
