package ru.lanwen.jenkins.juseppe.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author lanwen (Merkushev Kirill)
 *         Date: 28.06.15
 */
public final class Marshaller {

    private Marshaller() {
        throw new IllegalAccessError("This class for util purposes");
    }

    public static Gson serializer() {
        return new GsonBuilder()
                .registerTypeAdapter(PluginListSerializer.PLUGIN_LIST_TYPE, new PluginListSerializer())
                .setPrettyPrinting().create();
    }
}
