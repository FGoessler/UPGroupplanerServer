package de.unipotsdam.cs.groupplaner.config;

/**
 * This class contains all configured paths for resources.
 */
public final class PathConfig {
	public static final String BASE_RESOURCE_PATH = "/user";
	public static final String BLOCKED_DATES_RESOURCE_PATH = BASE_RESOURCE_PATH + "/blockedDates";
	public static final String GROUP_RESOURCE_PATH = BASE_RESOURCE_PATH + "/group";
	public static final String GROUP_MEMBER_RESOURCE_PATH = GROUP_RESOURCE_PATH + "/{id}/member";
}
