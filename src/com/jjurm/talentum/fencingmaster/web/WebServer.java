package com.jjurm.talentum.fencingmaster.web;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.jjurm.talentum.fencingmaster.game.Game;
import com.jjurm.talentum.fencingmaster.utils.ObjectHolder;
import com.sun.net.httpserver.HttpServer;

public class WebServer {

	protected HttpServer server;
	
	public WebServer(ObjectHolder<Game> gameHolder) throws IOException {
		server = HttpServer.create(new InetSocketAddress(80), 0);
		server.createContext("/", new WebRequestGameRedirection(gameHolder));
	}
	
	public synchronized void start() {
		server.start();
	}
	
	public synchronized void stop() {
		server.stop(0);
	}
	
}
