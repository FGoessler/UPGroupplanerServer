package de.unipotsdam.cs.groupplaner.group.resource;

import com.google.common.base.Preconditions;
import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.domain.Group;
import de.unipotsdam.cs.groupplaner.group.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(PathConfig.GROUP_RESOURCE_PATH)
public class GroupResource {

	@Autowired
	private GroupService groupService;

	@RequestMapping(method = RequestMethod.GET)
	public List<Group> getGroups() {
		return groupService.getGroups();
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public Group createGroup(@RequestBody final Map data) {
		Preconditions.checkNotNull(data.get("name"));

		return groupService.createGroup((String) data.get("name"));
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Group getGroup(@PathVariable("id") final Integer id) {
		return groupService.getGroup(id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Group updateGroup(@PathVariable("id") final Integer id, @RequestBody final Map data) {
		Preconditions.checkNotNull(data.get("name"));

		final Group updatedGroup = new Group(id, (String) data.get("name"));
		return groupService.updateGroup(updatedGroup);
	}
}
