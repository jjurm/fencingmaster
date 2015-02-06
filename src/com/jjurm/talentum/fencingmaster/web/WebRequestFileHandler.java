package com.jjurm.talentum.fencingmaster.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class WebRequestFileHandler implements HttpHandler {

	File file;
	
	int length = 0;
	List<byte[]> bytesArray = new ArrayList<byte[]>();
	
	public WebRequestFileHandler(File file) {
		this.file = file;
	}
	
	protected void loadResponse() {
		try (InputStream is = new FileInputStream(file)) {
			byte[] buffer = new byte[1024];
			while (is.read(buffer) != -1) {
				bytesArray.add(buffer);
				buffer = new byte[1024];
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void handle(HttpExchange e) throws IOException {
		e.sendResponseHeaders(200, this.length);
		OutputStream os = e.getResponseBody();
		
		for (byte[] bytes : bytesArray) {
			os.write(bytes);
		}
		
		os.close();
	}
	
}
