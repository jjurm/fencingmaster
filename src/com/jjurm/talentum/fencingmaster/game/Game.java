package com.jjurm.talentum.fencingmaster.game;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import org.java_websocket.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.jjurm.talentum.fencingmaster.GameEventsHandler;
import com.jjurm.talentum.fencingmaster.StateHolder;
import com.jjurm.talentum.fencingmaster.enums.Foot;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.enums.State;
import com.jjurm.talentum.fencingmaster.enums.UserAction;
import com.jjurm.talentum.fencingmaster.rgb.RGBController;
import com.jjurm.talentum.fencingmaster.web.FMJsonStringer;
import com.jjurm.talentum.fencingmaster.web.WebSocketServerImpl;
import com.sun.net.httpserver.HttpExchange;

public class Game implements GameEventsHandler, StateHolder<FMState> {

	final RGBController rgb;
	public final WebSocketServerImpl webSocketServer;

	FMState fmState;

	public final ScheduledExecutorService pool;
	public Future<?> shutdownFuture;
	public Object shutdownFutureMonitor = new Object();

	final History history = new History();
	
	protected boolean isPresentation = false;
	
	public final FootTracker footTracker = new FootTracker();

	public Game(RGBController rgb, WebSocketServerImpl webSocketServer) {
		this.rgb = rgb;
		this.webSocketServer = webSocketServer;

		pool = Executors.newScheduledThreadPool(5);
	}

	public RGBController getRGB() {
		return this.rgb;
	}

	protected void sendGameEntryEvent(GameEntry gameEntry) {
		JSONStringer json = new FMJsonStringer();
		json.object();
		json.key("content").value("event");
		json.key("type").value("entry");
		json.key("data");
		gameEntry.toJson(json);
		json.endObject();
		webSocketServer.distribute(json.toString());
	}
	
	protected void sendStateChangeEvent(FMState state) {
		JSONStringer json = new FMJsonStringer();
		json.object();
		json.key("content").value("event");
		json.key("type").value("statechange");
		json.key("state").value("");
		json.endObject();
		webSocketServer.distribute(json.toString());
	}
	
	public void sendBoomedEvent() {
		JSONStringer json = new FMJsonStringer();
		json.object();
		json.key("content").value("event");
		json.key("type").value("boomed");
		json.endObject();
		webSocketServer.distribute(json.toString());
	}

	public void historyAppend(GameEntry gameEntry) {
		history.append(gameEntry);
		sendGameEntryEvent(gameEntry);
	}

	public void historyReset() {
		history.reset();
	}

	public void terminate() {
		pool.shutdown();
	}

	@Override
	public void switchState(FMState newState) {
		FMState oldState = fmState;

		if (oldState != null)
			oldState.end();
		fmState = newState;
		System.out.println("Game: " + String.valueOf(oldState) + " -> " + String.valueOf(newState));
		sendStateChangeEvent(newState);
		if (newState != null)
			newState.begin();
	}

	@Override
	public void plateTouched(Side side) {
		System.out.println("Plate: " + side);
		if (fmState != null)
			pool.execute(() -> fmState.plateTouched(side));
	}

	@Override
	public void userAction(UserAction userAction, State state) {
		System.out.println("UserAction: " + userAction + ", " + state);
		if (userAction == UserAction.BACKBUTTON && state == State.NON_INTERACTING) {
			synchronized (shutdownFutureMonitor) {
				if (shutdownFuture != null) {
					shutdownFuture.cancel(false);
					shutdownFuture = null;
				}
			}
		}
		if (fmState != null)
			pool.execute(() -> fmState.userAction(userAction, state));
	}

	@Override
	public void footChanged(Foot foot, State state) {
		System.out.println("Foot: " + foot + ", " + state);
		footTracker.footChanged(foot, state);
		if (fmState != null)
			pool.execute(() -> fmState.footChanged(foot, state));
	}

	@Override
	public byte[] webRequest(HttpExchange e) throws IOException {
		String query = e.getRequestURI().getPath().substring(1);
		Path path;
		switch (query) {
		case "":
			path = Paths.get("web/index.html");
			return Files.readAllBytes(path);
		default:
			path = Paths.get("web/" + query);
			if (path.toFile().exists()) {
				return Files.readAllBytes(path);
			} else {
				return webRequestDefault(e);
			}
		}
	}

	public static byte[] webRequestDefault(HttpExchange e) {
		return ("Path: " + e.getRequestURI().getPath()).getBytes();
	}

	public void socketMessage(WebSocket conn, String message) {
		JSONObject obj = new JSONObject(message);
		String content = obj.getString("content");
		switch (content) {
		case "request":
			String type = obj.getString("type");
			switch (type) {
			case "history":
				int count;
				try {
					count = obj.getInt("count");
				} catch (JSONException e) {
					count = Integer.MAX_VALUE;
				}
				List<GameEntry> list = history.get(count);
				JSONStringer json = new FMJsonStringer();
				json.object();
				json.key("content").value("response");
				json.key("type").value("history");
				json.key("count").value(list.size());
				json.key("data").array();
				for (GameEntry entry : list) {
					entry.toJson(json);
				}
				json.endArray();
				json.endObject();
				conn.send(json.toString());
				break;
			}
		}
	}

	public boolean isPresentation() {
		return isPresentation;
	}

	public void setPresentation(boolean isPresentation) {
		this.isPresentation = isPresentation;
	}
	
}
