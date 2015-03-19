package com.jjurm.talentum.fencingmaster.audio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import sun.audio.AudioStream;

public class AudioPlayer {

	private static AudioPlayer instance;

	private static String FOLDER = "res/audio/";
	
	private AudioPlayer() {}

	public static AudioPlayer getInstance() {
		if (instance == null) {
			instance = new AudioPlayer();
		}
		return instance;
	}

	public void play(String filename) {
		try {
			InputStream in = new FileInputStream(FOLDER + filename);
			AudioStream as = new AudioStream(in);
			sun.audio.AudioPlayer.player.start(as);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*public void play(String filename) {
		try {
			URL url = getClass().getClassLoader().getResource("/audio/"+filename);
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}*/

	public void play(Sound sound) {
		play(sound.getFilename());
	}

	public void cache(String filename) {
		try (InputStream in = new FileInputStream(FOLDER + filename);
				AudioStream as = new AudioStream(in);) {
			as.getData();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void cache(Sound sound) {
		cache(sound.getFilename());
	}

}
