package com.jjurm.talentum.fencingmaster.web;

import java.io.IOException;
import java.io.OutputStream;

import com.jjurm.talentum.fencingmaster.game.Game;
import com.jjurm.talentum.fencingmaster.utils.ObjectHolder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class WebRequestGameRedirection implements HttpHandler {

	protected final ObjectHolder<Game> gameHolder;
	
	public WebRequestGameRedirection(ObjectHolder<Game> gameHolder) {
		this.gameHolder = gameHolder;
	}
	
	@Override
	public void handle(HttpExchange e) throws IOException {
		byte[] response = null;
		if (gameHolder.isNull()) {
			response = Game.webRequestDefault(e);
		} else {
			try {
				response = gameHolder.get().webRequest(e);
			} catch (IOException ex) {
				ex.printStackTrace();
				response = Game.webRequestDefault(e);
			}
		}
		
		e.sendResponseHeaders(200, response.length);
		OutputStream os = e.getResponseBody();
		os.write(response);
		os.close();
	}

}
