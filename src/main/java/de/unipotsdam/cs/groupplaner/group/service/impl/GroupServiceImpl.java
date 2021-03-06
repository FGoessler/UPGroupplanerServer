package de.unipotsdam.cs.groupplaner.group.service.impl;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.unipotsdam.cs.groupplaner.auth.SecurityContextFacade;
import de.unipotsdam.cs.groupplaner.domain.Group;
import de.unipotsdam.cs.groupplaner.domain.InvitationState;
import de.unipotsdam.cs.groupplaner.domain.Member;
import de.unipotsdam.cs.groupplaner.domain.User;
import de.unipotsdam.cs.groupplaner.group.dao.GroupDAO;
import de.unipotsdam.cs.groupplaner.group.dao.InvitationDAO;
import de.unipotsdam.cs.groupplaner.group.service.GroupService;
import de.unipotsdam.cs.groupplaner.user.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

	@Autowired
	private GroupDAO groupDAO;
	@Autowired
	private InvitationDAO invitationDAO;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private SecurityContextFacade securityContextFacade;

	@Override
	public ImmutableList<Group> getGroups() {
		final String email = securityContextFacade.getCurrentUserEmail();
		return groupDAO.getGroupsOfUser(email);
	}

	@Override
	@PreAuthorize("@groupPermissionService.hasReadPermission(authentication, #id)")
	public Group getGroup(final Integer id) {
		return groupDAO.getGroup(id);
	}

	@Override
	public Group createGroup(final String name) {
		final Group newGroup = new Group(name);
		final Group createdGroup = groupDAO.getGroup(groupDAO.createGroup(newGroup));
		final String useremail = securityContextFacade.getCurrentUserEmail();
		invitationDAO.addUserToGroup(useremail, useremail, createdGroup.getId(), InvitationState.ACCEPTED);
		return createdGroup;
	}

	@Override
	@PreAuthorize("@groupPermissionService.hasWritePermission(authentication, #updatedGroup.getId())")
	public Group updateGroup(final Group updatedGroup) {
		if (!groupDAO.updateGroup(updatedGroup)) {
			throw new EmptyResultDataAccessException(1);
		}
		return groupDAO.getGroup(updatedGroup.getId());
	}

	@Override
	@PreAuthorize("@groupPermissionService.hasReadPermission(authentication, #groupId)")
	public Member getMember(final String email, final Integer groupId) {
		return invitationDAO.getMember(email, groupId);
	}

	@Override
	@PreAuthorize("@groupPermissionService.hasReadPermission(authentication, #groupId)")
	public List<Member> getActiveMembers(final Integer groupId) {
		final List<Member> members = invitationDAO.getMembersOfGroup(groupId);
		return Lists.newArrayList(Collections2.filter(members, new Predicate<Member>() {
			@Override
			public boolean apply(Member member) {
				return member.getInvitationState() == InvitationState.ACCEPTED || member.getInvitationState() == InvitationState.INVITED;
			}
		}));
	}

	@Override
	@PreAuthorize("@groupPermissionService.hasWritePermission(authentication, #groupId)")
	public Member inviteUser(final String inviteeMail, final Integer groupId) {

		// check if invitee is already member
		if (isUserMemberOfGroup(inviteeMail, groupId)) {
			throw new IllegalArgumentException("A person with this email address is already invited / a member of this group!");
		}

		// if the user does not exist in the db yet add him/her also to user table
		if (userDAO.getUser(inviteeMail) == null) {
			userDAO.createUser(new User(inviteeMail));
		}

		// check whether the user already had an invite but rejected / was removed
		if (isUserRejectedOrRemovedFromGroup(inviteeMail, groupId)) {
			return updateMemberStatus(inviteeMail, groupId, InvitationState.INVITED);
		} else {
			final Integer inviteId = invitationDAO.addUserToGroup(inviteeMail, securityContextFacade.getCurrentUserEmail(), groupId, InvitationState.INVITED);
			return invitationDAO.getMember(inviteId);
		}
	}

	@Override
	@PreAuthorize("@groupPermissionService.hasWriteOrPartialWritePermission(authentication, #groupId, #memberEmail)")
	public Member updateMemberStatus(final String memberEmail, final Integer groupId, final InvitationState newInvitationState) {
		final Member currentMemberData = invitationDAO.getMember(memberEmail, groupId);
		final InvitationState currentInvitationState = currentMemberData.getInvitationState();
		if (!InvitationState.isStateTransitionAllowed(currentInvitationState, newInvitationState)) {
			throw new IllegalStateException("A transition from '" + currentInvitationState.toString() + "' to '" + newInvitationState.toString() + "' is not allowed.");
		}

		if (!invitationDAO.updateInviteStatus(memberEmail, groupId, newInvitationState)) {
			throw new EmptyResultDataAccessException(1);
		}

		if (!hasGroupActiveMembers(groupId)) {
			groupDAO.deleteGroup(groupId);
		}

		return currentMemberData;
	}

	private Boolean isUserMemberOfGroup(final String useremail, final Integer groupId) {
		final List<Member> membersOfGroup = invitationDAO.getMembersOfGroup(groupId);

		for (Member member : membersOfGroup) {
			if (member.getEmail().equals(useremail) && (member.getInvitationState().equals(InvitationState.ACCEPTED) || member.getInvitationState().equals(InvitationState.INVITED))) {
				return true;
			}
		}

		return false;
	}

	private Boolean isUserRejectedOrRemovedFromGroup(final String useremail, final Integer groupId) {
		final List<Member> membersOfGroup = invitationDAO.getMembersOfGroup(groupId);

		for (Member member : membersOfGroup) {
			if (member.getEmail().equals(useremail) && (member.getInvitationState().equals(InvitationState.REJECTED) || member.getInvitationState().equals(InvitationState.REMOVED))) {
				return true;
			}
		}

		return false;
	}

	private Boolean hasGroupActiveMembers(final Integer groupId) {
		final List<Member> members = invitationDAO.getMembersOfGroup(groupId);
		final Collection<Member> activeMembers = Collections2.filter(members, new Predicate<Member>() {
			@Override
			public boolean apply(Member member) {
				return member.getInvitationState() == InvitationState.ACCEPTED;
			}
		});
		return activeMembers.size() > 0;
	}

}
