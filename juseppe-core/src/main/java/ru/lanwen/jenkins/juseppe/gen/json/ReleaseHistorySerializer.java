package ru.lanwen.jenkins.juseppe.gen.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ru.lanwen.jenkins.juseppe.beans.Day;
import ru.lanwen.jenkins.juseppe.beans.Plugin;
import ru.lanwen.jenkins.juseppe.beans.Release;
import ru.lanwen.jenkins.juseppe.beans.UpdateSite;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Comparator.comparing;
import static java.util.Locale.ENGLISH;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;

/**
 * User: lanwen
 * Date: 28.01.15
 * Time: 1:34
 */
public final class ReleaseHistorySerializer implements JsonSerializer<UpdateSite> {

    private ReleaseHistorySerializer() {
    }

    public static Gson serializer() {
        return new GsonBuilder()
                .registerTypeAdapter(UpdateSite.class, asReleaseHistory())
                .setPrettyPrinting().create();
    }

    private static ReleaseHistorySerializer asReleaseHistory() {
        return new ReleaseHistorySerializer();
    }

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
                .withGav(
                        of(
                                plugin.getGroup(),
                                plugin.getName(),
                                plugin.getVersion()
                        ).collect(joining(":"))
                )
                .withUrl(plugin.getUrl())
                .withTimestamp(plugin.getReleaseTimestamp())
                .withTitle(plugin.getTitle())
                .withVersion(plugin.getVersion())
                .withWiki(plugin.getWiki())
        ).collect(toList());
    }
}
