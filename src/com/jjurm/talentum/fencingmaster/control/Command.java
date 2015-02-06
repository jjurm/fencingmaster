package com.jjurm.talentum.fencingmaster.control;

import java.io.BufferedReader;
import java.io.PrintWriter;

public interface Command {

	void process(String[] args, BufferedReader br, PrintWriter pw) throws StreamCloseRequest;

}
