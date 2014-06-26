package de.unipotsdam.cs.groupplaner.service.impl;

import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.domain.AcceptedDate;
import de.unipotsdam.cs.groupplaner.repository.AcceptedDatesRepository;
import de.unipotsdam.cs.groupplaner.service.AcceptedDatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class AcceptedDatesServiceImpl implements AcceptedDatesService {

	@Autowired
	private AcceptedDatesRepository acceptedDatesRepository;

	@Override
	@PreAuthorize("@groupPermissionService.hasReadPermission(authentication, #groupId)")
	public ImmutableList<AcceptedDate> getAcceptedDates(final Integer groupId) {
		return acceptedDatesRepository.getAcceptedDates(groupId);
	}

	@Override
	@PreAuthorize("@groupPermissionService.hasReadPermission(authentication, #groupId)")
	public AcceptedDate getAcceptedDate(final Integer groupId, final Integer id) {
		validateGroupMatchesDate(groupId, id);

		return acceptedDatesRepository.getAcceptedDate(id);
	}

	@Override
	@PreAuthorize("@groupPermissionService.hasWritePermission(authentication, #groupId)")
	public AcceptedDate createAcceptedDate(final Integer groupId, final Integer start, final Integer end) {
		AcceptedDate newAcceptedDate = new AcceptedDate(start, end, groupId);
		final Integer createdDateId = acceptedDatesRepository.createAcceptedDate(newAcceptedDate);
		return acceptedDatesRepository.getAcceptedDate(createdDateId);
	}

	@Override
	@PreAuthorize("@groupPermissionService.hasWritePermission(authentication, #groupId)")
	public AcceptedDate updateAcceptedDate(final Integer groupId, final Integer id, final Integer start, final Integer end) {
		validateGroupMatchesDate(groupId, id);

		AcceptedDate modifiedDate = new AcceptedDate(id, start, end, groupId);
		final Boolean updateSuccessful = acceptedDatesRepository.updateAcceptedDate(modifiedDate);
		if (!updateSuccessful) {
			throw new EmptyResultDataAccessException(1);
		}
		return acceptedDatesRepository.getAcceptedDate(modifiedDate.getId());
	}

	@Override
	@PreAuthorize("@groupPermissionService.hasWritePermission(authentication, #groupId)")
	public void deleteAcceptedDate(final Integer groupId, final Integer id) {
		validateGroupMatchesDate(groupId, id);

		final Boolean deletionSuccessful = acceptedDatesRepository.deleteAcceptedDate(id);
		if (!deletionSuccessful) {
			throw new EmptyResultDataAccessException(1);
		}
	}

	private void validateGroupMatchesDate(final Integer groupId, final Integer dateId) {
		final AcceptedDate date = acceptedDatesRepository.getAcceptedDate(dateId);
		if (!date.getGroup().equals(groupId)) {
			throw new IllegalArgumentException("The date does not belong to specified group.");
		}
	}
}
