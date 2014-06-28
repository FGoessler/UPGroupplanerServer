package de.unipotsdam.cs.groupplaner.auth;

import de.unipotsdam.cs.groupplaner.domain.InvitationState;
import de.unipotsdam.cs.groupplaner.domain.Member;
import de.unipotsdam.cs.groupplaner.repository.InvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

/**
 * This service is used to determine whether a user has access to the data of a particular group.
 * Use it with Spring Security by adding @PreAuthorize Annotations like:
 * "@PreAuthorize("@groupPermissionService.hasReadPermission(authentication, #groupId)")"
 */
@Service(value = "groupPermissionService")
public class GroupPermissionService {

	@Autowired
	private InvitationRepository invitationRepository;

	/**
	 * A user has read permissions if he/she is a member of the group or was invited to the group.
	 */
	public Boolean hasReadPermission(final Principal auth, final Integer groupId) {
		return isUserMemberOfGroupOrInvitedToGroup(auth.getName(), groupId);
	}

	/**
	 * A user has write permissions if he/she is a member.
	 */
	public Boolean hasWritePermission(final Principal auth, final Integer groupId) {
		return isUserMemberOfGroup(auth.getName(), groupId);
	}

	/**
	 * A user has partial write permissions for resources of a group that impact him.
	 * Example: If a user is invited to a group he can update its own invitation state but not others.
	 */
	public Boolean hasWriteOrPartialWritePermission(final Principal auth, final Integer groupId, final String resourceOwner) {
		final String userName = auth.getName();
		if (!isUserMemberOfGroup(userName, groupId)) {
			return isUserMemberOfGroupOrInvitedToGroup(userName, groupId) && resourceOwner.equals(userName);
		} else {
			return true;
		}
	}

	private Boolean isUserMemberOfGroup(final String useremail, final Integer groupId) {
		final List<Member> membersOfGroup = invitationRepository.getMembersOfGroup(groupId);

		for (Member member : membersOfGroup) {
			if (member.getEmail().equals(useremail) && member.getInvitationState().equals(InvitationState.ACCEPTED)) {
				return true;
			}
		}

		return false;
	}

	private Boolean isUserMemberOfGroupOrInvitedToGroup(final String useremail, final Integer groupId) {
		final List<Member> membersOfGroup = invitationRepository.getMembersOfGroup(groupId);

		for (Member member : membersOfGroup) {
			if (member.getEmail().equals(useremail) && (member.getInvitationState().equals(InvitationState.ACCEPTED) || member.getInvitationState().equals(InvitationState.INVITED))) {
				return true;
			}
		}

		return false;
	}

}
