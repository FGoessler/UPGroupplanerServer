package de.unipotsdam.cs.groupplaner.repository;

import de.unipotsdam.cs.groupplaner.domain.Group;
import de.unipotsdam.cs.groupplaner.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class InvitationRepository {

	@Autowired
	private DataSource dataSource;
	
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
	
	public Boolean inviteUserToGroup(final User invitee, final User invitor, final Group group) {
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
		Map<String, Object> inviteMap = new HashMap<String, Object>();
		inviteMap.put("invitee", invitee.getEmail());
		inviteMap.put("invitor", invitor.getEmail());
		inviteMap.put("groupId", group.getId());
		int rowsAffected = template.update("INSERT INTO invites (invitee, invitor, groupId, status) VALUES (:invitee, :invitor, :groupId, 'invited')", inviteMap);
		return rowsAffected == 1;
	}

	public Boolean acceptInviteOfUserToGroup(final User invitee, final Group group) {
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
		Map<String, Object> inviteMap = new HashMap<String, Object>();
		inviteMap.put("invitee", invitee.getEmail());
		inviteMap.put("groupId", group.getId());
		int rowsAffected = template.update("UPDATE invites SET status='accepted' WHERE invitee=:invitee AND groupId=:groupId", inviteMap);
		return rowsAffected == 1;
	}
}
