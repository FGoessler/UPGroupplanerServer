package de.unipotsdam.cs.groupplaner.domain;

import de.unipotsdam.cs.groupplaner.support.PartialToIntegerConverter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.Partial;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PeriodDate {
	protected final Partial start;
	protected final Partial end;

	public PeriodDate(final Partial start, final Partial end) {
		this.end = end;
		this.start = start;
	}

	public PeriodDate(final Integer start, final Integer end) {
		this.end = PartialToIntegerConverter.createPartialFromDBInt(end);
		this.start = PartialToIntegerConverter.createPartialFromDBInt(start);
	}

	public Integer getStart() {
		return PartialToIntegerConverter.createDBIntFromPartial(start);
	}

	public Integer getEnd() {
		return PartialToIntegerConverter.createDBIntFromPartial(end);
	}

	@JsonIgnore
	public Partial getStartAsPartial() {
		return start;
	}

	@JsonIgnore
	public Partial getEndAsPartial() {
		return end;
	}
}
