package de.unipotsdam.cs.groupplaner.service;

public interface TimetableImportService {
	public void importUsersTimetableFromPULS(final String userEmail, final String password);
}
