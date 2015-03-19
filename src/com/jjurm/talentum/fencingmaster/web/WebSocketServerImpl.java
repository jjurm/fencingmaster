package com.jjurm.talentum.fencingmaster.web;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONStringer;

import com.jjurm.talentum.fencingmaster.Config;
import com.jjurm.talentum.fencingmaster.game.Game;
import com.jjurm.talentum.fencingmaster.utils.ObjectHolder;

public class WebSocketServerImpl extends WebSocketServer {

	static AtomicInteger socketIndex = new AtomicInteger(1);
	Map<WebSocket, Integer> socketIndexes = new HashMap<WebSocket, Integer>();
	
	ObjectHolder<Game> gameHolder;
	
	public WebSocketServerImpl(ObjectHolder<Game> gameHolder) {
		super(new InetSocketAddress(Config.socketPort));
		this.gameHolder = gameHolder;
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		socketIndexes.put(conn, socketIndex.getAndIncrement());
		System.out.println("Accepted socket #" + socketIndexes.get(conn));
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println("Closed socket #" + socketIndexes.get(conn));
		socketIndexes.remove(conn);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		if (gameHolder.isNull()) {
			JSONStringer json = new FMJsonStringer();
			json.object();
			json.key("content").value("error");
			json.key("type").value("GameNotStarted");
			json.key("originalMessage").value(message);
			json.endObject();
			conn.send(json.toString());
		} else {
			gameHolder.get().socketMessage(conn, message);
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		System.out.println("Error occured");
		ex.printStackTrace();
	}
	
	public void distribute(String message) {
		for (WebSocket conn : connections()) {
			conn.send(message);
		}
	}
	
}
