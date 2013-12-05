package com.nasicion.autogestion_recovery.core;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.nasicion.autogestion_recovery.core.exceptions.LoginException;
import com.nasicion.autogestion_recovery.core.model.Fee;

public interface AutogestionConnector {
	HtmlPage doLogin(String user, String password) throws LoginException;
	
	List<Fee> getNextFees();
}
