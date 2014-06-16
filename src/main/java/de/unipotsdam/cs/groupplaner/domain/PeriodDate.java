package de.unipotsdam.cs.groupplaner.domain;

import de.unipotsdam.cs.groupplaner.support.PartialToIntegerConverter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.Partial;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PeriodDate {
	protected final Partial start;
	protected final Partial end;

	public PeriodDate(final Partial end, final Partial start) {
		this.end = end;
		this.start = start;
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
