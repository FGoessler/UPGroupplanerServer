package de.unipotsdam.cs.groupplaner.domain;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public enum InvitationState {
	INVITED,    //
	ACCEPTED,    //
	REJECTED,    //
	LEFT;

	public static Boolean isStateTransitionAllowed(final InvitationState oldState, final InvitationState newState) {
		List<Pair<InvitationState, InvitationState>> allowedTransitions = new ArrayList<Pair<InvitationState, InvitationState>>();
		allowedTransitions.add(new Pair<InvitationState, InvitationState>(INVITED, ACCEPTED));        // from INVITED to ACCEPTED
		allowedTransitions.add(new Pair<InvitationState, InvitationState>(INVITED, REJECTED));        // from INVITED to REJECTED
		allowedTransitions.add(new Pair<InvitationState, InvitationState>(ACCEPTED, LEFT));            // from ACCEPTED to LEFT

		for (Pair<InvitationState, InvitationState> allowedTransition : allowedTransitions) {
			if (allowedTransition.getKey() == oldState && allowedTransition.getValue() == newState) {
				return true;
			}
		}
		return false;
	}
}
