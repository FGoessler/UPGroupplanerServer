package de.unipotsdam.cs.groupplaner.group.dao;

import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.domain.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GroupDAO {

	@Autowired
	private DataSource dataSource;


	public ImmutableList<Group> getGroupsOfUser(final String email) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		final List<Group> groups = template.query("SELECT * FROM groups LEFT JOIN invites ON groups.id = invites.groupId WHERE invites.invitee=? AND (invites.status='INVITED' OR invites.status='ACCEPTED')", new GroupRowMapper(), email);
		return ImmutableList.copyOf(groups);
	}

	public Group getGroup(final Integer id) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		return template.queryForObject("SELECT * FROM groups WHERE id=?", new GroupRowMapper(), id);
	}

	/**
	 * Creates the given group in the database.
	 *
	 * @param newGroup The group to insert in the database.
	 * @return The database id of the created group.
	 */
	public Integer createGroup(final Group newGroup) {
		SimpleJdbcInsert template = new SimpleJdbcInsert(dataSource);
		template.setTableName("groups");
		template.setGeneratedKeyName("id");
		Map<String, Object> groupMap = new HashMap<String, Object>();
		groupMap.put("name", newGroup.getName());
		Number key = template.executeAndReturnKey(groupMap);
		return key.intValue();
	}

	/**
	 * Deletes the group and all associated dates and invites.
	 */
	public Boolean deleteGroup(final Integer id) {
		JdbcTemplate template = new JdbcTemplate(dataSource);

		// this also deletes all dates and invites cause of cascade rules defined via MySQL
		int rowsAffected = template.update("DELETE FROM groups WHERE id=?", id);

		return rowsAffected == 1;
	}

	public Boolean updateGroup(final Group updatedGroup) {
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
		Map<String, Object> groupMap = new HashMap<String, Object>();
		groupMap.put("id", updatedGroup.getId());
		groupMap.put("name", updatedGroup.getName());
		int rowsAffected = template.update("UPDATE groups SET name=:name WHERE id=:id", groupMap);
		return rowsAffected == 1;
	}

	private static class GroupRowMapper implements org.springframework.jdbc.core.RowMapper<Group> {
		@Override
		public Group mapRow(final ResultSet resultSet, final int i) throws SQLException {
			return new Group(resultSet.getInt("id"), resultSet.getString("name"));
		}
	}
}
