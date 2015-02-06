package com.jjurm.talentum.fencingmaster.utils;

import java.util.ArrayList;
import java.util.List;

public class Utils {

	public <T> List<T> substract(List<T> a, List<T> b) {
		List<T> result = new ArrayList<T>(a);
		result.removeAll(b);
		return result;
	}
	
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
	
}
