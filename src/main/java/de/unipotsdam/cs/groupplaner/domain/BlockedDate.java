package de.unipotsdam.cs.groupplaner.domain;

import de.unipotsdam.cs.groupplaner.support.PartialToIntegerConverter;
import org.codehaus.jackson.annotate.JsonIgnore;
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
		this(id, PartialToIntegerConverter.createPartialFromDBInt(start), PartialToIntegerConverter.createPartialFromDBInt(end), userEmail);
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
}
