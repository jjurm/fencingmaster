package com.jjurm.talentum.fencingmaster.enums;

public enum Button {
	
	BACKBUTTON(0, "back");
	
	private int index;
	private String name;
	
	public static final Button[] ALL = {BACKBUTTON};
	
	private Button(int index, String name) {
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
