package de.unipotsdam.cs.groupplaner.domain;

import java.util.ArrayList;
import java.util.List;

public enum InvitationState {
	INVITED,    //
	ACCEPTED,    //
	REJECTED,    //
	REMOVED;

	public static Boolean isStateTransitionAllowed(final InvitationState oldState, final InvitationState newState) {
		List<Transition> allowedTransitions = new ArrayList<Transition>();
		allowedTransitions.add(new Transition(INVITED, ACCEPTED));
		allowedTransitions.add(new Transition(INVITED, REJECTED));
		allowedTransitions.add(new Transition(INVITED, REMOVED));
		allowedTransitions.add(new Transition(ACCEPTED, REMOVED));
		allowedTransitions.add(new Transition(REMOVED, INVITED));
		allowedTransitions.add(new Transition(REJECTED, INVITED));

		for (Transition allowedTransition : allowedTransitions) {
			if (allowedTransition.getFrom() == oldState && allowedTransition.getTo() == newState) {
				return true;
			}
		}
		return false;
	}

	private static class Transition {
		private final InvitationState from;
		private final InvitationState to;

		private Transition(InvitationState from, InvitationState to) {
			this.from = from;
			this.to = to;
		}

		public InvitationState getFrom() {
			return from;
		}

		public InvitationState getTo() {
			return to;
		}
	}
}
