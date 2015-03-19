package com.jjurm.talentum.fencingmaster.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class History {

	private static AtomicInteger lastId = new AtomicInteger(0);
	
	List<GameEntry> history = new ArrayList<GameEntry>();

	public History() {

	}
	
	public static int newId() {
		return lastId.incrementAndGet();
	}

	public void reset() {
		history.clear();
	}

	public void append(GameEntry gameEntry) {
		history.add(0, gameEntry);
	}
	
	public List<GameEntry> get(int count) {
		return Collections.unmodifiableList(history.subList(0, Math.min(count, history.size())));
	}

}
