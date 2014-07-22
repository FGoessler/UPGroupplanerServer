package de.unipotsdam.cs.groupplaner.timetableimporter.impl;

import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.config.ExternalAPIEndpoints;
import de.unipotsdam.cs.groupplaner.domain.dates.BlockedDate;
import de.unipotsdam.cs.groupplaner.timetableimporter.TimetableImportService;
import de.unipotsdam.cs.groupplaner.user.dao.BlockedDatesDAO;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class TimetableImportServiceImpl implements TimetableImportService {

	private static final String TIMETABLE_IMPORTER_SOURCE_KEY = "TimetableImporter";

	@Autowired
	private BlockedDatesDAO blockedDatesDAO;
	@Autowired
	private Logger logger;

	@Override
	public void importUsersTimetableFromPULS(String userEmail, String password) {
		final List<BlockedDate> blockedDates = loadTimetableDates(userEmail, password);

		if (blockedDates != null) {
			final ImmutableList<BlockedDate> oldDates = blockedDatesDAO.getBlockedDates(userEmail, TIMETABLE_IMPORTER_SOURCE_KEY);
			for (BlockedDate date : oldDates) {
				blockedDatesDAO.deleteBlockedDate(date.getId());
			}

			for (BlockedDate blockedDate : blockedDates) {
				blockedDatesDAO.createBlockedDate(blockedDate);
			}
		}
	}


	private List<BlockedDate> loadTimetableDates(String userEmail, String password) {
		List<BlockedDate> timetableDates;

		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(createTimetableRequestUrl(userEmail, password));
			CloseableHttpResponse response1 = httpClient.execute(httpGet);

			try {
				final HttpEntity entity1 = response1.getEntity();

				final String responseString = EntityUtils.toString(entity1);
				timetableDates = parseTimetableResponseString(userEmail, responseString);

				EntityUtils.consume(entity1);
			} finally {
				response1.close();
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getLocalizedMessage());
			timetableDates = null;
		}

		return timetableDates;
	}

	private List<BlockedDate> parseTimetableResponseString(final String userEmail, final String responseString) throws JSONException {
		List<BlockedDate> timetableDates = new ArrayList<BlockedDate>();

		final JSONArray jsonCourses = new JSONArray(responseString);
		for (int i = 0; i < jsonCourses.length(); i++) {
			final JSONObject course = jsonCourses.getJSONObject(i);
			if (course.getString("current").equals("1")) {
				final JSONArray dates = course.getJSONArray("dates");
				for (int j = 0; j < dates.length(); j++) {
					final JSONObject date = dates.getJSONObject(j);
					final Integer weekday = Integer.parseInt(date.getString("weekdaynr")) - 1;    // api counts weekdays 1(=monday) to 7 , we count 0(=monday) to 6

					final Integer beginHours = Integer.parseInt(date.getString("begin").substring(0, 2));
					final Integer beginMinutes = Integer.parseInt(date.getString("begin").substring(2, 4));
					final Integer begin = weekday * 24 * 60 + beginHours * 60 + beginMinutes;

					final Integer endHours = Integer.parseInt(date.getString("end").substring(0, 2));
					final Integer endMinutes = Integer.parseInt(date.getString("end").substring(2, 4));
					final Integer end = weekday * 24 * 60 + endHours * 60 + endMinutes;

					final BlockedDate blocked = new BlockedDate(begin, end, userEmail, TIMETABLE_IMPORTER_SOURCE_KEY);
					timetableDates.add(blocked);
				}
			}
		}

		return timetableDates;
	}

	private String createTimetableRequestUrl(final String userEmail, final String password) {
		final String username = userEmail.replace("@uni-potsdam.de", "");
		return ExternalAPIEndpoints.PHP_API_ENDPOINT + "?action=course&user=" + username + "&password=" + password + "&auth=" + ExternalAPIEndpoints.PHP_API_KEY + "&datatype=json";
	}
}
