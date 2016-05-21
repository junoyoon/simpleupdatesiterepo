package ru.lanwen.jenkins.juseppe.gen.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import ru.lanwen.jenkins.juseppe.beans.Plugin;

import java.lang.reflect.Type;
import java.util.List;

import static java.util.Comparator.comparing;

/**
 * @author lanwen (Merkushev Kirill)
 */
public class UpdateSiteSerializer implements JsonSerializer<List<Plugin>> {

    private static final Type PLUGIN_LIST_TYPE = new TypeToken<List<Plugin>>() {
    }.getType();

    private UpdateSiteSerializer() {
    }

    public static Gson serializer() {
        return new GsonBuilder()
                .registerTypeAdapter(PLUGIN_LIST_TYPE, asUpdateSite())
                .setPrettyPrinting().create();
    }

    private static UpdateSiteSerializer asUpdateSite() {
        return new UpdateSiteSerializer();
    }

    @Override
    public JsonElement serialize(List<Plugin> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        src.stream()
                .sorted(comparing(Plugin::getReleaseTimestamp))
                .forEach(plugin -> jsonObject.add(plugin.getName(), context.serialize(plugin)));

        return jsonObject;
    }
}
