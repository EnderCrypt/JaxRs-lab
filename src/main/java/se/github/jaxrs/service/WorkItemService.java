package se.github.jaxrs.service;

import static se.github.jaxrs.loader.ContextLoader.getBean;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;

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

import se.github.springlab.model.WorkItem;
import se.github.springlab.repository.WorkItemRepository;

@Path("/items")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class WorkItemService
{
	private static WorkItemRepository itemRepo = getBean(WorkItemRepository.class);

	@Context
	UriInfo uriInfo;

	@POST
	public Response create(WorkItem item)
	{
		WorkItem newItem = itemRepo.save(item);
		URI location = uriInfo.getAbsolutePathBuilder().path(getClass(), "getOne").build(item.getId());

		return Response.ok(newItem).contentLocation(location).build();
	}

	@GET
	public Response getAll()
	{
		Collection<WorkItem> result = new HashSet<>();
		itemRepo.findAll().forEach(e -> result.add(e));
		GenericEntity<Collection<WorkItem>> entity = new GenericEntity<Collection<WorkItem>>(result)
		{
		};

		return Response.ok(entity).build();
	}

	@GET
	@Path("{id}")
	public Response getOne(@PathParam("id") Long id)
	{
		if (itemRepo.exists(id))
		{
			return Response.ok().build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}

	@DELETE
	@Path("{id}")
	public Response remove(@PathParam("id") Long id)
	{
		if (itemRepo.exists(id))
		{
			itemRepo.delete(id);
			return Response.noContent().build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}

	@PUT
	public WorkItem update(WorkItem item)
	{
		itemRepo.save(item);
		return item;
	}
}
