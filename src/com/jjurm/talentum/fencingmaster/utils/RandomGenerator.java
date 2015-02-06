package com.jjurm.talentum.fencingmaster.utils;

import java.util.Random;

public class RandomGenerator extends Random {
	private static final long serialVersionUID = 1L;
	
	public RandomGenerator() {
		super();
	}
	
	public RandomGenerator(long seed) {
		super(seed);
	}

	/**
	 * Returns a pseudo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min Minimum value
	 * @param max Maximum value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public int nextInt(int min, int max) {

	    return nextInt((max - min) + 1) + min;

	}
	
}
