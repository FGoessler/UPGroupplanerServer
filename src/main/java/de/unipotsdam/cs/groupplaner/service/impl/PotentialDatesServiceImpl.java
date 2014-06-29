package de.unipotsdam.cs.groupplaner.service.impl;

import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.domain.BlockedDate;
import de.unipotsdam.cs.groupplaner.domain.Member;
import de.unipotsdam.cs.groupplaner.domain.PeriodDate;
import de.unipotsdam.cs.groupplaner.repository.BlockedDatesRepository;
import de.unipotsdam.cs.groupplaner.service.GroupService;
import de.unipotsdam.cs.groupplaner.service.PotentialDatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class PotentialDatesServiceImpl implements PotentialDatesService {

	@Autowired
	private BlockedDatesRepository blockedDatesRepository;
	@Autowired
	private GroupService groupService;

	@Override
	@PreAuthorize("@groupPermissionService.hasReadPermission(authentication, #groupId)")
	public ImmutableList<PeriodDate> calculatePotentialDates(final Integer groupId) {
		// 1.) Alle BlockedDates aller Mitglieder laden
		// TODO: durch andere Gruppen belegte Termine berücksichtigen
		final List<Member> members = groupService.getMembers(groupId);
		final List<PeriodDate> allBlockedDates = new ArrayList<PeriodDate>();
		for (Member member : members) {
			final ImmutableList<BlockedDate> usersBlockedDates = blockedDatesRepository.getBlockedDates(member.getEmail());
			allBlockedDates.addAll(usersBlockedDates);
		}

		// cancel right here if we do not have any blocked dates
		if (allBlockedDates.size() == 0) {
			return ImmutableList.of(new PeriodDate(10000, 72359));
		}

		// 2.) split dates with end < start (e.g. starting friday and ending monday)
		for (int i = 1; i < allBlockedDates.size(); i++) {
			PeriodDate date = allBlockedDates.get(i);
			if (date.getEnd() < date.getStart()) {
				allBlockedDates.set(i, new PeriodDate(10000, date.getEnd()));
				allBlockedDates.add(new PeriodDate(date.getStart(), 72359));
			}
		}

		// 3.) BlockedDates sortieren
		Collections.sort(allBlockedDates, new Comparator<PeriodDate>() {
			@Override
			public int compare(PeriodDate date1, PeriodDate date2) {
				return date1.getStart().compareTo(date2.getStart());
			}
		});

		// 4.) BlockedDates kombinieren
		// DO we really need this? Couldn't we directly resolve the free dates by iterating over all blocked dates and consider overlapping in the same step?
		// This might make the QoS analysis easier!
		PeriodDate prevDate = allBlockedDates.get(0);
		int indexCounter = 1;
		for (int i = 1; i < allBlockedDates.size(); i++) {
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

		// 5.) verfügbare Zeiten schlussfolgern
		final List<PeriodDate> availableDates = new ArrayList<PeriodDate>();
		int curPeriodStart = 10000;
		for (PeriodDate periodDate : allBlockedDates) {
			if (periodDate.getStart() == curPeriodStart) {
				curPeriodStart = periodDate.getEnd();
			} else {
				availableDates.add(new PeriodDate(curPeriodStart, periodDate.getStart()));
				curPeriodStart = periodDate.getEnd();
			}
		}
		if (curPeriodStart != 72359) {
			availableDates.add(new PeriodDate(curPeriodStart, 72359));
		}

		// TODO: also output blocked dates and give every date "optimum" value between -10 and 10

		// 6.) (optional) verfügbare Zeiten mit Qualität bewerten
		// TODO: Qualitätsbewertung

		return ImmutableList.copyOf(availableDates);
	}


		/*

	Fuer Qualitaetsbewertung:
	- Ueberlappungen von BlockedDates zaehlen
	- Nachttermine abwerten
	- Termine nahe an BlockedDates mit hoher Ueberlappung aufwerten

	 */
}
