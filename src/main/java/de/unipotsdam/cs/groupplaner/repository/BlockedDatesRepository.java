package de.unipotsdam.cs.groupplaner.repository;

import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.domain.BlockedDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class BlockedDatesRepository {

	@Autowired
	private DataSource dataSource;

	public ImmutableList<BlockedDate> getBlockedDates(final String email) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		return ImmutableList.copyOf(template.query("SELECT *  FROM blockedDates WHERE user=?", new BlockedDateRowMapper(), email));
	}
	
	public BlockedDate getBlockedDate(final Integer id) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		return template.queryForObject("SELECT * FROM blockedDates WHERE id=?", new BlockedDateRowMapper(), id);
	}

	public Boolean createBlockedDate(final BlockedDate newBlockedDate) {
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("start", newBlockedDate.getStart());
		userMap.put("end", newBlockedDate.getEnd());
		userMap.put("user", newBlockedDate.getUserEmail());
		int rowsAffected = template.update("INSERT INTO blockedDates (start, end, user) VALUES (:start, :end, :user)", userMap);
		return rowsAffected == 1;
	}
	
	public Boolean deleteBlockedDate(final Integer id) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		int rowsAffected = template.update("DELETE * FROM blockedDates WHERE id=?", id);
		return rowsAffected == 1;
	}

	private static class BlockedDateRowMapper implements RowMapper<BlockedDate> {
		@Override
		public BlockedDate mapRow(final ResultSet resultSet, final int i) throws SQLException {
			return new BlockedDate(resultSet.getInt("id"), resultSet.getInt("start"), resultSet.getInt("end"), resultSet.getString("user"));
		}
	}
}
