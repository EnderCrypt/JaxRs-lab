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
	private static HashMap<Class<?>, HashMap<String, Field>> fields = new HashMap<>();
	private static HashMap<Class<?>, JsonConverter> converters = new HashMap<>();

	private JsonFieldUpdater()
	{
	}

	/**
	 * adds the most basic primitive converts to make it easy for you to conver
	 * most json properties into many java types
	 */
	public static void init()
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
		scanClass(object.getClass());

		Class<?> clazz = object.getClass();
		HashMap<String, Field> classFields = fields.get(clazz);

		for (Entry<String, JsonElement> entry : json.entrySet())
		{
			JsonElement element = entry.getValue();
			String name = entry.getKey();
			Field field = classFields.get(name);
			if (field != null)
			{
				Class<?> fieldType = field.getType();
				JsonConverter jsonConverter = converters.get(fieldType);
				if (jsonConverter == null)
				{
					throw new UnsupportedDataTypeException("the field type " + fieldType.getName() + " is not supported");
				}
				else
				{
					field.set(object, jsonConverter.call(element));
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
	 * @param clazz
	 *            class to scan through for annotations
	 */
	public static void scanClass(Class<?> clazz)
	{
		if (fields.containsKey(clazz) == false)
		{
			HashMap<String, Field> classFields = new HashMap<>();
			for (Field field : clazz.getDeclaredFields())
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
					classFields.put(name, field);
				}
			}
			fields.put(clazz, classFields);
		}
	}
}
