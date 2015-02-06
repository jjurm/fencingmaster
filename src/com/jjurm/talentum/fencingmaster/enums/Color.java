package com.jjurm.talentum.fencingmaster.enums;

public enum Color {
	
	RED(0), GREEN(1), BLUE(2);
	
	private int index;
	
	public static final Color[] ALL = new Color[]{RED, GREEN, BLUE};
	
	private Color(int index) {
		this.index = index;
	}
	
	public int index() {
		return this.index;
	}
	
	public static Color[] array(Color color) {
		if (color == null) {
			return ALL;
		} else {
			return new Color[]{color};
		}
	}
	
}
