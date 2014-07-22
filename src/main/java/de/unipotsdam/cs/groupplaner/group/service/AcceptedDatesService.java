package de.unipotsdam.cs.groupplaner.group.service;

import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.domain.dates.AcceptedDate;

public interface AcceptedDatesService {
	public ImmutableList<AcceptedDate> getAcceptedDates(final Integer groupId);

	public AcceptedDate getAcceptedDate(final Integer groupId, Integer id);

	public AcceptedDate createAcceptedDate(final Integer groupId, final Integer start, final Integer end);

	public AcceptedDate updateAcceptedDate(final Integer groupId, final Integer id, final Integer start, final Integer end);

	public void deleteAcceptedDate(final Integer groupId, final Integer id);
}
