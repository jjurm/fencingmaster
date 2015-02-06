package com.jjurm.talentum.fencingmaster.web;

import java.io.IOException;
import java.io.OutputStream;

import com.jjurm.talentum.fencingmaster.game.Game;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class WebRequestGameRedirection implements HttpHandler {

	protected final Game game;
	
	public WebRequestGameRedirection(Game game) {
		this.game = game;
	}
	
	@Override
	public void handle(HttpExchange e) throws IOException {
		String response = game.webRequest(e);
		
		byte[] bytes = response.getBytes();
		e.sendResponseHeaders(200, bytes.length);
		OutputStream os = e.getResponseBody();
		os.write(bytes);
		os.close();
	}

}
