package com.jjurm.talentum.fencingmaster.rgb;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class GroupedFuture<T> implements Future<T> {

	private List<Future<?>> futures;
	
	public GroupedFuture(List<Future<?>> futures) {
		this.futures = futures;
	}
	
	public boolean iterate(FutureAction action) {
		boolean result = true;
		for (Future<?> future : futures) {
			result &= action.action(future);
		}
		return result;
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return iterate(future -> future.cancel(mayInterruptIfRunning));
	}

	@Override
	public boolean isCancelled() {
		return iterate(future -> future.isCancelled());
	}

	@Override
	public boolean isDone() {
		return iterate(future -> future.isDone());
	}

	@Override
	public T get() {
		return null;
	}

	@Override
	public T get(long timeout, TimeUnit unit) {
		return null;
	}
	
	
	private static interface FutureAction {
		
		public boolean action(Future<?> future);
		
	}

}
