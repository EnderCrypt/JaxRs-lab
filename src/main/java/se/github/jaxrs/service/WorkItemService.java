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
			Collection<WorkItem> result = new HashSet<>();
			workItemRepo.findAll().forEach(e -> result.add(e));
			GenericEntity<Collection<WorkItem>> entity = new GenericEntity<Collection<WorkItem>>(result)
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

	private long getQueryID()
	{
		long id = -1L;
		try
		{
			id = Long.parseLong(uriInfo.getQueryParameters().getFirst("id"));
		}
		catch (NumberFormatException e)
		{
			throw new WebApplicationException("ID must be a number");
		}
		return id;
	}

	//getBy
	private Response getByQuery()
	{
		String getQuery = uriInfo.getQueryParameters().getFirst("getBy").toLowerCase();
		Collection<WorkItem> result = null;
		switch (getQuery)
		{
		case "issue":
			// TODO: no idea
			break;
		case "team":
			// TODO: MISSING access to user repo
			break;
		case "status":
			result = workItemRepo.findByItemStatus((int) getQueryID());
			break;
		case "user":
			// TODO: i dont freaking have userRepo, cant get a user to check with
			break;
		default:
			throw new WebApplicationException("Unknown getBy query");
		}
		if (result.isEmpty())
		{
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		GenericEntity<Collection<WorkItem>> entity = new GenericEntity<Collection<WorkItem>>(result)
		{
		};
		return Response.ok(entity).build();
	}

	// searchBy
	private Response searchByQuery()
	{
		if (uriInfo.getQueryParameters().getFirst("searchBy").equals("description"))
		{
			String workItem = uriInfo.getQueryParameters().getFirst("q");
			List<WorkItem> workItems = workItemRepo.findByDescriptionLike(workItem);
			if (workItems.isEmpty())
			{
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			GenericEntity<List<WorkItem>> workItemEntity = new GenericEntity<List<WorkItem>>(workItems)
			{
			};
			return Response.ok(workItemEntity).build();
		}
		throw new WebApplicationException(Status.NOT_FOUND);

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
