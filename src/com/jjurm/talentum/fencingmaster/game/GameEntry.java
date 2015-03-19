package com.jjurm.talentum.fencingmaster.game;

import org.json.JSONWriter;

import com.jjurm.talentum.fencingmaster.web.WebObject;
import com.jjurm.talentum.fencingmaster.web.WebString;

public class GameEntry implements WebObject {
	
	final long id;
	final Type type;
	final GameConditions gameConditions;
	
	protected GameEntry(long id, Type type, GameConditions gameConditions) {
		this.id = id;
		this.type = type;
		this.gameConditions = gameConditions;
	}
	
	public GameEntry(Type type, GameConditions gameConditions) {
		this(History.newId(), type, gameConditions);
	}
	
	@Override
	public void toJson(JSONWriter json) {
		json.object()
			.key("id").value(id)
			.key("type").value(type)
			.key("conds").value(gameConditions)
			.endObject();
	}

	public long getId() {
		return this.id;
	}
	
	public Type getType() {
		return this.type;
	}

	public GameConditions getGameConditions() {
		return this.gameConditions;
	}
	
	public static enum Type implements WebString {
		
		GAME("game"), GAMEPART("gamepart");

		private String webName;
	
		private Type(String webName) {
			this.webName = webName;
		}
		
		@Override
		public String getWebName() {
			return this.webName;
		}
		
	}
	
}
