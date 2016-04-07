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
import se.github.springlab.model.Issue;
import se.github.springlab.repository.IssueRepository;
import se.github.springlab.service.TaskerService;

/**
 * IssueService. Handles issues for specific WorkItem object. Can create,
 * retrieve and remove issues with logic through the TaskerService class
 * 
 * TODO: * finish remove function (check TaskerService.remove(Team team)) *
 * possibly create an IssueProvider for proper json output/input
 *
 */
@Path("/issues")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IssueService
{
	@Context
	UriInfo uriInfo;

	private static TaskerService service = getBean(TaskerService.class);
	private static IssueRepository issueRepo = service.getIssueRepository();

	static
	{
		MultiLogger.createLogger("IssueServiceLog");
	}

	@POST
	public Response create(Issue issue)
	{
		Issue newIssue = service.update(issue);
		URI location = uriInfo.getAbsolutePathBuilder().path(getClass(), "getOne").build(issue.getId());
		MultiLogger.log("IssueServiceLog", Level.INFO, "Created team: " + issue.toString());

		return Response.ok(newIssue).contentLocation(location).build();
	}

	@PUT
	@Path("{id}")
	public Issue update(@PathParam("id") Long id, String json) throws UnsupportedDataTypeException, IllegalArgumentException, IllegalAccessException
	{
		JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
		Issue issue = issueRepo.findOne(id);
		JsonFieldUpdater.modifyWithJson(issue, jsonObject);

		return service.update(issue);
	}

	@GET
	public Response getAll()
	{
		Collection<Issue> result = new HashSet<>();
		issueRepo.findAll().forEach(e -> result.add(e));
		GenericEntity<Collection<Issue>> entity = new GenericEntity<Collection<Issue>>(result)
		{
		};

		return Response.ok(entity).build();
	}

	@GET
	@Path("{id}")
	public Issue getOne(@PathParam("id") Long id)
	{
		if (issueRepo.exists(id))
		{
			return issueRepo.findOne(id);
		}
		throw new WebApplicationException(Status.NOT_FOUND);
	}

	@DELETE
	@Path("{id}")
	public Response remove(@PathParam("id") Long id)
	{
		if (issueRepo.exists(id))
		{
			issueRepo.delete(id);
			return Response.noContent().build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}
}
