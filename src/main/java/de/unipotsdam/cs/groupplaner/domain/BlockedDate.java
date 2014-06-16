package de.unipotsdam.cs.groupplaner.domain;

import de.unipotsdam.cs.groupplaner.support.PartialToIntegerConverter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.Partial;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BlockedDate extends PeriodDate {
	final private Integer id;
	final private String userEmail;

	public BlockedDate(final Partial start, final Partial end, final String userEmail) {
		this(null, start, end, userEmail);
	}

	public BlockedDate(final int start, final int end, final String userEmail) {
		this(null, start, end, userEmail);
	}

	public BlockedDate(final Integer id, final Partial start, final Partial end, final String userEmail) {
		super(start, end);
		this.id = id;
		this.userEmail = userEmail;
	}

	public BlockedDate(final Integer id, final int start, final int end, final String userEmail) {
		this(id, PartialToIntegerConverter.createPartialFromDBInt(start), PartialToIntegerConverter.createPartialFromDBInt(end), userEmail);
	}

	public Integer getId() {
		return id;
	}

	@JsonIgnore
	public String getUserEmail() {
		return userEmail;
	}
}
