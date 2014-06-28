package de.unipotsdam.cs.groupplaner.service.impl;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.auth.SecurityContextFacade;
import de.unipotsdam.cs.groupplaner.domain.Group;
import de.unipotsdam.cs.groupplaner.domain.InvitationState;
import de.unipotsdam.cs.groupplaner.domain.Member;
import de.unipotsdam.cs.groupplaner.domain.User;
import de.unipotsdam.cs.groupplaner.repository.GroupRepository;
import de.unipotsdam.cs.groupplaner.repository.InvitationRepository;
import de.unipotsdam.cs.groupplaner.repository.UserRepository;
import de.unipotsdam.cs.groupplaner.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private InvitationRepository invitationRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SecurityContextFacade securityContextFacade;

	@Override
	public ImmutableList<Group> getGroups() {
		final String email = securityContextFacade.getCurrentUserEmail();
		return groupRepository.getGroupsOfUser(email);
	}

	@Override
	@PreAuthorize("@groupPermissionService.hasReadPermission(authentication, #id)")
	public Group getGroup(final Integer id) {
		return groupRepository.getGroup(id);
	}

	@Override
	public Group createGroup(final String name) {
		final Group newGroup = new Group(name);
		final Group createdGroup = groupRepository.getGroup(groupRepository.createGroup(newGroup));
		final String useremail = securityContextFacade.getCurrentUserEmail();
		invitationRepository.addUserToGroup(useremail, useremail, createdGroup.getId(), InvitationState.ACCEPTED);
		return createdGroup;
	}

	@Override
	@PreAuthorize("@groupPermissionService.hasWritePermission(authentication, #updatedGroup)")
	public Group updateGroup(final Group updatedGroup) {
		if (!groupRepository.updateGroup(updatedGroup)) {
			throw new EmptyResultDataAccessException(1);
		}
		return groupRepository.getGroup(updatedGroup.getId());
	}

	@Override
	@PreAuthorize("@groupPermissionService.hasReadPermission(authentication, #groupId)")
	public Member getMember(final String email, final Integer groupId) {
		return invitationRepository.getMember(email, groupId);
	}

	@Override
	@PreAuthorize("@groupPermissionService.hasReadPermission(authentication, #groupId)")
	public List<Member> getMembers(final Integer groupId) {
		return invitationRepository.getMembersOfGroup(groupId);
	}

	@Override
	@PreAuthorize("@groupPermissionService.hasWritePermission(authentication, #groupId)")
	public Member inviteUser(final String inviteeMail, final Integer groupId) {

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

	@Override
	@PreAuthorize("@groupPermissionService.hasWriteOrPartialWritePermission(authentication, #groupId, #memberEmail)")
	public Member updateMemberStatus(final String memberEmail, final Integer groupId, final InvitationState newInvitationState) {
		final Member currentMemberData = invitationRepository.getMember(memberEmail, groupId);
		final InvitationState currentInvitationState = currentMemberData.getInvitationState();
		if (!InvitationState.isStateTransitionAllowed(currentInvitationState, newInvitationState)) {
			throw new IllegalStateException("A transition from '" + currentInvitationState.toString() + "' to '" + newInvitationState.toString() + "' is not allowed.");
		}

		if (!invitationRepository.updateInviteStatus(memberEmail, groupId, newInvitationState)) {
			throw new EmptyResultDataAccessException(1);
		}

		if (!hasGroupActiveMembers(groupId)) {
			groupRepository.deleteGroup(groupId);
		}

		return currentMemberData;
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

	private Boolean hasGroupActiveMembers(final Integer groupId) {
		final List<Member> members = invitationRepository.getMembersOfGroup(groupId);
		final Collection<Member> activeMembers = Collections2.filter(members, new Predicate<Member>() {
			@Override
			public boolean apply(Member member) {
				return member.getInvitationState() == InvitationState.ACCEPTED;
			}
		});
		return activeMembers.size() > 0;
	}

}
