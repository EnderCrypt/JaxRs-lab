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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.ws.rs.core.UriInfo;

import se.github.jaxrs.jsonupdater.JsonConverter;
import se.github.jaxrs.jsonupdater.JsonFieldUpdater;
import se.github.logger.MultiLogger;
import se.github.springlab.model.WorkItem;
import se.github.springlab.repository.WorkItemRepository;

@Path("/items")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class WorkItemService
{
	@Context
	UriInfo uriInfo;

	private static WorkItemRepository workItemRepo = getBean(WorkItemRepository.class);

	static
	{
		MultiLogger.createLogger("WorkitemServiceLog");
		JsonFieldUpdater.addTypeSupport(WorkItem.class, new JsonConverter()
		{
			@Override
			public Object call(JsonElement element)
			{
				Long id = element.getAsLong();
				return workItemRepo.findOne(id);
			}
		});
	}

	@POST
	public Response create(WorkItem item)
	{
		WorkItem newItem = workItemRepo.save(item);
		URI location = uriInfo.getAbsolutePathBuilder().path(getClass(), "getOne").build(item.getId());

		return Response.ok(newItem).contentLocation(location).build();
	}

	@PUT
	@Path("{id}")
	public WorkItem update(@PathParam("id") Long id, String json) throws UnsupportedDataTypeException, IllegalArgumentException, IllegalAccessException
	{
		JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
		WorkItem workItem = workItemRepo.findOne(id);
		JsonFieldUpdater.modifyWithJson(workItem, jsonObject);

		return workItemRepo.save(workItem);
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
	public Response getOne(@PathParam("id") Long id)
	{
		if (workItemRepo.exists(id))
		{
			return Response.ok().build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}

	@DELETE
	@Path("{id}")
	public Response remove(@PathParam("id") Long id)
	{
		if (workItemRepo.exists(id))
		{
			workItemRepo.delete(id);
			return Response.noContent().build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}
}
