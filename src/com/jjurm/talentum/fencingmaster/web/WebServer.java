package com.jjurm.talentum.fencingmaster.web;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.jjurm.talentum.fencingmaster.game.Game;
import com.sun.net.httpserver.HttpServer;

public class WebServer {

	protected final Game game;
	
	protected HttpServer server;
	
	public WebServer(Game game) throws IOException {
		this.game = game;
		server = HttpServer.create(new InetSocketAddress(80), 0);
		server.createContext("/fm", new WebRequestGameRedirection(game));
		server.start();
	}
	
}
