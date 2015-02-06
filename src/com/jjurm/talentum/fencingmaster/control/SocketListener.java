package com.jjurm.talentum.fencingmaster.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.jjurm.talentum.fencingmaster.Config;

public class SocketListener implements Runnable {

	private boolean running;

	Commander commander;

	ServerSocket serverSocket;
	Thread serverSocketThread;
	List<OutputStream> outputs = new ArrayList<OutputStream>();

	public SocketListener(Commander commander) {
		this.commander = commander;
		serverSocketThread = new Thread(this);
	}

	public synchronized void start() {
		if (running)
			return;
		running = true;
		serverSocketThread.start();
	}

	public synchronized void stop() {
		if (!running)
			return;
		running = false;
		try {
			if (serverSocket != null)
				serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeToAll(byte[] data) throws IOException {
		for (OutputStream output : outputs) {
			output.write(data);
			output.flush();
		}
	}

	@Override
	public void run() {
		Thread.currentThread().setName("ServerSocketThread");

		try {
			serverSocket = new ServerSocket(Config.socketPort, 3, null);
		} catch (BindException e) {
			System.out.println("Port " + Config.socketPort + " is already in use, terminating SocketListener");
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			while (running) {
				Socket socket = serverSocket.accept();
				SocketThread thread = new SocketThread(commander, this, socket);
				thread.start();
			}
		} catch (IOException e) {
			// socket closed
		}

	}

	static class SocketThread extends Thread {

		static AtomicInteger atomicInteger = new AtomicInteger(1);

		Commander commander;
		SocketListener socketListener;
		Socket socket;

		int socketIndex;

		public SocketThread(Commander commander, SocketListener socketListener, Socket socket) {
			this.commander = commander;
			this.socketListener = socketListener;
			this.socket = socket;

			socketIndex = atomicInteger.getAndIncrement();
		}

		@Override
		public void run() {
			setName("SocketThread-" + socketIndex);
			
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
					OutputStream os = socket.getOutputStream();
					PrintWriter pw = new PrintWriter(os, true)) {

				socketListener.outputs.add(os);
				System.out.println("Accepted socket #" + socketIndex);
				
				loop: while (true) {

					try {
						commander.process(br, pw);
					} catch (StreamCloseRequest scr) {
						System.out.println("Closed socket #" + socketIndex);
						socketListener.outputs.remove(os);
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						break loop;
					}

				}
				

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
