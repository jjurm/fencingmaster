package com.jjurm.talentum.fencingmaster.enums;

public enum UserAction {
	
	BACKBUTTON(0, "backbutton");
	
	private int index;
	private String name;
	
	private UserAction(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	public int index() {
		return this.index;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
