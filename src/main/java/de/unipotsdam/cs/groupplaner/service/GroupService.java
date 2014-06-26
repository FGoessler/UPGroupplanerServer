package de.unipotsdam.cs.groupplaner.service;

import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.domain.Group;
import de.unipotsdam.cs.groupplaner.domain.InvitationState;
import de.unipotsdam.cs.groupplaner.domain.Member;

import java.util.List;

public interface GroupService {
	public ImmutableList<Group> getGroups();

	public Group getGroup(final Integer id);

	public Group createGroup(final String name);

	public Group updateGroup(final Group updatedGroup);

	public Member getMember(final String email, Integer groupId);

	public List<Member> getMembers(final Integer groupId);

	public Member inviteUser(final String inviteeMail, final Integer groupId);

	public Member updateMemberStatus(final String memberEmail, final Integer groupId, final InvitationState newInvitationState);
}
