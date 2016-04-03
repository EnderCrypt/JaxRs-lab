package se.github.jaxrs.service;

import static se.github.jaxrs.loader.ContextLoader.getBean;

import com.google.gson.JsonElement;

import se.github.jaxrs.jsonupdater.JsonConverter;
import se.github.jaxrs.jsonupdater.JsonFieldUpdater;
import se.github.springlab.model.Issue;
import se.github.springlab.model.Team;
import se.github.springlab.model.User;
import se.github.springlab.model.WorkItem;
import se.github.springlab.repository.IssueRepository;
import se.github.springlab.repository.TeamRepository;
import se.github.springlab.repository.UserRepository;
import se.github.springlab.repository.WorkItemRepository;

public abstract class AbstractService
{
	static
	{
		JsonFieldUpdater.addTypeSupport(Team.class, new JsonConverter()
		{
			@Override
			public Object call(JsonElement element)
			{
				Long id = element.getAsLong();
				return getBean(TeamRepository.class).findOne(id);
			}
		});
		JsonFieldUpdater.addTypeSupport(User.class, new JsonConverter()
		{
			@Override
			public Object call(JsonElement element)
			{
				Long id = element.getAsLong();
				return getBean(UserRepository.class).findOne(id);
			}
		});
		JsonFieldUpdater.addTypeSupport(WorkItem.class, new JsonConverter()
		{
			@Override
			public Object call(JsonElement element)
			{
				Long id = element.getAsLong();
				return getBean(WorkItemRepository.class).findOne(id);
			}
		});
		JsonFieldUpdater.addTypeSupport(Issue.class, new JsonConverter()
		{
			@Override
			public Object call(JsonElement element)
			{
				Long id = element.getAsLong();
				return getBean(IssueRepository.class).findOne(id);
			}
		});
	}

}
