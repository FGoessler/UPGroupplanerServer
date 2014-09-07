package de.unipotsdam.cs.groupplaner.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User {
	private final String email;

	public User(final String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}
}
