package de.unipotsdam.cs.groupplaner.group.resource;

import com.google.common.base.Preconditions;
import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.domain.InvitationState;
import de.unipotsdam.cs.groupplaner.domain.Member;
import de.unipotsdam.cs.groupplaner.group.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(PathConfig.GROUP_MEMBER_RESOURCE_PATH)
public class MemberResource {

	@Autowired
	private GroupService groupService;

	@RequestMapping(method = RequestMethod.GET)
	public List<Member> getMembers(@PathVariable("id") final Integer groupId) {
		return groupService.getActiveMembers(groupId);
	}

	@RequestMapping(value = "/{email}", method = RequestMethod.GET)
	public Member getMember(@PathVariable("id") final Integer groupId, @PathVariable("email") String email) {
		return groupService.getMember(email, groupId);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public Member addMember(@PathVariable("id") final Integer groupId, @RequestBody final Map data) {
		Preconditions.checkNotNull(data.get("email"));

		return groupService.inviteUser((String) data.get("email"), groupId);
	}

	@RequestMapping(value = "/{email}", method = RequestMethod.PUT)
	public Member updateMember(@PathVariable("id") final Integer groupId, @PathVariable("email") final String email, @RequestBody final Map data) {
		Preconditions.checkNotNull(data.get("invitationState"));

		return groupService.updateMemberStatus(email, groupId, InvitationState.valueOf((String) data.get("invitationState")));
	}

	@RequestMapping(value = "/{email}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMember(@PathVariable("id") final Integer groupId, @PathVariable("email") String email) {
		groupService.updateMemberStatus(email, groupId, InvitationState.REMOVED);
	}
}
