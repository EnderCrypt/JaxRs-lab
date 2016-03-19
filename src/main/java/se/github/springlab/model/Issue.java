package se.github.springlab.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import se.github.jaxrs.jsonupdater.JsonUpdatable;
import se.github.springlab.status.IssueStatus;

@Entity
public class Issue extends Id
{
	@JsonUpdatable
	@Column(nullable = false)
	private String description;

	@Column(name = "status", nullable = false)
	private int issueStatus;

	@OneToOne //(cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	private WorkItem workItem;

	protected Issue()
	{
	}

	public Issue(WorkItem workItem, String description)
	{
		this.workItem = workItem;
		this.description = description;
	}

	public String getDescription()
	{
		return description;
	}

	public IssueStatus getStatus()
	{
		return IssueStatus.values()[issueStatus];
	}

	public void setStatus(IssueStatus issueStatus)
	{
		this.issueStatus = issueStatus.ordinal();
	}

	public void setWorkItem(WorkItem workItem)
	{
		this.workItem = workItem;
	}

	public WorkItem getWorkItem()
	{
		return workItem;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((workItem == null) ? 0 : workItem.hashCode());
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
		if (obj instanceof Issue)
		{
			Issue otherIssue = (Issue) obj;
			return getDescription().equals(otherIssue.getDescription()) &&
					getWorkItem().equals(otherIssue.getWorkItem());
		}
		return false;
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}
