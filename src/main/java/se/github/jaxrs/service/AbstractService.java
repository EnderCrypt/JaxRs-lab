package se.github.jaxrs.service;

import se.github.jaxrs.jsonupdater.JsonFieldUpdater;

public abstract class AbstractService
{
	//	protected final static BasicLogger logger = new BasicLogger(AbstractService.class.getName());

	static
	{
		JsonFieldUpdater.init();
	}

}
