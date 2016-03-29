package se.github.jaxrs.service;

import static se.github.jaxrs.loader.ContextLoader.getBean;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;

import javax.activation.UnsupportedDataTypeException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import se.github.jaxrs.jsonupdater.JsonConverter;
import se.github.jaxrs.jsonupdater.JsonFieldUpdater;
import se.github.logger.MultiLogger;
import se.github.springlab.model.Team;
import se.github.springlab.repository.TeamRepository;

/**
 * DONE: 29/03-2016 Note: - entity Team.class is annotated with @XMLRootElement
 * - several TODOs for appealing purposes
 *
 */

@Path("/teams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeamService extends AbstractService
{
	@Context
	UriInfo uriInfo;

	private static TeamRepository teamRepo = getBean(TeamRepository.class);

	static
	{
		MultiLogger.createLogger("TeamServiceLog");
		JsonFieldUpdater.addTypeSupport(Team.class, new JsonConverter()
		{
			@Override
			public Object call(JsonElement element)
			{
				Long id = element.getAsLong();
				return teamRepo.findOne(id);
			}
		});
	}

	@POST
	public Response create(Team team)
	{
		Team newTeam = teamRepo.save(team);
		URI location = uriInfo.getAbsolutePathBuilder().path(getClass(), "getOne").build(team.getId());
		MultiLogger.log("TeamServiceLog", Level.INFO, "Created team: " + team.toString());

		return Response.ok(newTeam).contentLocation(location).build();
	}

	@PUT
	@Path("{id}")
	public Team update(@PathParam("id") Long id, String json) throws UnsupportedDataTypeException, IllegalArgumentException, IllegalAccessException
	{
		JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
		Team team = teamRepo.findOne(id);
		JsonFieldUpdater.modifyWithJson(team, jsonObject);

		return teamRepo.save(team);
	}

	@GET
	public Response getAll()
	{
		Collection<Team> result = new HashSet<>();
		teamRepo.findAll().forEach(e -> result.add(e));
		GenericEntity<Collection<Team>> entity = new GenericEntity<Collection<Team>>(result)
		{
		};

		return Response.ok(entity).build();
	}

	@GET
	@Path("{id}")
	public Team getOne(@PathParam("id") Long id)
	{
		if (teamRepo.exists(id))
		{
			return teamRepo.findOne(id);
		}
		return null; //TODO: throw proper exception (404 NOT FOUND)
	}

	@DELETE
	@Path("{id}")
	public Response remove(@PathParam("id") Long id)
	{
		if (teamRepo.exists(id))
		{
			teamRepo.delete(id);
			return Response.noContent().build();
		}
		return Response.status(Status.NOT_FOUND).build(); //TODO: throw proper exception (404 NOT FOUND)

	}

}
