package de.unipotsdam.cs.groupplaner.domain;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Date;

@XmlRootElement
public class Member {
	private final String email;
	private final String name;
	private final String invitedBy;
	private final InvitationState invitationState;
	private final Date lastStateChange;

	public Member(final String email, final String name, final InvitationState invitationState, final Date lastStateChange, final String invitedBy) {
		this.email = email;
		this.name = name;
		this.invitedBy = invitedBy;
		this.invitationState = invitationState;
		this.lastStateChange = lastStateChange;
	}

	public Member(final String email, final String name, final InvitationState invitationState, final Date lastStateChange) {
		this(email, name, invitationState, lastStateChange, null);
	}

	public Member(final User user, final InvitationState invitationState, final Date lastStateChange) {
		this(user.getEmail(), user.getName(), invitationState, lastStateChange);
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public String getInvitedBy() {
		return invitedBy;
	}

	public InvitationState getInvitationState() {
		return invitationState;
	}

	public Date getLastStateChange() {
		return lastStateChange;
	}
}
