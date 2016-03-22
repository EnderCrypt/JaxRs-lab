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

import se.github.springlab.model.User;
import se.github.springlab.repository.UserRepository;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserService
{
	private static UserRepository userRepo = getBean(UserRepository.class);

	static
	{
		userRepo.save(new User("Olle", "Ollesson", "olle37", "qwerty1234", "1002"));
	}

	@GET
	Response getAll()
	{
		Collection<User> result = new HashSet<>();
		userRepo.findAll().forEach(e -> result.add(e));
		GenericEntity<Collection<User>> entity = new GenericEntity<Collection<User>>(result)
		{
		};

		return Response.ok(entity).build();
	}

	@POST
	public User create(User user)
	{
		return userRepo.save(user);
	}

	@DELETE
	public Response remove(Long id)
	{
		if (userRepo.exists(id))
		{
			userRepo.delete(id);
			return Response.noContent().build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}

	@PUT
	public User update(User user)
	{
		userRepo.save(user);
		return user;
	}

}
