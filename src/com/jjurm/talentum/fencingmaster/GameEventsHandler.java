package com.jjurm.talentum.fencingmaster;

import com.jjurm.talentum.fencingmaster.enums.Button;
import com.jjurm.talentum.fencingmaster.enums.Foot;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.enums.State;
import com.sun.net.httpserver.HttpExchange;

public interface GameEventsHandler {

	public abstract void buttonPressed(Button button, State state);
	public abstract void plateTouched(Side side);
	public abstract void footChanged(Foot foot, State state);
	
	public abstract String webRequest(HttpExchange e);
	
}
