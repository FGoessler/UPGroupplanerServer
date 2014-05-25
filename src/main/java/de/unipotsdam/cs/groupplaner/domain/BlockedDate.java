package de.unipotsdam.cs.groupplaner.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Partial;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BlockedDate {
	final private Integer id;
	final private Partial start;
	final private Partial end;
	final private String userEmail;

	public BlockedDate(final Partial start, final Partial end, final String userEmail) {
		this(null, start, end, userEmail);
	}

	public BlockedDate(final int start, final int end, final String userEmail) {
		this(null, start, end, userEmail);
	}

	public BlockedDate(final Integer id, final Partial start, final Partial end, final String userEmail) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.userEmail = userEmail;
	}

	public BlockedDate(final Integer id, final int start, final int end, final String userEmail) {
		this(id, createPartialFromDBInt(start), createPartialFromDBInt(end), userEmail);
	}

	public Integer getId() {
		return id;
	}

	public Integer getStart() {
		return createDBIntFromPartial(start);
	}

	public Integer getEnd() {
		return createDBIntFromPartial(end);
	}

	@JsonIgnore
	public Partial getStartAsPartial() {
		return start;
	}

	@JsonIgnore
	public Partial getEndAsPartial() {
		return end;
	}

	@JsonIgnore
	public String getUserEmail() {
		return userEmail;
	}

	private static Partial createPartialFromDBInt(final int partialAsIntFromDatabase) {
		DateTimeFieldType[] types = new DateTimeFieldType[3];
		types[0] = DateTimeFieldType.dayOfWeek();
		types[1] = DateTimeFieldType.clockhourOfDay();
		types[2] = DateTimeFieldType.minuteOfHour();
		
		int[] values = new int[3];
		values[0] = partialAsIntFromDatabase / 10000;
		values[1] = (partialAsIntFromDatabase - values[0] * 10000) / 100;
		values[2] = partialAsIntFromDatabase - values[0] * 10000 - values[1] * 100;
		
		return new Partial(types, values);
	}

	private static Integer createDBIntFromPartial(final Partial partial) {
		Integer result = 0;
		
		final DateTimeFieldType[] types = partial.getFieldTypes();
		final int[] values = partial.getValues();

		for (int i = 0; i < types.length; i++) {
			if(types[i] == DateTimeFieldType.dayOfWeek()) {
				result += values[i] * 10000;
			} else if(types[i] == DateTimeFieldType.clockhourOfDay()) {
				result += values[i] * 100;
			} else if(types[i] == DateTimeFieldType.minuteOfHour()) {
				result += values[i];
			}
		}

		return result;
	}
}
