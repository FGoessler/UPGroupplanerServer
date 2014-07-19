package de.unipotsdam.cs.groupplaner.datefinder.service;

import de.unipotsdam.cs.groupplaner.domain.Member;

import java.util.List;

/**
 * This class is used to cache certain information about a group (e.g. its member) for the duration of a request to
 * avoid too many calls to the database.
 */
public interface GroupInformationHolder {
	public Integer getGroupId();

	public void setGroupId(Integer groupId);

	public List<Member> getMembers();

	public Integer getNumberOfMembers();
}
