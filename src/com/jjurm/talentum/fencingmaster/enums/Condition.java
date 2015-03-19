package com.jjurm.talentum.fencingmaster.enums;

import com.jjurm.talentum.fencingmaster.web.WebString;

public enum Condition implements WebString {

	CORRECT(true, "correct"),
	INCORRECT(false, "incorrect"),
	NOT_USED(true, "not_used");
	
	private boolean ok;
	private String webName;
	
	private Condition(boolean ok, String webName) {
		this.ok = ok;
		this.webName = webName;
	}
	
	public boolean isOk() {
		return this.ok;
	}
	
	@Override
	public String getWebName() {
		return this.webName;
	}
	
	public static Condition get(boolean state) {
		return state ? CORRECT : INCORRECT;
	}
	
}
