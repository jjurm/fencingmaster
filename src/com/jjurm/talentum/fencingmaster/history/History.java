package com.jjurm.talentum.fencingmaster.history;

import java.util.ArrayList;
import java.util.Collection;

public class History<T extends UserAction<?>> extends ArrayList<T>{
	private static final long serialVersionUID = 1L;
	
	public History() {
		super();
	}
	
	public History(Collection<? extends T> arg0) {
		super(arg0);
	}
	
	public History(int arg0) {
		super(arg0);
	}
	
}
