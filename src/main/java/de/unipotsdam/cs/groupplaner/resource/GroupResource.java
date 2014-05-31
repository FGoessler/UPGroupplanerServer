package de.unipotsdam.cs.groupplaner.resource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.domain.Group;
import de.unipotsdam.cs.groupplaner.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
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
	private GroupService groupService;
	
	@GET
	public Response getGroups() {
		final ImmutableList<Group> groupsOfUser = groupService.getGroups();
		return Response.status(Response.Status.OK).entity(Lists.newArrayList(groupsOfUser)).build();
	}

	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	public Response createGroup(@RequestBody final Map data) {
		final Group createdGroup = groupService.createGroup((String) data.get("name"));
		return Response.status(Response.Status.CREATED).entity(createdGroup).build();
	}

	@GET
	@Path("/{id}")
	public Response getGroup(@PathParam("id") final Integer id) {
		return Response.status(Response.Status.OK).entity(groupService.getGroup(id)).build();
	}

	@DELETE
	@Path("/{id}")
	public Response deleteGroup(@PathParam("id") final Integer id) {
		groupService.deleteGroup(id);
		return Response.status(Response.Status.CREATED).build();
	}

	@PUT
	@Path("/{id}")
	@Consumes({MediaType.APPLICATION_JSON})
	public Response updateGroup(@PathParam("id") final Integer id, @RequestBody final Map data) {
		final Group updatedGroup = new Group(id, (String) data.get("name"));
		return Response.status(Response.Status.OK).entity(groupService.updateGroup(updatedGroup)).build();
	}
}
