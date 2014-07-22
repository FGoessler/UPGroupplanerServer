package de.unipotsdam.cs.groupplaner.domain.dates;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AcceptedDate extends PeriodDate {
	final private Integer id;
	final private Integer group;


	public AcceptedDate(final Integer start, final Integer end, final Integer groupId) {
		this(null, start, end, groupId);
	}

	public AcceptedDate(final Integer id, final int start, final int end, final Integer groupId) {
		super(start, end);
		this.id = id;
		this.group = groupId;
	}

	public Integer getId() {
		return id;
	}

	public Integer getGroup() {
		return group;
	}

}
