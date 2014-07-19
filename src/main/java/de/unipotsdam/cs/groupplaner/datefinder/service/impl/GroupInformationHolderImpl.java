package de.unipotsdam.cs.groupplaner.datefinder.service.impl;

import com.google.common.base.Preconditions;
import de.unipotsdam.cs.groupplaner.datefinder.service.GroupInformationHolder;
import de.unipotsdam.cs.groupplaner.domain.Member;
import de.unipotsdam.cs.groupplaner.group.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
public class GroupInformationHolderImpl implements GroupInformationHolder {

	@Autowired
	private GroupService groupService;

	private Integer groupId;

	private List<Member> cachedMembers;

	@Override
	public Integer getGroupId() {
		return groupId;
	}

	@Override
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	@Override
	public List<Member> getMembers() {
		Preconditions.checkNotNull(groupId);

		if (cachedMembers == null) {
			cachedMembers = groupService.getActiveMembers(groupId);
		}
		return cachedMembers;
	}

	@Override
	public Integer getNumberOfMembers() {
		return getMembers().size();
	}
}
