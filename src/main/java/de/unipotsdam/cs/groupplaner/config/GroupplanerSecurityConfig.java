package de.unipotsdam.cs.groupplaner.config;

import de.unipotsdam.cs.groupplaner.auth.PulsAuthenticationProvider;
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
			.authorizeRequests()
			.anyRequest().authenticated()
			.and()
			.httpBasic();
	}
}
