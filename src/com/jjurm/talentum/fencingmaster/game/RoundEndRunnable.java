package com.jjurm.talentum.fencingmaster.game;


public abstract class RoundEndRunnable implements Runnable {

	AbstractGame game;
	
	public RoundEndRunnable(AbstractGame game) {
		this.game = game;
	}
	
	@Override
	public final void run() {
		if (game.reacting.compareAndSet(true, false)) {
			run0();
			game.nextRound();
		}
	}
	
	protected abstract void run0();

}
