package de.unipotsdam.cs.groupplaner.auth;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class PulsLoginValidator {

	@Autowired
	Logger logger;

	public boolean validateLogin(final String userEmail, final String password) {
		Boolean authenticated = false;

		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(createLoginValidationUrl(userEmail, password));
			CloseableHttpResponse response1 = httpclient.execute(httpGet);

			try {
				System.out.println(response1.getStatusLine());
				HttpEntity entity1 = response1.getEntity();

				final String responseString = EntityUtils.toString(entity1);
				if (responseString.equals("Login erfolgreich")) {
					authenticated = true;
				}

				EntityUtils.consume(entity1);
			} finally {
				response1.close();
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getLocalizedMessage());
		}

		return authenticated;
	}

	private String createLoginValidationUrl(final String userEmail, final String password) {
		final String username = userEmail.replace("@uni-potsdam.de", "");
		return "https://musang.soft.cs.uni-potsdam.de/upapp/puls_request.php/puls_request.php?action=login&user=" + username + "&password=" + password + "&auth=H2LHXK5N9RDBXMA";
	}
}