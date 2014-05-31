package de.unipotsdam.cs.groupplaner.repository;

import de.unipotsdam.cs.groupplaner.domain.InvitationState;
import de.unipotsdam.cs.groupplaner.domain.Member;
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
import java.util.List;
import java.util.Map;

@Repository
public class InvitationRepository {

	@Autowired
	private DataSource dataSource;
	
	public List<Member> getMembersOfGroup(final Integer groupId) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		return template.query("SELECT user.email, user.name, invites.status, invites.lastModified, invites.invitor FROM user LEFT JOIN invites ON user.email = invites.invitee WHERE invites.groupId=?", new MemberRowMapper(), groupId);
	}

	public Member getMember(final String email, final Integer groupId) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		return template.queryForObject("SELECT user.email, user.name, invites.status, invites.lastModified, invites.invitor FROM user LEFT JOIN invites ON user.email = invites.invitee WHERE invites.groupId=? AND invites.invitee=?", new MemberRowMapper(), groupId, email);
	}

	public Member getMember(final Integer inviteId) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		return template.queryForObject("SELECT user.email, user.name, invites.status, invites.lastModified, invites.invitor FROM user LEFT JOIN invites ON user.email = invites.invitee WHERE invites.id=?", new MemberRowMapper(), inviteId);
	}

	public Integer addUserToGroup(final String inviteeMail, final String invitorMail, final Integer groupId, final InvitationState invitationState) {
		SimpleJdbcInsert template = new SimpleJdbcInsert(dataSource);
		template.setTableName("invites");
		template.setGeneratedKeyName("id");

		Map<String, Object> inviteMap = new HashMap<String, Object>();
		inviteMap.put("invitee", inviteeMail);
		inviteMap.put("invitor", invitorMail);
		inviteMap.put("groupId", groupId);
		inviteMap.put("status", invitationState.toString());

		Number key = template.executeAndReturnKey(inviteMap);
		return key.intValue();
	}

	public Boolean updateInviteStatus(final String inviteeMail, final Integer groupId, final InvitationState newStatus) {
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);

		Map<String, Object> inviteMap = new HashMap<String, Object>();
		inviteMap.put("invitee", inviteeMail);
		inviteMap.put("groupId", groupId);
		inviteMap.put("status", newStatus.toString());
		int rowsAffected = template.update("UPDATE invites SET status=:status WHERE invitee=:invitee AND groupId=:groupId", inviteMap);
		return rowsAffected == 1;
	}

	private static class MemberRowMapper implements RowMapper<Member> {
		@Override
		public Member mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			InvitationState invitationState = InvitationState.valueOf(resultSet.getString("status"));
			return new Member(resultSet.getString("email"), resultSet.getString("name"), invitationState, resultSet.getDate("lastModified"), resultSet.getString("invitor"));
		}
	}
}
