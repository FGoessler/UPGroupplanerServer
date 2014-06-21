package de.unipotsdam.cs.groupplaner.domain;

import de.unipotsdam.cs.groupplaner.support.PartialToIntegerConverter;
import org.joda.time.Partial;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AcceptedDate extends PeriodDate {
	final private Integer id;
	final private Integer group;

	public AcceptedDate(final Partial start, final Partial end, final Integer groupId) {
		this(null, start, end, groupId);
	}

	public AcceptedDate(final int start, final int end, final Integer groupId) {
		this(null, start, end, groupId);
	}

	public AcceptedDate(final Integer id, final Partial start, final Partial end, final Integer groupId) {
		super(start, end);
		this.id = id;
		this.group = groupId;
	}

	public AcceptedDate(final Integer id, final int start, final int end, final Integer groupId) {
		this(id, PartialToIntegerConverter.createPartialFromDBInt(start), PartialToIntegerConverter.createPartialFromDBInt(end), groupId);
	}

	public Integer getId() {
		return id;
	}

	public Integer getGroup() {
		return group;
	}

}
