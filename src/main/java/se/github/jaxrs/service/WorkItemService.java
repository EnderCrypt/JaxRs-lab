package se.github.jaxrs.service;

import static se.github.jaxrs.loader.ContextLoader.getBean;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
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
import se.github.springlab.model.WorkItem;
import se.github.springlab.repository.WorkItemRepository;
import se.github.springlab.service.TaskerService;
import se.github.springlab.status.ItemStatus;

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
		MultiLogger.log("WorkItemServiceLog", Level.INFO, "Created workitem: " + item.toString());
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
		for (Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet())
		{
			String key = entry.getKey();
			String searchToken = uriInfo.getQueryParameters().getFirst(key);
			switch (key.toLowerCase())
			{
			case "getby":
				return getByQuery(searchToken);
			case "searchby":
				return searchByQuery(searchToken);
			}
		}
		throw new WebApplicationException(Status.BAD_REQUEST);
	}

	private long getQueryID()
	{
		try
		{
			return Long.parseLong(uriInfo.getQueryParameters().getFirst("q"));
		}
		catch (NumberFormatException e)
		{
			throw new WebApplicationException("Query must be a number");
		}
	}

	//getBy
	private Response getByQuery(String searchToken)
	{
		Collection<WorkItem> result = null;
		switch (searchToken.toLowerCase())
		{
		case "issue":
			result = service.getItemsWithIssue();
			break;
		case "team":
			result = workItemRepo.findByAssignedUser_Team_Id(getQueryID());
			break;
		case "status":
			int status = (int) getQueryID();
			int validStatus = ItemStatus.values().length;
			if (status < 0 || status > validStatus)
			{
				throw new WebApplicationException("Invalid status. Valid range: 0+" + validStatus, Status.BAD_REQUEST);
			}
			result = workItemRepo.findByItemStatus(status);
			break;
		case "user":
			result = workItemRepo.findByAssignedUser_Id(getQueryID());
			break;
		default:
			throw new WebApplicationException("Unknown getBy query", Status.BAD_REQUEST);
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
	private Response searchByQuery(String key)
	{
		String searchId = String.valueOf(getQueryID());
		if (key.equalsIgnoreCase("description") || key.equalsIgnoreCase("desc"))
		{
			List<WorkItem> workItems = workItemRepo.findByDescription(searchId);
			if (workItems.isEmpty())
			{
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			GenericEntity<List<WorkItem>> workItemEntity = new GenericEntity<List<WorkItem>>(workItems)
			{
			};
			return Response.ok(workItemEntity).build();
		}
		throw new WebApplicationException("Invalid search token! Valid tokens: description, desc", Status.BAD_REQUEST);
	}

	@PUT
	@Path("{id}")
	public WorkItem update(@PathParam("id") Long id, String json) throws UnsupportedDataTypeException, IllegalArgumentException, IllegalAccessException
	{
		JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
		WorkItem workItem = workItemRepo.findOne(id);
		try
		{
			JsonFieldUpdater.modifyWithJson(workItem, jsonObject);
		}
		catch (UnsupportedDataTypeException e)
		{
			throw new WebApplicationException(e.getMessage(), Status.BAD_REQUEST);
		}

		return service.update(workItem);
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
			service.removeItem(workItemRepo.findOne(id));
			return Response.ok().build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}
}
