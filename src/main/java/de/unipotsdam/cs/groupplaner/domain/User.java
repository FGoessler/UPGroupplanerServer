package de.unipotsdam.cs.groupplaner.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User {
	private final String email;
	private final String name;

	public User(final String email, final String name) {
		this.email = email;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}
}
