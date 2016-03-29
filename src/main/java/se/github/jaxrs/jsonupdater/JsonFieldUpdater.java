package se.github.jaxrs.jsonupdater;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.activation.UnsupportedDataTypeException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author EnderCrypt Class designed to allow json to quickly and dynamically
 *         modify any java object
 */
public class JsonFieldUpdater
{
	private static HashMap<Class<?>, HashMap<String, UpdateableField>> fields = new HashMap<>();
	private static HashMap<Class<?>, JsonConverter> converters = new HashMap<>();

	private JsonFieldUpdater()
	{
	}

	/**
	 * adds the most basic primitive converts to make it easy for you to conver
	 * most json properties into many java types
	 */
	static
	{
		// @formatter:off
		addTypeSupport(boolean.class, e -> e.getAsBoolean());
		addTypeSupport(int.class, e -> e.getAsInt());
		addTypeSupport(double.class, e -> e.getAsDouble());
		addTypeSupport(long.class, e -> e.getAsLong());
		addTypeSupport(char.class, e -> e.getAsCharacter());
		addTypeSupport(String.class, e -> e.getAsString());
		// @formatter:@on
	}

	/**
	 * adds a converter, which allows a json value be transformed into something
	 * that java can accept most few basic prmivitives (and string) are
	 * automatically added using {@link init()}
	 * 
	 * @param clazz
	 * @param jsonConverter
	 */
	public static void addTypeSupport(Class<?> clazz, JsonConverter jsonConverter)
	{
		converters.put(clazz, jsonConverter);
	}

	/**
	 * scans through the json object for properties and replaces the values in
	 * the java objects from the json fields ignores any unknown json properties
	 * 
	 * @param object
	 *            to have its fields modified
	 * @param json
	 *            object with fields in to replace in the java obj
	 * @throws UnsupportedDataTypeException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void modifyWithJson(Object object, JsonObject json) throws UnsupportedDataTypeException, IllegalArgumentException, IllegalAccessException
	{
		Class<?> objectClass = object.getClass();

		scanClass(objectClass);

		HashMap<String, UpdateableField> classFields = fields.get(objectClass);
		for (Entry<String, JsonElement> entry : json.entrySet())
		{
			JsonElement jsonEntryValue = entry.getValue();
			String jsonEntryName = entry.getKey();
			UpdateableField objectField = classFields.get(jsonEntryName);
			if (objectField != null) // check if the field found in json exists in this java object
			{
				Class<?> objectFieldType = objectField.getFieldType();
				JsonConverter jsonConverter = converters.get(objectFieldType);
				if (jsonConverter == null) // check if the field is of the same type as in json
				{
					throw new UnsupportedDataTypeException("the field type " + objectFieldType.getName() + " is not supported");
				}
				else
				{
					objectField.update(object, jsonConverter.call(jsonEntryValue));
				}
			}
		}
	}

	/**
	 * scans through a class after JsonUpdateable annotations to make it more
	 * easily modifiable for this class this method is automatically called from
	 * {@link modifyWithJson(Object object, JsonObject json)} so dont call this
	 * unless you wanna pre-load a huge quantity of class
	 * 
	 * @param objectClass
	 *            class to scan through for annotations
	 */
	public static void scanClass(Class<?> objectClass)
	{
		if (fields.containsKey(objectClass) == false)
		{
			HashMap<String, UpdateableField> classFields = new HashMap<>();
			for (Field field : objectClass.getDeclaredFields())
			{
				JsonUpdatable annotation = field.getDeclaredAnnotation(JsonUpdatable.class);
				if (annotation != null)
				{
					field.setAccessible(true);
					String name = annotation.value();
					if (name.equals(""))
					{
						name = field.getName();
					}
					classFields.put(name, new UpdateableField(field));
				}
			}
			fields.put(objectClass, classFields);
		}
	}
}
