package com.nasicion.autogestion_recovery.core.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.nasicion.autogestion_recovery.core.AutogestionConnector;
import com.nasicion.autogestion_recovery.core.exceptions.LoginException;
import com.nasicion.autogestion_recovery.core.model.Fee;

@Service
public class AutogestionConnectorImpl implements AutogestionConnector {

	@Value("${autogestion_recovery.login.url}")
	private String loginUrl;
	@Value("${autogestion_recovery.login.user}")
	private String loginUser;
	@Value("${autogestion_recovery.login.password}")
	private String loginPassword;

	@Value("${autogestion_recovery.fees.url}")
	private String feesUrl;

	private static final String USER_LOGIN_ID = "W0002_USULOG";
	private static final String USER_PASSWORD_ID = "W0002_USUPASS";
	private static final String LOGIN_BUTTON_NAME = "W0002BTN_LOGIN";
	private static final String CLOSE_SESSION_SPAN_ID = "W0002CERRARSESION";

	public HtmlPage doLogin(String user, String password) throws LoginException {
		HtmlPage mainPage = null;
		WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_8);
		try {
			HtmlPage loginPage = webClient.getPage(loginUrl);

			HtmlTextInput userField =  (HtmlTextInput) loginPage.getElementById(USER_LOGIN_ID);
			HtmlPasswordInput passwordField =  (HtmlPasswordInput) loginPage.getElementById(USER_PASSWORD_ID);

			userField.setValueAttribute(user);
			passwordField.setValueAttribute(password);

			HtmlSubmitInput loginButton = loginPage.getElementByName(LOGIN_BUTTON_NAME);

			mainPage = loginButton.click();

			HtmlSpan closeSessionSpan = (HtmlSpan) mainPage.getElementById(CLOSE_SESSION_SPAN_ID);
			if(!closeSessionSpan.isDisplayed()) {
				throw new LoginException();
			}

		} catch (IOException e) {
			//FIXME mejorar manejo de excepciones
			e.printStackTrace();
		}
		
		return mainPage;
	}

	public List<Fee> getNextFees() {
		List<Fee> fees = new LinkedList<Fee>();
		HtmlPage feesPage = null;
		try {
			HtmlPage mainPage = null;
			mainPage = doLogin(loginUser, loginPassword);
			WebClient wc = mainPage.getWebClient();
			feesPage = wc.getPage(feesUrl);
		} catch (LoginException e) {
			//FIXME mejorar manejo de excepciones
			e.printStackTrace();
		} catch (IOException e) {
			//FIXME mejorar manejo de excepciones
			e.printStackTrace();
		}

		if(feesPage != null) {
			HtmlAnchor link = feesPage.getFirstByXPath("/html/body/form/p[2]/table/tbody/tr/td/table[2]/tbody/tr/td/p/table/tbody/tr/td/p/table/tbody/tr[2]/td[10]/a");
			try {
				feesPage = link.click();
			} catch (IOException e) {
				//FIXME
				e.printStackTrace();
			}
			HtmlTable feesTable = (HtmlTable) feesPage.getElementById("GRID1");
		}

		return fees;
	}

}
