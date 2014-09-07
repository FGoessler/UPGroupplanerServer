package de.unipotsdam.cs.groupplaner.user.dao;

import de.unipotsdam.cs.groupplaner.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
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
public class UserDAO {

	@Autowired
	private DataSource dataSource;

	public Boolean createUser(final User newUser) {
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("email", newUser.getEmail());
		int rowsAffected = template.update("INSERT INTO user (email) VALUES (:email)", userMap);
		return rowsAffected == 1;
	}

	public User getUser(final String email) {
		JdbcTemplate template = new JdbcTemplate(dataSource);

		try {
			return template.queryForObject("SELECT * FROM user WHERE email=?", new String[]{email}, new UserRowMapper());
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static class UserRowMapper implements RowMapper<User> {
		@Override
		public User mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			return new User(resultSet.getString("email"));
		}
	}
}
