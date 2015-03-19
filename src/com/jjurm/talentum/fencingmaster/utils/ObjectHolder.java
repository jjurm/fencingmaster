package com.jjurm.talentum.fencingmaster.utils;

public class ObjectHolder<T> {

	T object;
	
	public ObjectHolder(T object) {
		this.object = object;
	}
	
	public ObjectHolder() {
		this(null);
	}
	
	public T get() {
		return object;
	}
	
	public void set(T object) {
		this.object = object;
	}
	
	public boolean isNull() {
		return object == null;
	}
	
}
