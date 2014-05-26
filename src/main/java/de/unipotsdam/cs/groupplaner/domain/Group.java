package de.unipotsdam.cs.groupplaner.domain;

public class Group {
	final private Integer id;
	final private String name;

	public Group(final Integer id, final String name) {
		this.id = id;
		this.name = name;
	}

	public Group(final String name) {
		this.id = null;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
