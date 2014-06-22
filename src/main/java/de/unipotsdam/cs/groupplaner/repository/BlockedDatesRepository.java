package de.unipotsdam.cs.groupplaner.repository;

import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.domain.BlockedDate;
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
public class BlockedDatesRepository {

	@Autowired
	private DataSource dataSource;

	public ImmutableList<BlockedDate> getBlockedDates(final String email) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		return ImmutableList.copyOf(template.query("SELECT * FROM blockedDates WHERE user=?", new BlockedDateRowMapper(), email));
	}

	public ImmutableList<BlockedDate> getBlockedDates(final String email, final String source) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		return ImmutableList.copyOf(template.query("SELECT * FROM blockedDates WHERE user=? AND source=?", new BlockedDateRowMapper(), email, source));
	}

	public BlockedDate getBlockedDate(final Integer id) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		return template.queryForObject("SELECT * FROM blockedDates WHERE id=?", new BlockedDateRowMapper(), id);
	}

	public Integer createBlockedDate(final BlockedDate newBlockedDate) {
		SimpleJdbcInsert template = new SimpleJdbcInsert(dataSource);
		template.setTableName("blockedDates");
		template.setGeneratedKeyName("id");

		Number key = template.executeAndReturnKey(getParamMap(newBlockedDate));
		return key.intValue();
	}

	public Boolean updateBlockedDate(final BlockedDate modifiedBlockedDate) {
		if (modifiedBlockedDate.getId() < 1) return false;

		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);

		int rowsAffected = template.update("UPDATE blockedDates SET start=:start, end=:end, user=:user, source=:source WHERE id=:id", getParamMap(modifiedBlockedDate));
		return rowsAffected == 1;
	}

	public Boolean deleteBlockedDate(final Integer id) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		int rowsAffected = template.update("DELETE FROM blockedDates WHERE id=?", id);
		return rowsAffected == 1;
	}

	private Map<String, Object> getParamMap(final BlockedDate modifiedBlockedDate) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("start", modifiedBlockedDate.getStart());
		paramMap.put("end", modifiedBlockedDate.getEnd());
		paramMap.put("user", modifiedBlockedDate.getUserEmail());
		paramMap.put("id", modifiedBlockedDate.getId());
		paramMap.put("source", modifiedBlockedDate.getSource());
		return paramMap;
	}

	private static class BlockedDateRowMapper implements RowMapper<BlockedDate> {
		@Override
		public BlockedDate mapRow(final ResultSet resultSet, final int i) throws SQLException {
			return new BlockedDate(resultSet.getInt("id"), resultSet.getInt("start"), resultSet.getInt("end"), resultSet.getString("user"), resultSet.getString("source"));
		}
	}
}
