package de.unipotsdam.cs.groupplaner.service;

import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.auth.SecurityContextFacade;
import de.unipotsdam.cs.groupplaner.domain.Group;
import de.unipotsdam.cs.groupplaner.domain.InvitationState;
import de.unipotsdam.cs.groupplaner.domain.Member;
import de.unipotsdam.cs.groupplaner.domain.User;
import de.unipotsdam.cs.groupplaner.repository.GroupRepository;
import de.unipotsdam.cs.groupplaner.repository.InvitationRepository;
import de.unipotsdam.cs.groupplaner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private InvitationRepository invitationRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SecurityContextFacade securityContextFacade;

	public ImmutableList<Group> getGroups() {
		final String email = securityContextFacade.getCurrentUserEmail();
		return groupRepository.getGroupsOfUser(email);
	}

	public Group getGroup(final Integer id) {
		validateUsersPermissionForGroup(id);
		return groupRepository.getGroup(id);
	}

	public Group createGroup(final String name) {
		final Group newGroup = new Group(name);
		final Group createdGroup = groupRepository.getGroup(groupRepository.createGroup(newGroup));
		final String useremail = securityContextFacade.getCurrentUserEmail();
		invitationRepository.addUserToGroup(useremail, useremail, createdGroup.getId(), InvitationState.ACCEPTED);
		return createdGroup;
	}

	public Group updateGroup(final Group updatedGroup) {
		validateUsersPermissionForGroup(updatedGroup.getId());
		if (!groupRepository.updateGroup(updatedGroup)) {
			throw new EmptyResultDataAccessException(1);
		}
		return groupRepository.getGroup(updatedGroup.getId());
	}

	public void deleteGroup(final Integer groupId) {
		validateUsersPermissionForGroup(groupId);
		if (!groupRepository.deleteGroup(groupId)) {
			throw new EmptyResultDataAccessException(1);
		}
	}

	public Member getMember(final String email, final Integer groupId) {
		validateUsersPermissionForGroup(groupId);
		return invitationRepository.getMember(email, groupId);
	}

	public List<Member> getMembers(final Integer groupId) {
		validateUsersPermissionForGroup(groupId);
		return invitationRepository.getMembersOfGroup(groupId);
	}

	public Member inviteUser(final String inviteeMail, final Integer groupId) {
		validateUsersPermissionForGroup(groupId);

		// check if invitee is already member
		if (isUserMemberOfGroup(inviteeMail, groupId)) {
			throw new IllegalArgumentException("A person with this email address is already invited / a member of this group!");
		}

		// if the user does not exist in the db yet add him/her also to user table
		if (userRepository.getUser(inviteeMail) == null) {
			userRepository.createUser(new User(inviteeMail, ""));
		}

		// check whether the user already had an invite but rejected / was removed
		if (isUserRejectedOrRemovedFromGroup(inviteeMail, groupId)) {
			return updateMemberStatus(inviteeMail, groupId, InvitationState.INVITED);
		} else {
			final Integer inviteId = invitationRepository.addUserToGroup(inviteeMail, securityContextFacade.getCurrentUserEmail(), groupId, InvitationState.INVITED);
			return invitationRepository.getMember(inviteId);
		}
	}

	public Member updateMemberStatus(final String memberEmail, final Integer groupId, final InvitationState newInvitationState) {
		validateUsersPermissionForGroup(groupId);

		final Member currentMemberData = invitationRepository.getMember(memberEmail, groupId);
		final InvitationState currentInvitationState = currentMemberData.getInvitationState();
		if (!InvitationState.isStateTransitionAllowed(currentInvitationState, newInvitationState)) {
			throw new IllegalStateException("A transition from '" + currentInvitationState.toString() + "' to '" + newInvitationState.toString() + "' is not allowed.");
		}

		if (!invitationRepository.updateInviteStatus(memberEmail, groupId, newInvitationState)) {
			throw new EmptyResultDataAccessException(1);
		}

		return currentMemberData;
	}

	public void validateUsersPermissionForGroup(final Integer groupId) {
		if (!isUserMemberOfGroup(securityContextFacade.getCurrentUserEmail(), groupId)) {
			throw new AccessDeniedException("You are not a member of this group.");
		}
	}

	private Boolean isUserMemberOfGroup(final String useremail, final Integer groupId) {
		final List<Member> membersOfGroup = invitationRepository.getMembersOfGroup(groupId);

		for (Member member : membersOfGroup) {
			if (member.getEmail().equals(useremail) && (member.getInvitationState().equals(InvitationState.ACCEPTED) || member.getInvitationState().equals(InvitationState.INVITED))) {
				return true;
			}
		}

		return false;
	}

	private Boolean isUserRejectedOrRemovedFromGroup(final String useremail, final Integer groupId) {
		final List<Member> membersOfGroup = invitationRepository.getMembersOfGroup(groupId);

		for (Member member : membersOfGroup) {
			if (member.getEmail().equals(useremail) && (member.getInvitationState().equals(InvitationState.REJECTED) || member.getInvitationState().equals(InvitationState.REMOVED))) {
				return true;
			}
		}

		return false;
	}
}
