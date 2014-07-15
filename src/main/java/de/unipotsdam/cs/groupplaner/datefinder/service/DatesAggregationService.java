package de.unipotsdam.cs.groupplaner.datefinder.service;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.unipotsdam.cs.groupplaner.datefinder.list.LinearDateList;
import de.unipotsdam.cs.groupplaner.datefinder.list.LinearDateListDateCreator;
import de.unipotsdam.cs.groupplaner.datefinder.list.TraitDateCombiner;
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
	private TraitDateCombiner<TraitDate> traitDateCombiner;
	@Autowired
	private LinearDateListDateCreator<TraitDate> linearDateListDateCreator;

	public LinearDateList<TraitDate> loadDates(final Integer groupId) {
		final List<Member> members = groupService.getActiveMembers(groupId);

		final LinearDateList<TraitDate> dates = new LinearDateList<TraitDate>(traitDateCombiner, linearDateListDateCreator);

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
