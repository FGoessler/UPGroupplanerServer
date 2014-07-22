package de.unipotsdam.cs.groupplaner.group.dao;

import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.domain.dates.AcceptedDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class AcceptedDatesDAO {

	@Autowired
	private DataSource dataSource;

	public ImmutableList<AcceptedDate> getAcceptedDates(final String user) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		return ImmutableList.copyOf(template.query("SELECT " +
				"acceptedDates.start, acceptedDates.end, acceptedDates.groupId, acceptedDates.id " +
				"FROM groups " +
				"INNER JOIN acceptedDates ON groups.id = acceptedDates.groupId " +
				"LEFT JOIN invites ON groups.id = invites.groupId " +
				"WHERE invites.invitee=?", new AcceptedDateRowMapper(), user));
	}

	public ImmutableList<AcceptedDate> getAcceptedDates(final Integer groupId) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		return ImmutableList.copyOf(template.query("SELECT *  FROM acceptedDates WHERE groupId=?", new AcceptedDateRowMapper(), groupId));
	}

	public AcceptedDate getAcceptedDate(final Integer id) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		return template.queryForObject("SELECT * FROM acceptedDates WHERE id=?", new AcceptedDateRowMapper(), id);
	}

	public Integer createAcceptedDate(final AcceptedDate newDate) {
		SimpleJdbcInsert template = new SimpleJdbcInsert(dataSource);
		template.setTableName("acceptedDates");
		template.setGeneratedKeyName("id");

		Number key = template.executeAndReturnKey(getParamMap(newDate));
		return key.intValue();
	}

	public Boolean updateAcceptedDate(final AcceptedDate modifiedDate) {
		if (modifiedDate.getId() < 1) return false;

		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);

		int rowsAffected = template.update("UPDATE acceptedDates SET start=:start, end=:end, groupId=:groupId WHERE id=:id", getParamMap(modifiedDate));
		return rowsAffected == 1;
	}

	public Boolean deleteAcceptedDate(final Integer id) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		int rowsAffected = template.update("DELETE FROM acceptedDates WHERE id=?", id);
		return rowsAffected == 1;
	}

	private Map<String, Object> getParamMap(final AcceptedDate date) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("start", date.getStart());
		paramMap.put("end", date.getEnd());
		paramMap.put("groupId", date.getGroup());
		if (date.getId() != null && date.getId() > 0) paramMap.put("id", date.getId());
		return paramMap;
	}

	private static class AcceptedDateRowMapper implements RowMapper<AcceptedDate> {
		@Override
		public AcceptedDate mapRow(final ResultSet resultSet, final int i) throws SQLException {
			return new AcceptedDate(resultSet.getInt("id"), resultSet.getInt("start"), resultSet.getInt("end"), resultSet.getInt("groupId"));
		}
	}
}
