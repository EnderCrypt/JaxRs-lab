package se.github.jaxrs.jsonupdater;

import java.lang.reflect.Field;

/**
 * @author EnderCrypt
 *
 */
public class UpdateableField
{
	private Field field;
	private Class<?> fieldType;

	public UpdateableField(Field field)
	{
		this.field = field;
		fieldType = field.getType();
	}

	public void update(Object object, Object value) throws IllegalArgumentException, IllegalAccessException
	{
		field.set(object, value);
	}

	public Field getField()
	{
		return field;
	}

	public Class<?> getFieldType()
	{
		return fieldType;
	}
}
