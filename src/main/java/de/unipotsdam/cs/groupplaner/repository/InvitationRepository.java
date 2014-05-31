package de.unipotsdam.cs.groupplaner.repository;

import de.unipotsdam.cs.groupplaner.domain.Group;
import de.unipotsdam.cs.groupplaner.domain.InvitationState;
import de.unipotsdam.cs.groupplaner.domain.Member;
import de.unipotsdam.cs.groupplaner.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
	
	//TODO: extract complex methods to a service!
	
	public Boolean isUserMemberOfGroup(final User user, final Group group) {
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("email", user.getEmail());
		paramMap.put("groupId", group.getId());
		final Integer invitesCount = template.queryForObject("SELECT COUNT(*) FROM invites WHERE invitee=:email AND groupId=:groupId", paramMap ,Integer.class);
		
		// if no invites were found check if the reason could be that no such group exists
		if(invitesCount < 1) {
			final Integer groupCount = template.queryForObject("SELECT COUNT(*) FROM groups WHERE id=:groupId", paramMap ,Integer.class);
			if(groupCount < 1) {
				throw new EmptyResultDataAccessException(1);
			}
		}
		
		return invitesCount > 0;
	}
	
	public List<Member> getMembersOfGroup(final Integer groupId) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		return template.query("SELECT user.email, user.name, invites.status, invites.lastModified FROM user LEFT JOIN invites ON user.email = invites.invitee WHERE invites.groupId=?", new MemberRowMapper(), groupId);
	}
	
	public Member getMember(final Integer groupId, final String email) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		return template.queryForObject("SELECT user.email, user.name, invites.status, invites.lastModified FROM user LEFT JOIN invites ON user.email = invites.invitee WHERE invites.groupId=? AND invites.invitee=?", new MemberRowMapper(), groupId, email);
	}
	
	public Boolean inviteUserToGroup(final String inviteeMail, final String invitorMail, final Integer groupId) {
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
		Map<String, Object> inviteMap = new HashMap<String, Object>();
		inviteMap.put("invitee", inviteeMail);
		inviteMap.put("invitor", invitorMail);
		inviteMap.put("groupId", groupId);
		inviteMap.put("status", InvitationState.INVITED.toString());
		int rowsAffected = template.update("INSERT INTO invites (invitee, invitor, groupId, status) VALUES (:invitee, :invitor, :groupId, :status)", inviteMap);
		return rowsAffected == 1;
	}

	public Boolean updateInviteStatus(final String inviteeMail, final Integer groupId, final InvitationState newStatus) {
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
		
		//TODO: check if state is actually invited and not already accepted or something else
		
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
			return new Member(resultSet.getString("email"), resultSet.getString("name"), invitationState, resultSet.getDate("lastModified"));
		}
	}
}
