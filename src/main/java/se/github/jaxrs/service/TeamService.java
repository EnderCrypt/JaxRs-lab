package se.github.jaxrs.service;

import static se.github.jaxrs.loader.ContextLoader.getBean;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import se.github.springlab.model.Team;
import se.github.springlab.repository.TeamRepository;

@Path("/teams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeamService extends AbstractService
{
	private static TeamRepository teamRepo = getBean(TeamRepository.class);
	private static Map<Long, Team> teams = new HashMap<>();

	static
	{
		teamRepo.save(new Team("yhc3l"));
	}

	@GET
	public Response getAll()
	{
		for (Team team : teamRepo.findAll())
		{
			teams.put(team.getId(), team);
		}
		Collection<Team> result = teams.values();
		GenericEntity<Collection<Team>> entity = new GenericEntity<Collection<Team>>(result)
		{
		};

		return Response.ok(entity).build();
	}

}
