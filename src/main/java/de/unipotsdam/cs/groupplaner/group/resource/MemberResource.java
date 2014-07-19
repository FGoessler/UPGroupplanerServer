package de.unipotsdam.cs.groupplaner.group.resource;

import com.google.common.base.Preconditions;
import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.domain.InvitationState;
import de.unipotsdam.cs.groupplaner.domain.Member;
import de.unipotsdam.cs.groupplaner.group.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(PathConfig.GROUP_MEMBER_RESOURCE_PATH)
public class MemberResource {

	@Autowired
	private GroupService groupService;

	@RequestMapping(method = RequestMethod.GET)
	public Response getMembers(@PathVariable("id") final Integer groupId) {
		final List<Member> members = groupService.getActiveMembers(groupId);
		return Response.status(Response.Status.OK).entity(members).build();
	}

	@RequestMapping(value = "/{email}", method = RequestMethod.GET)
	public Response getMember(@PathVariable("id") final Integer groupId, @PathVariable("email") String email) {
		Member member = groupService.getMember(email, groupId);
		return Response.status(Response.Status.OK).entity(member).build();
	}

	@RequestMapping(method = RequestMethod.POST)
	@Consumes({MediaType.APPLICATION_JSON})
	public Response addMember(@PathVariable("id") final Integer groupId, @RequestBody final Map data) {
		Preconditions.checkNotNull(data.get("email"));

		Member newMember = groupService.inviteUser((String) data.get("email"), groupId);
		return Response.status(Response.Status.CREATED).entity(newMember).build();
	}

	@RequestMapping(value = "/{email}", method = RequestMethod.PUT)
	@Consumes({MediaType.APPLICATION_JSON})
	public Response updateMember(@PathVariable("id") final Integer groupId, @PathVariable("email") final String email, @RequestBody final Map data) {
		Preconditions.checkNotNull(data.get("invitationState"));

		Member modifiedMember = groupService.updateMemberStatus(email, groupId, InvitationState.valueOf((String) data.get("invitationState")));
		return Response.status(Response.Status.OK).entity(modifiedMember).build();
	}

	@RequestMapping(value = "/{email}", method = RequestMethod.DELETE)
	public Response deleteMember(@PathVariable("id") final Integer groupId, @PathVariable("email") String email) {
		groupService.updateMemberStatus(email, groupId, InvitationState.REMOVED);
		return Response.status(Response.Status.NO_CONTENT).build();
	}
}
