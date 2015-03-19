package com.jjurm.talentum.fencingmaster.web;

import org.json.JSONException;
import org.json.JSONStringer;
import org.json.JSONWriter;

public class FMJsonStringer extends JSONStringer {
	
	@Override
	public JSONWriter value(Object object) throws JSONException {
		if (object instanceof WebObject) {
			((WebObject) object).toJson(this);
		} else if (object instanceof WebString) {
			value(((WebString) object).getWebName());
		} else {
			super.value(object);
		}			
		return this;
	}

}
