package com.jjurm.talentum.fencingmaster;

import java.io.IOException;

import com.jjurm.talentum.fencingmaster.enums.UserAction;
import com.jjurm.talentum.fencingmaster.enums.Foot;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.enums.State;
import com.sun.net.httpserver.HttpExchange;

public interface GameEventsHandler {

	public abstract void userAction(UserAction userAction, State state);
	public abstract void plateTouched(Side side);
	public abstract void footChanged(Foot foot, State state);
	
	public abstract byte[] webRequest(HttpExchange e) throws IOException;
	
}
