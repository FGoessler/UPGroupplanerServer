package de.unipotsdam.cs.groupplaner.domain;

import de.unipotsdam.cs.groupplaner.support.PartialToIntegerConverter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.Partial;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AcceptedDate {
	final private Integer id;
	final private Partial start;
	final private Partial end;
	final private Integer group;

	public AcceptedDate(final Partial start, final Partial end, final Integer groupId) {
		this(null, start, end, groupId);
	}

	public AcceptedDate(final int start, final int end, final Integer groupId) {
		this(null, start, end, groupId);
	}

	public AcceptedDate(final Integer id, final Partial start, final Partial end, final Integer groupId) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.group = groupId;
	}

	public AcceptedDate(final Integer id, final int start, final int end, final Integer groupId) {
		this(id, PartialToIntegerConverter.createPartialFromDBInt(start), PartialToIntegerConverter.createPartialFromDBInt(end), groupId);
	}

	public Integer getId() {
		return id;
	}

	public Integer getStart() {
		return PartialToIntegerConverter.createDBIntFromPartial(start);
	}

	public Integer getEnd() {
		return PartialToIntegerConverter.createDBIntFromPartial(end);
	}

	public Integer getGroup() {
		return group;
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
