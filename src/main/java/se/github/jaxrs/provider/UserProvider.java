package se.github.jaxrs.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonWriter;

import se.github.jaxrs.loader.ContextLoader;
import se.github.springlab.model.User;
import se.github.springlab.repository.TeamRepository;
import se.github.springlab.repository.UserRepository;

//@Provider //uncomment to enable custom MBW/MBR for User.class
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class UserProvider implements MessageBodyWriter<User>, MessageBodyReader<User>
{
	private static final Gson gson = new GsonBuilder().registerTypeAdapter(User.class, new UserAdapter()).create();
	private static UserRepository userRepo = ContextLoader.getBean(UserRepository.class);
	private static TeamRepository teamRepo = ContextLoader.getBean(TeamRepository.class);

	//  ============= MessageBodyWriter
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
	{
		return type.isAssignableFrom(User.class);
	}

	@Override
	public long getSize(User t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
	{
		return 0;
	}

	@Override
	public void writeTo(User user, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException, WebApplicationException
	{

		try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(entityStream)))
		{
			gson.toJson(user, User.class, writer);
		}
	}

	//  ============= MessageBodyReader

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
	{
		return type.isAssignableFrom(User.class);
	}

	@Override
	public User readFrom(Class<User> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
			InputStream entityStream) throws IOException, WebApplicationException
	{

		return gson.fromJson(new InputStreamReader(entityStream), User.class);
	}

	// GSON
	private static final class UserAdapter implements JsonSerializer<User>, JsonDeserializer<User>
	{

		@Override
		public JsonElement serialize(User user, Type type, JsonSerializationContext context)
		{

			JsonObject json = new JsonObject();
			json.addProperty("id", user.getId());
			json.addProperty("firstName", user.getFirstName());
			json.addProperty("lastName", user.getLastName());
			json.addProperty("username", user.getUsername());
			json.addProperty("userNumber", user.getUserNumber());
			json.addProperty("password", user.getPassword());
			json.addProperty("isActive", user.isActive());
			if (user.getTeam() != null)
			{
				json.addProperty("team", user.getTeam().getId());
			}

			return json;
		}

		@Override
		public User deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
		{
			JsonObject userJson = json.getAsJsonObject();
			String firstName = userJson.get("firstName").getAsString();
			String lastName = userJson.get("lastName").getAsString();
			String username = userJson.get("username").getAsString();
			String userNumber = userJson.get("userNumber").getAsString();
			String password = userJson.get("password").getAsString();
			boolean userIsActive = userJson.get("isActive").getAsBoolean();

			User user = new User(firstName, lastName, username, password, userNumber);
			if (!userIsActive)
			{
				user.deactivate();
			}
			if (userJson.has("team"))
			{
				Long id = userJson.get("team").getAsLong();
				user.assignTeam(teamRepo.findOne(id));
			}
			return userRepo.save(user);
		}

	}

}
