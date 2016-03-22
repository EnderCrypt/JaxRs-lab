package se.github.springlab.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import se.github.jaxrs.jsonupdater.JsonUpdatable;
import se.github.springlab.status.ItemStatus;

@Entity
public class WorkItem extends Id
{
	@JsonUpdatable
	@Column(nullable = false)
	private String topic;

	@JsonUpdatable
	@Column(nullable = false)
	private String description;

	@ManyToOne
	private User assignedUser;

	@JsonUpdatable
	@Column(name = "status", nullable = false)
	private int itemStatus;

	protected WorkItem()
	{
	}

	public WorkItem(String topic, String description)
	{
		this.topic = topic;
		this.description = description;
	}

	public String getTopic()
	{
		return topic;
	}

	public String getDescription()
	{
		return description;
	}

	public User getAssignedUser()
	{
		return assignedUser;
	}

	public void assignUser(User user)
	{
		this.assignedUser = user;
	}

	public ItemStatus getStatus()
	{
		return ItemStatus.values()[itemStatus];
	}

	public void setStatus(ItemStatus ItemStatus)
	{
		this.itemStatus = ItemStatus.ordinal();
	}

	@Override
	public int hashCode()
	{
		final int prime = 37;
		int result = 1;
		result += prime * (getTopic() == null ? 0 : getTopic().hashCode());
		result += prime * (getDescription() == null ? 0 : getDescription().hashCode());
		result += prime * (getAssignedUser() == null ? 0 : getAssignedUser().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (obj instanceof WorkItem)
		{
			WorkItem otherObj = (WorkItem) obj;
			return getTopic().equals(otherObj.getTopic()) &&
					getDescription().equals(otherObj.getDescription()) &&
					getAssignedUser().equals(otherObj.getAssignedUser());
		}
		return false;
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}
