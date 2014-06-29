package de.unipotsdam.cs.groupplaner.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BlockedDate extends PeriodDate {
	final private Integer id;
	final private String userEmail;
	final private String source;


	public BlockedDate(final int start, final int end, final String userEmail, String source) {
		this(null, start, end, userEmail, source);
	}

	public BlockedDate(final Integer id, final int start, final int end, final String userEmail, String source) {
		super(start, end);
		this.id = id;
		this.userEmail = userEmail;
		this.source = source;
	}

	public Integer getId() {
		return id;
	}

	@JsonIgnore
	public String getUserEmail() {
		return userEmail;
	}

	public String getSource() {
		return source;
	}
}
