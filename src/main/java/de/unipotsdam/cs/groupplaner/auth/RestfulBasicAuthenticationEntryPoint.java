package de.unipotsdam.cs.groupplaner.auth;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This EntryPoint is necessary to avoid sending a "WWW-Authenticate"-Header which is problematic with REST APIs.
 */
public class RestfulBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		response.addHeader("Access-Control-Allow-Origin", "null");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		setRealmName("Groupplaner");
		super.afterPropertiesSet();
	}

}
