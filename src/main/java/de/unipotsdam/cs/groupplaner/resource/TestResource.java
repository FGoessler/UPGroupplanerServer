package de.unipotsdam.cs.groupplaner.resource;

import de.unipotsdam.cs.groupplaner.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Component
@Path("/")
public class TestResource {
	
	@Autowired
	private TestService service;
	
	@Autowired
	private DataSource dataSource;
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response test() {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		final List<Map<String, Object>> data = template.queryForList("SELECT * from user");
		
		return Response.status(200).entity(data).build();
	}
	
}
