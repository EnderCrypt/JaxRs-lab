package se.github.jaxrs.service;

import static se.github.jaxrs.loader.ContextLoader.getBean;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;

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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import se.github.jaxrs.jsonupdater.JsonFieldUpdater;
import se.github.springlab.model.User;
import se.github.springlab.repository.UserRepository;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserService extends AbstractService
{
	private static UserRepository userRepo = getBean(UserRepository.class);

	@Context
	UriInfo uriInfo;

	@POST
	public Response create(User user)
	{
		User newUser = userRepo.save(user);
		URI location = uriInfo.getAbsolutePathBuilder().path(getClass(), "getOne").build(user.getId());
		return Response.ok(newUser).contentLocation(location).build();
	}

	@GET
	public Response getAll()
	{
		Collection<User> result = new HashSet<>();
		userRepo.findAll().forEach(e -> result.add(e));
		GenericEntity<Collection<User>> entity = new GenericEntity<Collection<User>>(result)
		{
		};

		return Response.ok(entity).build();
	}

	@GET
	@Path("query")
	public Response getBy()
	{

		if (uriInfo.getQueryParameters().getFirst("getBy").equals("team"))
		{
			Long id = Long.parseLong(uriInfo.getQueryParameters().getFirst("id"));
			Collection<User> result = new HashSet<>();
			userRepo.findByTeam_Id(id).forEach(e -> result.add(e));
			GenericEntity<Collection<User>> entity = new GenericEntity<Collection<User>>(result)
			{
			};

			return Response.ok(entity).build();

		}

		return null;

	}

	@GET
	@Path("{id}")
	public Response getOne(@PathParam("id") Long id)
	{
		if (userRepo.exists(id))
		{
			return Response.ok().build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}

	@DELETE
	@Path("{id}")
	public Response remove(@PathParam("id") Long id)
	{
		if (userRepo.exists(id))
		{
			userRepo.delete(id);
			return Response.noContent().build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}

	@PUT
	@Path("{id}")
	public User update(@PathParam("id") Long id, String json) throws UnsupportedDataTypeException, IllegalArgumentException, IllegalAccessException
	{
		JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
		User user = userRepo.findOne(id);
		JsonFieldUpdater.modifyWithJson(user, jsonObject);

		return userRepo.save(user);
	}

}
