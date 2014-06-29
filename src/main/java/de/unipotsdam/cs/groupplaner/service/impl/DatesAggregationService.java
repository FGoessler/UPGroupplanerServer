package de.unipotsdam.cs.groupplaner.service.impl;


import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.domain.AcceptedDate;
import de.unipotsdam.cs.groupplaner.domain.BlockedDate;
import de.unipotsdam.cs.groupplaner.domain.Member;
import de.unipotsdam.cs.groupplaner.domain.PeriodDate;
import de.unipotsdam.cs.groupplaner.repository.AcceptedDatesRepository;
import de.unipotsdam.cs.groupplaner.repository.BlockedDatesRepository;
import de.unipotsdam.cs.groupplaner.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class DatesAggregationService {

	@Autowired
	private BlockedDatesRepository blockedDatesRepository;
	@Autowired
	private AcceptedDatesRepository acceptedDatesRepository;
	@Autowired
	private GroupService groupService;

	public List<PeriodDate> combineOverlappingDates(final List<PeriodDate> allBlockedDates) {
		PeriodDate prevDate = allBlockedDates.get(0);
		int indexCounter = 1;
		final int originalSize = allBlockedDates.size();
		for (int i = 1; i < originalSize; i++) {
			PeriodDate curDate = allBlockedDates.get(indexCounter);
			if (prevDate.getStart() <= curDate.getStart() && curDate.getStart() <= prevDate.getEnd()) {
				//combine dates by creating a new date and replacing the old one with the new one
				if (prevDate.getEnd() < curDate.getEnd()) {
					PeriodDate newDate = new PeriodDate(prevDate.getStart(), curDate.getEnd());
					int index = allBlockedDates.indexOf(prevDate);
					allBlockedDates.set(index, newDate);
					prevDate = newDate;
				}

				//remove curDate from list
				allBlockedDates.remove(indexCounter);
			} else {
				prevDate = curDate;
				indexCounter++;
			}
		}
		return allBlockedDates;
	}

	public List<PeriodDate> getSortedBlockedDatesOfAllMembers(final Integer groupId) {
		final List<Member> members = groupService.getMembers(groupId);
		List<PeriodDate> allBlockedDates = new ArrayList<PeriodDate>();
		for (Member member : members) {
			// add members blocked dates
			final ImmutableList<BlockedDate> usersBlockedDates = blockedDatesRepository.getBlockedDates(member.getEmail());
			allBlockedDates.addAll(usersBlockedDates);

			//add members accepted dates
			final ImmutableList<AcceptedDate> usersAcceptedDates = acceptedDatesRepository.getAcceptedDates(member.getEmail());
			allBlockedDates.addAll(usersAcceptedDates);
		}

		allBlockedDates = splitOverflowingDates(allBlockedDates);

		Collections.sort(allBlockedDates, new Comparator<PeriodDate>() {
			@Override
			public int compare(PeriodDate date1, PeriodDate date2) {
				return date1.getStart().compareTo(date2.getStart());
			}
		});

		return allBlockedDates;
	}

	private List<PeriodDate> splitOverflowingDates(final List<PeriodDate> allBlockedDates) {
		// iterate over all dates and split those with end < start (e.g. starting friday and ending tuesday)
		for (int i = 1; i < allBlockedDates.size(); i++) {
			PeriodDate date = allBlockedDates.get(i);
			if (date.getEnd() < date.getStart()) {
				allBlockedDates.set(i, new PeriodDate(PeriodDate.START_OF_WEEK, date.getEnd()));
				allBlockedDates.add(new PeriodDate(date.getStart(), PeriodDate.END_OF_WEEK));
			}
		}
		return allBlockedDates;
	}
}
