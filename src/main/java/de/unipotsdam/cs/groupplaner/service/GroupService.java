package de.unipotsdam.cs.groupplaner.service;

import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.auth.SecurityContextFacade;
import de.unipotsdam.cs.groupplaner.domain.Group;
import de.unipotsdam.cs.groupplaner.domain.InvitationState;
import de.unipotsdam.cs.groupplaner.domain.Member;
import de.unipotsdam.cs.groupplaner.repository.GroupRepository;
import de.unipotsdam.cs.groupplaner.repository.InvitationRepository;
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

		final Integer inviteId = invitationRepository.addUserToGroup(inviteeMail, securityContextFacade.getCurrentUserEmail(), groupId, InvitationState.INVITED);
		return invitationRepository.getMember(inviteId);
	}

	public Member updateMemberStatus(final String memberEmail, final Integer groupId, final InvitationState invitationState) {
		validateUsersPermissionForGroup(groupId);

		//TODO: check if transition is valid

		if (!invitationRepository.updateInviteStatus(memberEmail, groupId, invitationState)) {
			throw new EmptyResultDataAccessException(1);
		}
		
		return invitationRepository.getMember(memberEmail, groupId);
	}

	private void validateUsersPermissionForGroup(final Integer groupId) {
		if (!isUserMemberOfGroup(securityContextFacade.getCurrentUserEmail(), groupId)) {
			throw new AccessDeniedException("You are not a member of this group.");
		}
	}

	private Boolean isUserMemberOfGroup(final String useremail, final Integer groupId) {
		final List<Member> membersOfGroup = invitationRepository.getMembersOfGroup(groupId);

		for (Member member : membersOfGroup) {
			if (member.getEmail().equals(useremail)) {
				return true;
			}
		}

		return false;
	}
}
