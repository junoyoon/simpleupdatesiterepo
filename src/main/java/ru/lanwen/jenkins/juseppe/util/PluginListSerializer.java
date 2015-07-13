package ru.lanwen.jenkins.juseppe.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import ru.lanwen.jenkins.juseppe.beans.Day;
import ru.lanwen.jenkins.juseppe.beans.Plugin;
import ru.lanwen.jenkins.juseppe.beans.Release;
import ru.lanwen.jenkins.juseppe.beans.UpdateSite;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Joiner.on;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Comparator.comparing;
import static java.util.Locale.ENGLISH;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * User: lanwen
 * Date: 28.01.15
 * Time: 1:34
 */
public final class PluginListSerializer {

    public static final Type PLUGIN_LIST_TYPE = new TypeToken<List<Plugin>>() {
    }.getType();

    private PluginListSerializer() {
    }


    public static JsonSerializer<List<Plugin>> asUpdateSite() {
        return (src, typeOfSrc, context) -> {
            JsonObject jsonObject = new JsonObject();

            for (Plugin plugin : src) {
                jsonObject.add(plugin.getName(), context.serialize(plugin));
            }

            return jsonObject;
        };
    }

    public static JsonSerializer<UpdateSite> asReleaseHistory() {
        return new JsonSerializer<UpdateSite>() {
            @Override
            public JsonElement serialize(UpdateSite src, Type typeOfSrc, JsonSerializationContext context) {
                Map<String, List<Plugin>> byDate = src.getPlugins().stream().collect(groupingBy(Plugin::getBuildDate));

                List<Day> releases = byDate.entrySet().stream().map(entry -> new Day()
                        .withDate(entry.getKey())
                        .withReleases(fromPlugins(entry.getValue())))
                        .sorted(comparing(day -> LocalDate.parse(day.getDate(), ofPattern("MMM dd',' uuuu", ENGLISH))))
                        .collect(toList());

                JsonObject jsonObject = new JsonObject();
                jsonObject.add("releaseHistory", context.serialize(releases));

                return jsonObject;
            }

            private List<Release> fromPlugins(List<Plugin> plugins) {
                return plugins.stream().map(plugin -> new Release()
                                .withGav(on(":").join(plugin.getGroup(), plugin.getName(), plugin.getVersion()))
                                .withTimestamp(plugin.getReleaseTimestamp())
                                .withTitle(plugin.getTitle())
                                .withVersion(plugin.getVersion())
                                .withWiki(plugin.getWiki())
                ).collect(toList());
            }
        };
    }
}
