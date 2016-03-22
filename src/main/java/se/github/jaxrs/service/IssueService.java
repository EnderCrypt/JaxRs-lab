package se.github.jaxrs.service;

import static se.github.jaxrs.loader.ContextLoader.getBean;

import java.util.Collection;
import java.util.HashSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import se.github.springlab.model.Issue;
import se.github.springlab.model.WorkItem;
import se.github.springlab.repository.IssueRepository;

@Path("/issues")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IssueService
{
	private static IssueRepository issueRepo = getBean(IssueRepository.class);

	static
	{
		issueRepo.save(new Issue(new WorkItem("topic", "desc"), "ETEASTEST"));
	}

	@GET
	public Response getOne()
	{
		Collection<Issue> result = new HashSet<>();
		issueRepo.findAll().forEach(e -> result.add(e));
		GenericEntity<Collection<Issue>> entity = new GenericEntity<Collection<Issue>>(result)
		{
		};

		return Response.ok(entity).build();
	}

	@POST
	public Issue create(Issue issue)
	{
		return issueRepo.save(issue);

	}

	@DELETE
	public Response remove(Long id)
	{
		if (issueRepo.exists(id))
		{
			issueRepo.delete(id);
			return Response.noContent().build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}

	@PUT
	public Issue update(Issue issue)
	{
		issueRepo.save(issue);
		return issue;
	}
}
