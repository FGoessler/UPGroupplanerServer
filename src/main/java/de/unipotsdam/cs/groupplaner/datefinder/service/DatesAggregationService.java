package de.unipotsdam.cs.groupplaner.datefinder.service;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import de.unipotsdam.cs.groupplaner.datefinder.list.ConsecutiveDateStream;
import de.unipotsdam.cs.groupplaner.datefinder.list.ConsecutiveDateStreamDateCreator;
import de.unipotsdam.cs.groupplaner.datefinder.list.DateCombiner;
import de.unipotsdam.cs.groupplaner.domain.Member;
import de.unipotsdam.cs.groupplaner.domain.dates.AcceptedDate;
import de.unipotsdam.cs.groupplaner.domain.dates.BlockedDate;
import de.unipotsdam.cs.groupplaner.domain.dates.TraitDate;
import de.unipotsdam.cs.groupplaner.group.dao.AcceptedDatesDAO;
import de.unipotsdam.cs.groupplaner.group.service.GroupService;
import de.unipotsdam.cs.groupplaner.user.dao.BlockedDatesDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DatesAggregationService {

	@Autowired
	private BlockedDatesDAO blockedDatesDAO;
	@Autowired
	private AcceptedDatesDAO acceptedDatesDAO;
	@Autowired
	private GroupService groupService;
	@Autowired
	private DateCombiner<TraitDate> dateCombiner;
	@Autowired
	private ConsecutiveDateStreamDateCreator<TraitDate> consecutiveDateStreamDateCreator;

	public ConsecutiveDateStream<TraitDate> loadDates(final Integer groupId) {
		final List<Member> members = groupService.getActiveMembers(groupId);

		final ConsecutiveDateStream<TraitDate> dates = new ConsecutiveDateStream<TraitDate>(dateCombiner, consecutiveDateStreamDateCreator);

		for (Member member : members) {
			// add members blocked dates
			final ImmutableList<BlockedDate> usersBlockedDates = blockedDatesDAO.getBlockedDates(member.getEmail());
			for (BlockedDate date : usersBlockedDates) {
				final Map<String, Object> traits = new HashMap<String, Object>();
				traits.put(TraitDate.TRAIT_BLOCKED_DATE, Sets.newHashSet(member.getEmail()));
				dates.add(new TraitDate(date, traits));
			}

			//add members accepted dates
			final ImmutableList<AcceptedDate> usersAcceptedDates = acceptedDatesDAO.getAcceptedDates(member.getEmail());
			for (AcceptedDate date : usersAcceptedDates) {
				final Map<String, Object> traits = new HashMap<String, Object>();
				if (date.getGroup().equals(groupId)) {
					traits.put(TraitDate.TRAIT_ACCEPTED_DATE, date.getId());
				}
				traits.put(TraitDate.TRAIT_BLOCKED_DATE, Sets.newHashSet(member.getEmail()));
				dates.add(new TraitDate(date, traits));
			}
		}

		return dates;
	}
}
