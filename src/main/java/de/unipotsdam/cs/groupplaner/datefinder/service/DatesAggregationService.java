package de.unipotsdam.cs.groupplaner.datefinder.service;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.unipotsdam.cs.groupplaner.domain.*;
import de.unipotsdam.cs.groupplaner.group.dao.AcceptedDatesDAO;
import de.unipotsdam.cs.groupplaner.group.service.GroupService;
import de.unipotsdam.cs.groupplaner.user.dao.BlockedDatesDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class DatesAggregationService {

	@Autowired
	private BlockedDatesDAO blockedDatesDAO;
	@Autowired
	private AcceptedDatesDAO acceptedDatesDAO;
	@Autowired
	private GroupService groupService;

	public List<TraitDate> combineOverlappingDates(final List<TraitDate> allBlockedDates) {
		TraitDate prevDate = allBlockedDates.get(0);
		int indexCounter = 1;
		final int originalSize = allBlockedDates.size();
		for (int i = 1; i < originalSize; i++) {
			TraitDate curDate = allBlockedDates.get(indexCounter);
			if (prevDate.getStart() <= curDate.getStart() && curDate.getStart() <= prevDate.getEnd()) {
				//combine dates by creating a new date and replacing the old one with the new one
				if (prevDate.getEnd() < curDate.getEnd()) {
					// TODO: handle traits -> eventually split in several dates - continue the loop at the right position/maybe resort necessary? - maybe use a TreeMap
					TraitDate newDate = new TraitDate(prevDate.getStart(), curDate.getEnd(), curDate.getTraits());
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

	public List<TraitDate> getSortedBlockedDatesOfAllMembers(final Integer groupId) {
		final List<Member> members = groupService.getActiveMembers(groupId);
		List<TraitDate> allBlockedDates = new ArrayList<TraitDate>();
		for (Member member : members) {
			// add members blocked dates
			final ImmutableList<BlockedDate> usersBlockedDates = blockedDatesDAO.getBlockedDates(member.getEmail());
			for (BlockedDate date : usersBlockedDates) {
				allBlockedDates.add(new TraitDate(date, Lists.newArrayList(TraitDate.TRAIT_BLOCKED_DATE)));
			}

			//add members accepted dates
			final ImmutableList<AcceptedDate> usersAcceptedDates = acceptedDatesDAO.getAcceptedDates(member.getEmail());
			for (AcceptedDate date : usersAcceptedDates) {
				final ArrayList<String> traits = Lists.newArrayList();
				if (date.getGroup().equals(groupId)) {
					traits.add(TraitDate.TRAIT_ACCEPTED_DATE);
				}
				allBlockedDates.add(new TraitDate(date, traits));
			}
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

	private List<TraitDate> splitOverflowingDates(final List<TraitDate> allBlockedDates) {
		// iterate over all dates and split those with end < start (e.g. starting friday and ending tuesday)
		for (int i = 1; i < allBlockedDates.size(); i++) {
			TraitDate date = allBlockedDates.get(i);
			if (date.getEnd() < date.getStart()) {
				allBlockedDates.set(i, new TraitDate(PeriodDate.START_OF_WEEK, date.getEnd(), date.getTraits()));
				allBlockedDates.add(new TraitDate(date.getStart(), PeriodDate.END_OF_WEEK, date.getTraits()));
			}
		}
		return allBlockedDates;
	}
}
