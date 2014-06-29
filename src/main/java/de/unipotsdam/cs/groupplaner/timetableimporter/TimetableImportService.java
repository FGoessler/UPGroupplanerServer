package de.unipotsdam.cs.groupplaner.timetableimporter;

public interface TimetableImportService {
	public void importUsersTimetableFromPULS(final String userEmail, final String password);
}
