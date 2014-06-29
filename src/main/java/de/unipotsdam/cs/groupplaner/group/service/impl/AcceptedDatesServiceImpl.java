package de.unipotsdam.cs.groupplaner.group.service.impl;

import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.domain.AcceptedDate;
import de.unipotsdam.cs.groupplaner.group.dao.AcceptedDatesDAO;
import de.unipotsdam.cs.groupplaner.group.service.AcceptedDatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class AcceptedDatesServiceImpl implements AcceptedDatesService {

	@Autowired
	private AcceptedDatesDAO acceptedDatesDAO;

	@Override
	@PreAuthorize("@groupPermissionService.hasReadPermission(authentication, #groupId)")
	public ImmutableList<AcceptedDate> getAcceptedDates(final Integer groupId) {
		return acceptedDatesDAO.getAcceptedDates(groupId);
	}

	@Override
	@PreAuthorize("@groupPermissionService.hasReadPermission(authentication, #groupId)")
	public AcceptedDate getAcceptedDate(final Integer groupId, final Integer id) {
		validateGroupMatchesDate(groupId, id);

		return acceptedDatesDAO.getAcceptedDate(id);
	}

	@Override
	@PreAuthorize("@groupPermissionService.hasWritePermission(authentication, #groupId)")
	public AcceptedDate createAcceptedDate(final Integer groupId, final Integer start, final Integer end) {
		AcceptedDate newAcceptedDate = new AcceptedDate(start, end, groupId);
		final Integer createdDateId = acceptedDatesDAO.createAcceptedDate(newAcceptedDate);
		return acceptedDatesDAO.getAcceptedDate(createdDateId);
	}

	@Override
	@PreAuthorize("@groupPermissionService.hasWritePermission(authentication, #groupId)")
	public AcceptedDate updateAcceptedDate(final Integer groupId, final Integer id, final Integer start, final Integer end) {
		validateGroupMatchesDate(groupId, id);

		AcceptedDate modifiedDate = new AcceptedDate(id, start, end, groupId);
		final Boolean updateSuccessful = acceptedDatesDAO.updateAcceptedDate(modifiedDate);
		if (!updateSuccessful) {
			throw new EmptyResultDataAccessException(1);
		}
		return acceptedDatesDAO.getAcceptedDate(modifiedDate.getId());
	}

	@Override
	@PreAuthorize("@groupPermissionService.hasWritePermission(authentication, #groupId)")
	public void deleteAcceptedDate(final Integer groupId, final Integer id) {
		validateGroupMatchesDate(groupId, id);

		final Boolean deletionSuccessful = acceptedDatesDAO.deleteAcceptedDate(id);
		if (!deletionSuccessful) {
			throw new EmptyResultDataAccessException(1);
		}
	}

	private void validateGroupMatchesDate(final Integer groupId, final Integer dateId) {
		final AcceptedDate date = acceptedDatesDAO.getAcceptedDate(dateId);
		if (!date.getGroup().equals(groupId)) {
			throw new IllegalArgumentException("The date does not belong to specified group.");
		}
	}
}
