package de.unipotsdam.cs.groupplaner.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This AuthenticationProvider will validate a combination of username and password against the "old" PHP API running 
 * on musang.soft.cs.uni-potsdam.de. This API validates the username and password by performing a pseudo login on the 
 * PULS website via a web crawler mechanism.
 * This mechanism is highly unstable, error prone and slow! As soon as possible this AuthenticationProvider should be
 * replaced with a validation via LDAP or Shibboleth!
 */
@Component
public class PulsAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private PulsLoginValidator pulsLoginValidator;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String userEmail = authentication.getName();
		String password = authentication.getCredentials().toString();
		
		if (pulsLoginValidator.validateLogin(userEmail, password)) {
			List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
			grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
			return new UsernamePasswordAuthenticationToken(userEmail, password, grantedAuths);
		} else {
			return null;
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
