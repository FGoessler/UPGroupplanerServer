package de.unipotsdam.cs.groupplaner.datefinder.service;


import com.google.common.collect.Lists;
import de.unipotsdam.cs.groupplaner.datefinder.list.LinearDateListModifier;
import de.unipotsdam.cs.groupplaner.domain.PrioritizedDate;
import de.unipotsdam.cs.groupplaner.domain.TraitDate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Convert neutral dates near blocked dates to optimal dates depending on min and max optimal date duration.
 */
@Component
public class OptimalDatePrioritizeModifier implements LinearDateListModifier<PrioritizedDate> {

	public static final int MAX_OPTIMAL_DATE_DURATION = 60;
	public static final int MIN_OPTIMAL_DATE_DURATION = 30;

	@Override
	public List<PrioritizedDate> modifyDate(final PrioritizedDate predecessorDate, final PrioritizedDate curDate, final PrioritizedDate successorDate) {
		final List<PrioritizedDate> dates = new ArrayList<PrioritizedDate>();
		if (curDate.getPriority() != PrioritizedDate.PRIORITY_BLOCKED) {
			final Integer duration = curDate.getDuration();
			final Integer newOptiPrio = curDate.getPriority() + PrioritizedDate.PRIORITY_OPTIMAL;

			// TODO: at the moment the algorithm fails to add optimal dates that would cover more than 1 succeeding period (e.g. not night and night date)

			// date is between two blocked dates
			if (dateIsSomehowBlocked(predecessorDate) && dateIsSomehowBlocked(successorDate)) {
				if (duration > MAX_OPTIMAL_DATE_DURATION * 2) {    // two optimal dates fit in with break between
					dates.add(new PrioritizedDate(curDate.getStart(), curDate.getStart() + MAX_OPTIMAL_DATE_DURATION, newOptiPrio));
					dates.add(new PrioritizedDate(curDate.getStart() + MAX_OPTIMAL_DATE_DURATION, curDate.getEnd() - MAX_OPTIMAL_DATE_DURATION, PrioritizedDate.PRIORITY_NEUTRAL));
					dates.add(new PrioritizedDate(curDate.getEnd() - MAX_OPTIMAL_DATE_DURATION, curDate.getEnd(), newOptiPrio));
				} else if (duration > MAX_OPTIMAL_DATE_DURATION && duration <= MAX_OPTIMAL_DATE_DURATION * 2) {    // two optimal dates fit in without break between
					int middle = curDate.getStart() + (int) Math.floor(duration / 2);
					dates.add(new PrioritizedDate(curDate.getStart(), middle, newOptiPrio));
					dates.add(new PrioritizedDate(middle, curDate.getEnd(), newOptiPrio));
				} else if (duration >= MIN_OPTIMAL_DATE_DURATION) {    // one optimal date fits in
					dates.add(new PrioritizedDate(curDate, newOptiPrio));
				} else {        // no optimal date fits in
					dates.add(new PrioritizedDate(curDate, PrioritizedDate.PRIORITY_NEUTRAL));
				}
				// date is before a blocked date
			} else if (!dateIsSomehowBlocked(predecessorDate) && dateIsSomehowBlocked(successorDate)) {
				if (duration > MAX_OPTIMAL_DATE_DURATION) {                // one optimal fits in with time before
					dates.add(new PrioritizedDate(curDate.getStart(), curDate.getEnd() - MAX_OPTIMAL_DATE_DURATION, PrioritizedDate.PRIORITY_NEUTRAL));
					dates.add(new PrioritizedDate(curDate.getEnd() - MAX_OPTIMAL_DATE_DURATION, curDate.getEnd(), newOptiPrio));
				} else if (duration >= MIN_OPTIMAL_DATE_DURATION) {        // one optimal fits in without time before
					dates.add(new PrioritizedDate(curDate, newOptiPrio));
				} else {        // no optimal date fits in
					dates.add(new PrioritizedDate(curDate, PrioritizedDate.PRIORITY_NEUTRAL));
				}
				// date is after a blocked date
			} else if (dateIsSomehowBlocked(predecessorDate) && !dateIsSomehowBlocked(successorDate)) {
				if (duration > MAX_OPTIMAL_DATE_DURATION) {                // one optimal fits in with time after
					dates.add(new PrioritizedDate(curDate.getStart(), curDate.getStart() + MAX_OPTIMAL_DATE_DURATION, newOptiPrio));
					dates.add(new PrioritizedDate(curDate.getStart() + MAX_OPTIMAL_DATE_DURATION, curDate.getEnd(), PrioritizedDate.PRIORITY_NEUTRAL));
				} else if (duration >= MIN_OPTIMAL_DATE_DURATION) {        // one optimal fits in without time after
					dates.add(new PrioritizedDate(curDate, newOptiPrio));
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
		return date.hasTrait(TraitDate.TRAIT_BLOCKED_DATE) || date.hasTrait(TraitDate.TRAIT_ACCEPTED_DATE);
	}
}
