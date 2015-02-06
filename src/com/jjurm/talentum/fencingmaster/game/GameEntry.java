package com.jjurm.talentum.fencingmaster.game;

public class GameEntry {
	
	protected int reactionTime = -1;
	protected boolean plateOk = false;
	protected boolean footOk = false;
	
	protected boolean ok = false;
	
	public GameEntry(int reactionTime, boolean plateOk, boolean footOk, boolean ok) {
		this.reactionTime = reactionTime;
		this.plateOk = plateOk;
		this.footOk = footOk;
		
		this.ok = ok;
	}

	public int getReactionTime() {
		return reactionTime;
	}

	public boolean isPlateOk() {
		return plateOk;
	}

	public boolean isFootOk() {
		return footOk;
	}

	public boolean isOk() {
		return ok;
	}

}
