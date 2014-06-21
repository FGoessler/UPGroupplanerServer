package de.unipotsdam.cs.groupplaner.support;

import org.joda.time.DateTimeFieldType;
import org.joda.time.Partial;

public class PartialToIntegerConverter {

	public static Partial createPartialFromDBInt(final int partialAsIntFromDatabase) {
		DateTimeFieldType[] types = new DateTimeFieldType[3];
		types[0] = DateTimeFieldType.dayOfWeek();
		types[1] = DateTimeFieldType.clockhourOfDay();
		types[2] = DateTimeFieldType.minuteOfHour();

		int[] values = new int[3];
		values[0] = partialAsIntFromDatabase / 10000;
		values[1] = 1 + (partialAsIntFromDatabase - values[0] * 10000) / 100;
		values[2] = partialAsIntFromDatabase - values[0] * 10000 - (values[1] - 1) * 100;

		return new Partial(types, values);
	}

	public static Integer createDBIntFromPartial(final Partial partial) {
		Integer result = 0;

		final DateTimeFieldType[] types = partial.getFieldTypes();
		final int[] values = partial.getValues();

		for (int i = 0; i < types.length; i++) {
			if (types[i] == DateTimeFieldType.dayOfWeek()) {
				result += values[i] * 10000;
			} else if (types[i] == DateTimeFieldType.clockhourOfDay()) {
				result += (values[i] - 1) * 100;
			} else if (types[i] == DateTimeFieldType.minuteOfHour()) {
				result += values[i];
			}
		}

		return result;
	}
}
