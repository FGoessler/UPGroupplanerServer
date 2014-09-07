package de.unipotsdam.cs.groupplaner.domain;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Date;

@XmlRootElement
public class Member {
	private final String email;
	private final String invitedBy;
	private final InvitationState invitationState;
	private final Date lastStateChange;

	public Member(final String email, final InvitationState invitationState, final Date lastStateChange, final String invitedBy) {
		this.email = email;
		this.invitedBy = invitedBy;
		this.invitationState = invitationState;
		this.lastStateChange = lastStateChange;
	}

	public Member(final String email, final InvitationState invitationState, final Date lastStateChange) {
		this(email, invitationState, lastStateChange, null);
	}

	public Member(final User user, final InvitationState invitationState, final Date lastStateChange) {
		this(user.getEmail(), invitationState, lastStateChange);
	}

	public String getEmail() {
		return email;
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
