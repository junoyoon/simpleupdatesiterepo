package ru.lanwen.jenkins.juseppe.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import ru.lanwen.jenkins.juseppe.beans.Plugin;

import java.lang.reflect.Type;
import java.util.List;

/**
 * User: lanwen
 * Date: 28.01.15
 * Time: 1:34
 */
public class PluginListSerializer implements JsonSerializer<List<Plugin>> {

    public static final Type PLUGIN_LIST_TYPE = new TypeToken<List<Plugin>>() {
    }.getType();

    @Override
    public JsonElement serialize(List<Plugin> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        for (Plugin plugin : src) {
            jsonObject.add(plugin.getName(), context.serialize(plugin));
        }

        return jsonObject;
    }
}