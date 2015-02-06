package com.jjurm.talentum.fencingmaster.control;

public class StreamCloseRequest extends Exception {
	private static final long serialVersionUID = 1L;

	public StreamCloseRequest() {
		super("Request for stream close");
	}

}
