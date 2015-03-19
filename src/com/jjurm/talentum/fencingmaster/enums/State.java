package com.jjurm.talentum.fencingmaster.enums;

public enum State {

	INTERACTING(true, "interacting"), NON_INTERACTING(false, "non_interacting");
	
	public static final State DEFAULT = NON_INTERACTING;
	
	private boolean value;
	private String name;
	
	private State(boolean value, String name) {
		this.value = value;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public boolean value() {
		return this.value;
	}
	
}
