package com.jjurm.talentum.fencingmaster.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ConsoleReader implements Runnable {

	private boolean running;

	private Commander commander;

	BufferedReader br;
	PrintWriter pw;

	Thread consoleReader;

	public ConsoleReader(Commander commander) {
		this.commander = commander;

		br = new BufferedReader(new InputStreamReader(System.in));
		pw = new PrintWriter(System.out, true);

		consoleReader = new Thread(this);
	}

	public synchronized void start() {
		if (running)
			return;
		running = true;
		consoleReader.start();
	}

	public synchronized void stop() {
		if (!running)
			return;
		running = false;
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pw.close();
	}

	@Override
	public void run() {
		Thread.currentThread().setName("ConsoleReader");

		while (running) {

			try {
				commander.process(br, pw);
			} catch (StreamCloseRequest e) {
				// Never close the console streams
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}

	}

}
