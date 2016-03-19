package se.github.springlab.model;

import javax.persistence.GeneratedValue;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Id
{
	@javax.persistence.Id
	@GeneratedValue
	protected Long id;

	public Long getId()
	{
		return id;
	}

	public boolean hasId()
	{
		return id != null;
	}
}
