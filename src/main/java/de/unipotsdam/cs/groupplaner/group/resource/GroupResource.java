package de.unipotsdam.cs.groupplaner.group.resource;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.domain.Group;
import de.unipotsdam.cs.groupplaner.group.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@RestController
@RequestMapping(PathConfig.GROUP_RESOURCE_PATH)
public class GroupResource {

	@Autowired
	private GroupService groupService;

	@RequestMapping(method = RequestMethod.GET)
	public Response getGroups() {
		final ImmutableList<Group> groupsOfUser = groupService.getGroups();
		return Response.status(Response.Status.OK).entity(Lists.newArrayList(groupsOfUser)).build();
	}

	@RequestMapping(method = RequestMethod.POST)
	@Consumes({MediaType.APPLICATION_JSON})
	public Response createGroup(@RequestBody final Map data) {
		Preconditions.checkNotNull(data.get("name"));

		final Group createdGroup = groupService.createGroup((String) data.get("name"));
		return Response.status(Response.Status.CREATED).entity(createdGroup).build();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Response getGroup(@PathVariable("id") final Integer id) {
		return Response.status(Response.Status.OK).entity(groupService.getGroup(id)).build();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@Consumes({MediaType.APPLICATION_JSON})
	public Response updateGroup(@PathVariable("id") final Integer id, @RequestBody final Map data) {
		Preconditions.checkNotNull(data.get("name"));

		final Group updatedGroup = new Group(id, (String) data.get("name"));
		return Response.status(Response.Status.OK).entity(groupService.updateGroup(updatedGroup)).build();
	}
}
