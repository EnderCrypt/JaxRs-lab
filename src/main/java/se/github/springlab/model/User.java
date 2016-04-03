package se.github.springlab.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import se.github.jaxrs.jsonupdater.JsonUpdatable;

@Entity
@XmlRootElement
public class User extends Id
{
	@XmlElement
	@JsonUpdatable
	@Column(nullable = false)
	private String firstName;

	@XmlElement
	@JsonUpdatable
	@Column(nullable = false)
	private String lastName;

	@XmlElement
	@JsonUpdatable
	@Column(nullable = false, unique = true)
	private String username;

	@XmlElement
	@JsonUpdatable
	@Column(nullable = false)
	private String password;

	@XmlElement
	@JsonUpdatable
	@Column(name = "user_nbr", unique = true, nullable = false)
	private String userNumber;

	@XmlElement
	@JsonUpdatable
	@Column(nullable = false)
	private boolean isActive = true;

	@XmlElement
	@JsonUpdatable
	@ManyToOne //(cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	private Team team;

	protected User()
	{
	}

	public User(String firstName, String lastName, String username, String password, String userNumber)
	{
		this.lastName = lastName;
		this.firstName = firstName;
		this.username = username;
		this.password = password;
		this.userNumber = userNumber;
	}

	public String getUsername()
	{
		return username;
	}

	public String getPassword()
	{
		return password;
	}

	public String getUserNumber()
	{
		return userNumber;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public boolean isActive()
	{
		return isActive;
	}

	public void activate()
	{
		isActive = true;
	}

	public void deactivate()
	{
		isActive = false;
		team = null;
	}

	public void assignTeam(Team team)
	{
		this.team = team;
	}

	public Team getTeam()
	{
		return team;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userNumber == null) ? 0 : userNumber.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object other)
	{
		if (this == other)
		{
			return true;
		}
		if (other == null)
		{
			return false;
		}
		if (other instanceof User)
		{
			User otherUser = (User) other;
			return getUsername().equals(otherUser.getUsername()) &&
					getUserNumber().equals(otherUser.getUserNumber());
		}
		return false;

	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}
