package de.unipotsdam.cs.groupplaner.datefinder.service;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.unipotsdam.cs.groupplaner.datefinder.list.ConsecutiveDateStream;
import de.unipotsdam.cs.groupplaner.datefinder.list.ConsecutiveDateStreamDateCreator;
import de.unipotsdam.cs.groupplaner.datefinder.list.DateCombiner;
import de.unipotsdam.cs.groupplaner.domain.AcceptedDate;
import de.unipotsdam.cs.groupplaner.domain.BlockedDate;
import de.unipotsdam.cs.groupplaner.domain.Member;
import de.unipotsdam.cs.groupplaner.domain.TraitDate;
import de.unipotsdam.cs.groupplaner.group.dao.AcceptedDatesDAO;
import de.unipotsdam.cs.groupplaner.group.service.GroupService;
import de.unipotsdam.cs.groupplaner.user.dao.BlockedDatesDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
				dates.add(new TraitDate(date, Lists.newArrayList(TraitDate.TRAIT_BLOCKED_DATE)));
			}

			//add members accepted dates
			final ImmutableList<AcceptedDate> usersAcceptedDates = acceptedDatesDAO.getAcceptedDates(member.getEmail());
			for (AcceptedDate date : usersAcceptedDates) {
				final ArrayList<String> traits = Lists.newArrayList();
				if (date.getGroup().equals(groupId)) {
					traits.add(TraitDate.TRAIT_ACCEPTED_DATE);
				} else {
					traits.add(TraitDate.TRAIT_BLOCKED_DATE);
				}
				dates.add(new TraitDate(date, traits));
			}
		}

		return dates;
	}
}
