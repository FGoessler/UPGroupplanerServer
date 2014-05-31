package de.unipotsdam.cs.groupplaner.resource;

import de.unipotsdam.cs.groupplaner.auth.SecurityContextFacade;
import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.domain.InvitationState;
import de.unipotsdam.cs.groupplaner.domain.Member;
import de.unipotsdam.cs.groupplaner.repository.InvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Component
@Path(PathConfig.GROUP_MEMBER_RESOURCE_PATH)
@Produces({MediaType.APPLICATION_JSON})
public class MemberResource {
	
	@Autowired
	private InvitationRepository invitationRepository;
	@Autowired
	private SecurityContextFacade securityContextFacade;
	
	@GET
	public Response getMembers(@PathParam("id") final Integer groupId) {
		final List<Member> members = checkAndGetMembersOfGroup(groupId);

		return Response.status(200).entity(members).build();
	}

	@GET
	@Path("/{email}")
	public Response getMember(@PathParam("id") final Integer groupId, @PathParam("email") String email) {
		Member member = invitationRepository.getMember(groupId, email);
		return Response.status(200).entity(member).build();
	}

	@POST
	@Path("/{email}")
	@Consumes({MediaType.APPLICATION_JSON})
	public Response addMember(@PathParam("id") final Integer groupId, @PathParam("email") String email) {
		checkAndGetMembersOfGroup(groupId);

		invitationRepository.inviteUserToGroup(email, securityContextFacade.getCurrentUserEmail(), groupId);
		
		Member modifiedMember = invitationRepository.getMember(groupId, email);
		return Response.status(201).entity(modifiedMember).build();
	}

	@PUT
	@Path("/{email}")
	@Consumes({MediaType.APPLICATION_JSON})
	public Response updateMember(@PathParam("id") final Integer groupId, @PathParam("email") final String email, @RequestBody final Map data) {
		String newStatus = (String) data.get("status");

		final Boolean updateSuccessful = invitationRepository.updateInviteStatus(email, groupId, InvitationState.valueOf(newStatus));
		if(!updateSuccessful) {
			throw new EmptyResultDataAccessException(1);	// no invite updated -> 404 no data
		}
		
		Member modifiedMember = invitationRepository.getMember(groupId, email);
		return Response.status(200).entity(modifiedMember).build();
	}

	@DELETE
	@Path("/{email}")
	public Response deleteMember(@PathParam("id") final Integer groupId, @PathParam("email") String email) {
		final List<Member> members = checkAndGetMembersOfGroup(groupId);
		
		if(!isUserAMemberOfTheGroup(members, email)) {
			throw new EmptyResultDataAccessException(1);
		}

		invitationRepository.updateInviteStatus(email, groupId, InvitationState.LEFT);

		return Response.status(204).build();
	}

	private List<Member> checkAndGetMembersOfGroup(final Integer groupId) {
		final List<Member> membersOfGroup = invitationRepository.getMembersOfGroup(groupId);

		final String currentUserMail = securityContextFacade.getCurrentUserEmail();
		if(!isUserAMemberOfTheGroup(membersOfGroup, currentUserMail)) {
			throw new AccessDeniedException("You are not a member of this group.");
		}
		
		return membersOfGroup;
	}

	private Boolean isUserAMemberOfTheGroup(final List<Member> membersOfGroup, final String currentUserMail) {
		Boolean currentUseIsMemberOfGroup = false;
		for(Member member : membersOfGroup) {
			if(member.getEmail().equals(currentUserMail)) {
				currentUseIsMemberOfGroup = true;
				break;
			}
		}
		return currentUseIsMemberOfGroup;
	}
}
