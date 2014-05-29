package de.unipotsdam.cs.groupplaner.config;

import de.unipotsdam.cs.groupplaner.auth.PulsAuthenticationProvider;
import de.unipotsdam.cs.groupplaner.auth.RestfulBasicAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class GroupplanerSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private PulsAuthenticationProvider pulsAuthenticationProvider;
	
	@SuppressWarnings("SpringJavaAutowiringInspection")		//the IDE seems not to know about spring security in detail...
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(pulsAuthenticationProvider);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			// every resource is protected
			.authorizeRequests()
			.anyRequest().authenticated()
			.and()
			// http basic auth is enough for our purpose since this API should only be accessed via https!
			.httpBasic().authenticationEntryPoint(new RestfulBasicAuthenticationEntryPoint())	
			.and()
			// disable Cross-Site-Request-Forgery to allow REST-API like POST requests
			.csrf().disable();
	}
}
