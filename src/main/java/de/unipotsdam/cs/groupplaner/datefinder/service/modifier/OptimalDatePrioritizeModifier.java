package de.unipotsdam.cs.groupplaner.datefinder.service.modifier;


import com.google.common.collect.Lists;
import de.unipotsdam.cs.groupplaner.datefinder.list.ConsecutiveDateStream;
import de.unipotsdam.cs.groupplaner.datefinder.list.ConsecutiveDateStreamModifier;
import de.unipotsdam.cs.groupplaner.datefinder.service.GroupInformationHolder;
import de.unipotsdam.cs.groupplaner.domain.dates.PrioritizedDate;
import de.unipotsdam.cs.groupplaner.domain.dates.TraitDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Convert neutral dates near blocked dates to optimal dates depending on min and max optimal date duration.
 * The more members of the group are blocked before a date the higher the priority.
 */
@Component
public class OptimalDatePrioritizeModifier implements ConsecutiveDateStreamModifier<PrioritizedDate> {

	@Autowired
	private GroupInformationHolder groupInformationHolder;

	public static final int MAX_OPTIMAL_DATE_DURATION = 60;
	public static final int MIN_OPTIMAL_DATE_DURATION = 30;

	@Override
	public List<PrioritizedDate> modifyDate(ConsecutiveDateStream<PrioritizedDate> dateStream, final PrioritizedDate curDate) {
		final PrioritizedDate predecessorDate = dateStream.predeccessorDate(curDate);
		final PrioritizedDate successorDate = dateStream.successorDate(curDate);

		final List<PrioritizedDate> dates = new ArrayList<PrioritizedDate>();
		if (curDate.getPriority() != PrioritizedDate.PRIORITY_BLOCKED) {
			final Integer duration = curDate.getDuration();
			final Integer newOptiPrioAfterPredcessor = calculateNwPriority(curDate, predecessorDate);
			final Integer newOptiPrioBeforeSuccessor = calculateNwPriority(curDate, successorDate);

			// date is between two blocked dates
			if (dateIsSomehowBlocked(predecessorDate) && dateIsSomehowBlocked(successorDate)) {
				if (duration > MAX_OPTIMAL_DATE_DURATION * 2) {    // two optimal dates fit in with break between
					dates.add(new PrioritizedDate(curDate.getStart(), curDate.getStart() + MAX_OPTIMAL_DATE_DURATION, newOptiPrioAfterPredcessor));
					dates.add(new PrioritizedDate(curDate.getStart() + MAX_OPTIMAL_DATE_DURATION, curDate.getEnd() - MAX_OPTIMAL_DATE_DURATION, PrioritizedDate.PRIORITY_NEUTRAL));
					dates.add(new PrioritizedDate(curDate.getEnd() - MAX_OPTIMAL_DATE_DURATION, curDate.getEnd(), newOptiPrioBeforeSuccessor));
				} else if (duration > MAX_OPTIMAL_DATE_DURATION && duration <= MAX_OPTIMAL_DATE_DURATION * 2) {    // two optimal dates fit in without break between
					int middle = curDate.getStart() + (int) Math.floor(duration / 2);
					dates.add(new PrioritizedDate(curDate.getStart(), middle, newOptiPrioAfterPredcessor));
					dates.add(new PrioritizedDate(middle, curDate.getEnd(), newOptiPrioBeforeSuccessor));
				} else if (duration >= MIN_OPTIMAL_DATE_DURATION) {    // one optimal date fits in
					dates.add(new PrioritizedDate(curDate, Math.max(newOptiPrioAfterPredcessor, newOptiPrioBeforeSuccessor)));
				} else {        // no optimal date fits in
					dates.add(new PrioritizedDate(curDate, PrioritizedDate.PRIORITY_NEUTRAL));
				}
				// date is before a blocked date
			} else if (!dateIsSomehowBlocked(predecessorDate) && dateIsSomehowBlocked(successorDate)) {
				if (duration > MAX_OPTIMAL_DATE_DURATION) {                // one optimal fits in with time before
					dates.add(new PrioritizedDate(curDate.getStart(), curDate.getEnd() - MAX_OPTIMAL_DATE_DURATION, PrioritizedDate.PRIORITY_NEUTRAL));
					dates.add(new PrioritizedDate(curDate.getEnd() - MAX_OPTIMAL_DATE_DURATION, curDate.getEnd(), newOptiPrioBeforeSuccessor));
				} else if (duration >= MIN_OPTIMAL_DATE_DURATION) {        // one optimal fits in without time before
					dates.add(new PrioritizedDate(curDate, newOptiPrioBeforeSuccessor));
				} else {        // no optimal date fits in
					dates.add(new PrioritizedDate(curDate, PrioritizedDate.PRIORITY_NEUTRAL));
				}
				// date is after a blocked date
			} else if (dateIsSomehowBlocked(predecessorDate) && !dateIsSomehowBlocked(successorDate)) {
				if (duration > MAX_OPTIMAL_DATE_DURATION) {                // one optimal fits in with time after
					dates.add(new PrioritizedDate(curDate.getStart(), curDate.getStart() + MAX_OPTIMAL_DATE_DURATION, newOptiPrioAfterPredcessor));
					dates.add(new PrioritizedDate(curDate.getStart() + MAX_OPTIMAL_DATE_DURATION, curDate.getEnd(), PrioritizedDate.PRIORITY_NEUTRAL));
				} else if (duration >= MIN_OPTIMAL_DATE_DURATION) {        // one optimal fits in without time after
					dates.add(new PrioritizedDate(curDate, newOptiPrioAfterPredcessor));
				} else {        // no optimal date fits in
					dates.add(new PrioritizedDate(curDate, PrioritizedDate.PRIORITY_NEUTRAL));
				}
			}

		}

		if (dates.size() < 1) {
			return Lists.newArrayList(curDate);
		} else {
			return dates;
		}
	}

	private Boolean dateIsSomehowBlocked(final TraitDate date) {
		return date.hasTrait(TraitDate.TRAIT_BLOCKED_DATE);
	}

	private Integer calculateNwPriority(final PrioritizedDate curDate, final PrioritizedDate edgeDate) {
		return curDate.getPriority() + ((int) Math.floor(PrioritizedDate.PRIORITY_OPTIMAL * blockedMultiplierForDate(edgeDate)));
	}

	/**
	 * Calculates a multiplier between 0 (no one is blocked) and 1 (everyone is blocked) depending on how many of all
	 * group members are blocked for this date.
	 */
	private Double blockedMultiplierForDate(final PrioritizedDate date) {
		final Integer members = groupInformationHolder.getNumberOfMembers();
		if (date.hasTrait(TraitDate.TRAIT_BLOCKED_DATE)) {
			return ((Integer) date.getTrait(TraitDate.TRAIT_BLOCKED_DATE)).doubleValue() / members.doubleValue();
		} else {
			return 0.0;
		}
	}
}
