package se.github.jaxrs.service;

import static se.github.jaxrs.loader.ContextLoader.getBean;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.activation.UnsupportedDataTypeException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import se.github.jaxrs.jsonupdater.JsonFieldUpdater;
import se.github.logger.MultiLogger;
import se.github.springlab.model.User;
import se.github.springlab.model.WorkItem;
import se.github.springlab.repository.WorkItemRepository;
import se.github.springlab.service.TaskerService;

@Path("/items")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class WorkItemService
{
	@Context
	UriInfo uriInfo;

	private static TaskerService service = getBean(TaskerService.class);
	private static WorkItemRepository workItemRepo = service.getWorkItemRepository();

	static
	{
		MultiLogger.createLogger("WorkItemServiceLog");
	}

	@POST
	public Response create(WorkItem item)
	{
		WorkItem newItem = service.update(item);
		URI location = uriInfo.getAbsolutePathBuilder().path(getClass(), "getOne").build(item.getId());

		return Response.ok(newItem).contentLocation(location).build();
	}

	@GET
	public Response get()
	{
		if (uriInfo.getQueryParameters().isEmpty())
		{
			Collection<User> result = new HashSet<>();
			userRepo.findAll().forEach(e -> result.add(e));
			GenericEntity<Collection<User>> entity = new GenericEntity<Collection<User>>(result)
			{
			};

			return Response.ok(entity).build();
		}
		if (uriInfo.getQueryParameters().containsKey("getBy"))
		{
			return getByQuery();
		}
		if (uriInfo.getQueryParameters().containsKey("searchBy"))
		{
			return searchByQuery();
		}
		throw new WebApplicationException(Status.BAD_REQUEST);

	}

	//getBy
	private Response getByQuery()
	{
		if (uriInfo.getQueryParameters().getFirst("getBy").equals("team"))
		{
			Long id = Long.parseLong(uriInfo.getQueryParameters().getFirst("id"));
			Collection<User> result = userRepo.findByTeamId(id);
			if (result.isEmpty())
			{
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			GenericEntity<Collection<User>> entity = new GenericEntity<Collection<User>>(result)
			{
			};

			return Response.ok(entity).build();
		}
		throw new WebApplicationException(Status.BAD_REQUEST);
	}

	//searchBy
	private Response searchByQuery()
	{
		switch (uriInfo.getQueryParameters().getFirst("searchBy"))
		{
		case "firstName":
		{
			String firstName = uriInfo.getQueryParameters().getFirst("q");
			List<User> firstNames = userRepo.findByFirstNameLike(firstName);
			if (firstNames.isEmpty())
			{
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			GenericEntity<List<User>> firstNamesEntity = new GenericEntity<List<User>>(firstNames)
			{
			};

			return Response.ok(firstNamesEntity).build();
		}
		case "lastName":
		{
			String lastName = uriInfo.getQueryParameters().getFirst("q");
			List<User> lastNames = userRepo.findByLastNameLike(lastName);
			if (lastNames.isEmpty())
			{
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			GenericEntity<List<User>> lastNamesEntity = new GenericEntity<List<User>>(lastNames)
			{
			};

			return Response.ok(lastNamesEntity).build();
		}
		case "username":
		{
			String username = uriInfo.getQueryParameters().getFirst("q");
			List<User> usernames = userRepo.findByUsernameLike(username);
			if (usernames.isEmpty())
			{
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			GenericEntity<List<User>> usernamesEntity = new GenericEntity<List<User>>(usernames)
			{
			};

			return Response.ok(usernamesEntity).build();
		}

		case "userNumber":
		{
			String userNumber = uriInfo.getQueryParameters().getFirst("q");
			User user = userRepo.findByUserNumber(userNumber);
			if (user == null)
			{
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			return Response.ok(user).build();
		}

		default:
			throw new WebApplicationException(Status.BAD_REQUEST);
		}//switch
	}

	@PUT
	@Path("{id}")
	public WorkItem update(@PathParam("id") Long id, String json) throws UnsupportedDataTypeException, IllegalArgumentException, IllegalAccessException
	{
		JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
		WorkItem workItem = workItemRepo.findOne(id);
		JsonFieldUpdater.modifyWithJson(workItem, jsonObject);

		return service.update(workItem);
	}

	@GET
	public Response getAll()
	{
		Collection<WorkItem> result = new HashSet<>();
		workItemRepo.findAll().forEach(e -> result.add(e));
		GenericEntity<Collection<WorkItem>> entity = new GenericEntity<Collection<WorkItem>>(result)
		{
		};

		return Response.ok(entity).build();
	}

	@GET
	@Path("{id}")
	public WorkItem getOne(@PathParam("id") Long id)
	{
		if (workItemRepo.exists(id))
		{
			return workItemRepo.findOne(id);
		}
		throw new WebApplicationException(Status.NOT_FOUND);
	}

	@DELETE
	@Path("{id}")
	public Response remove(@PathParam("id") Long id)
	{
		if (workItemRepo.exists(id))
		{
			workItemRepo.delete(id);
			return Response.ok().build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}
}
