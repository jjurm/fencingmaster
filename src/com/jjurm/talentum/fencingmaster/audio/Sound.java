package com.jjurm.talentum.fencingmaster.audio;

public enum Sound {

	BACK("back.wav"),
	BOOT("boot.wav"),
	CORRECT("correct.wav"),
	CORRECT2("correct2.wav"),
	SHUTDOWN("shutdown.wav"),
	START("start.wav"),
	TIMEOUT("timeout.wav"),
	WRONG("wrong.wav"),
	WRONG2("wrong2.wav"),
	WRONG3("wrong3.wav"),
	;
	
	static AudioPlayer player = AudioPlayer.getInstance();
	
	private String filename;
	
	private Sound(String filename) {
		this.filename = filename;
	}
	
	public String getFilename() {
		return this.filename;
	}
	
	public void play() {
		player.play(this);
	}
	
	public static void cacheSounds() {
		for (Sound sound : values()) {
			player.cache(sound);
		}
	}
	
}
