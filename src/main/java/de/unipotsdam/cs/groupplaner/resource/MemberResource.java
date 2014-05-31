package de.unipotsdam.cs.groupplaner.resource;

import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.domain.InvitationState;
import de.unipotsdam.cs.groupplaner.domain.Member;
import de.unipotsdam.cs.groupplaner.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
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
	private GroupService groupService;
	
	@GET
	public Response getMembers(@PathParam("id") final Integer groupId) {
		final List<Member> members = groupService.getMembers(groupId);
		return Response.status(200).entity(members).build();
	}

	@GET
	@Path("/{email}")
	public Response getMember(@PathParam("id") final Integer groupId, @PathParam("email") String email) {
		Member member = groupService.getMember(email, groupId);
		return Response.status(200).entity(member).build();
	}

	@POST
	@Path("/{email}")
	@Consumes({MediaType.APPLICATION_JSON})
	public Response addMember(@PathParam("id") final Integer groupId, @PathParam("email") String email) {
		Member newMember = groupService.inviteUser(email, groupId);
		return Response.status(201).entity(newMember).build();
	}

	@PUT
	@Path("/{email}")
	@Consumes({MediaType.APPLICATION_JSON})
	public Response updateMember(@PathParam("id") final Integer groupId, @PathParam("email") final String email, @RequestBody final Map data) {
		String newStatus = (String) data.get("status");
		Member modifiedMember = groupService.updateMemberStatus(email, groupId, InvitationState.valueOf(newStatus));
		return Response.status(200).entity(modifiedMember).build();
	}

	@DELETE
	@Path("/{email}")
	public Response deleteMember(@PathParam("id") final Integer groupId, @PathParam("email") String email) {
		groupService.updateMemberStatus(email, groupId, InvitationState.LEFT);
		return Response.status(204).build();
	}
}
