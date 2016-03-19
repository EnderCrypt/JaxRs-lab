package se.github.jaxrs.jsonupdater;

import com.google.gson.JsonElement;

@FunctionalInterface
public interface JsonConverter
{
	public Object call(JsonElement element);
}
