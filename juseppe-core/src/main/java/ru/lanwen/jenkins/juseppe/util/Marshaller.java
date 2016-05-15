package ru.lanwen.jenkins.juseppe.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.lanwen.jenkins.juseppe.beans.UpdateSite;

import static ru.lanwen.jenkins.juseppe.util.PluginListSerializer.asReleaseHistory;
import static ru.lanwen.jenkins.juseppe.util.PluginListSerializer.asUpdateSite;

/**
 * @author lanwen (Merkushev Kirill)
 *         Date: 28.06.15
 */
public final class Marshaller {

    private Marshaller() {
        throw new IllegalAccessError("This class for util purposes");
    }

    public static Gson serializerForUpdateCenter() {
        return new GsonBuilder()
                .registerTypeAdapter(PluginListSerializer.PLUGIN_LIST_TYPE, asUpdateSite())
                .setPrettyPrinting().create();
    }

    public static Gson serializerForReleaseHistory() {
        return new GsonBuilder()
                .registerTypeAdapter(UpdateSite.class, asReleaseHistory())
                .setPrettyPrinting().create();
    }
}
