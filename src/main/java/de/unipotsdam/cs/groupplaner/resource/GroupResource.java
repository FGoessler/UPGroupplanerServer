package de.unipotsdam.cs.groupplaner.resource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.unipotsdam.cs.groupplaner.auth.SecurityContextFacade;
import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.domain.Group;
import de.unipotsdam.cs.groupplaner.domain.User;
import de.unipotsdam.cs.groupplaner.repository.GroupRepository;
import de.unipotsdam.cs.groupplaner.repository.InvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Component
@Path(PathConfig.GROUP_RESOURCE_PATH)
@Produces({MediaType.APPLICATION_JSON})
public class GroupResource {

	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private InvitationRepository invitationRepository;
	@Autowired
	private SecurityContextFacade securityContextFacade;
	
	@GET
	public Response getGroups() {
		final String email = securityContextFacade.getCurrentUserEmail();
		final ImmutableList<Group> groupsOfUser = groupRepository.getGroupsOfUser(email);
		return Response.status(200).entity(Lists.newArrayList(groupsOfUser)).build();
	}

	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	public Response createGroup(@RequestBody final Map data) {
		final String email = securityContextFacade.getCurrentUserEmail();
		final Group newGroup = new Group((String) data.get("name"));
		final User user = new User(email, null);
		
		final Group createdGroup = groupRepository.getGroup(groupRepository.createGroup(newGroup));
		invitationRepository.inviteUserToGroup(user, user, createdGroup);
		invitationRepository.acceptInviteOfUserToGroup(user, createdGroup);
		
		return Response.status(201).entity(createdGroup).build();
	}

	@GET
	@Path("/{id}")
	public Response getGroup(@PathParam("id") final Integer id) {
		final Group group = groupRepository.getGroup(id);

		checkIfUserIsAMemberOfTheGroup(group);

		return Response.status(200).entity(group).build();
	}

	@DELETE
	@Path("/{id}")
	public Response deleteGroup(@PathParam("id") final Integer id) {
		checkIfUserIsAMemberOfTheGroup(new Group(id, null));
		
		groupRepository.deleteGroup(id);
		
		return Response.status(200).build();
	}

	@PUT
	@Path("/{id}")
	@Consumes({MediaType.APPLICATION_JSON})
	public Response updateGroup(@PathParam("id") final Integer id, @RequestBody final Map data) {
		final Group updatedGroup = new Group(id, (String) data.get("name"));

		checkIfUserIsAMemberOfTheGroup(updatedGroup);
		groupRepository.updateGroup(updatedGroup);
		
		return Response.status(200).entity(updatedGroup).build();
	}

	private void checkIfUserIsAMemberOfTheGroup(final Group group) {
		if(!invitationRepository.isUserMemberOfGroup(securityContextFacade.getCurrentUser(),group)) {
			throw new AccessDeniedException("You are not a member of this group!");
		}
	}
}
