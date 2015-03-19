package com.jjurm.talentum.fencingmaster.utils;

import java.util.ArrayList;
import java.util.List;

public class Utils {

	public static final RandomGenerator rand = new RandomGenerator();

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

	public static <T> void shuffleArray(T[] array) {
		RandomGenerator rnd = new RandomGenerator();
		for (int i = array.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			T a = array[index];
			array[index] = array[i];
			array[i] = a;
		}
	}

}
