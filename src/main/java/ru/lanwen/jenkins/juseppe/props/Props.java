package ru.lanwen.jenkins.juseppe.props;

import ru.yandex.qatools.properties.PropertyLoader;
import ru.yandex.qatools.properties.annotations.Property;
import ru.yandex.qatools.properties.annotations.Resource;

import java.io.File;
import java.net.URI;

/**
 * User: lanwen
 * Date: 25.01.15
 * Time: 20:38
 */
public class Props {

    public static final int UPDATE_CENTER_VERSION = 1;


    private Props() {
        PropertyLoader.populate(this);
    }

    public static Props props() {
        return new Props();
    }

    @Property("update.center.plugins.dir")
    private String plugins = new File("").getAbsolutePath();

    @Property("update.center.saveto.dir")
    private String saveto = new File("").getAbsolutePath();

    @Property("update.center.json.name")
    private String name = "update-center.json";

    @Property("jetty.port")
    private int port = 8080;

    @Property("update.center.baseurl")
    private URI baseurl = URI.create("http://localhost:8080");

    @Property("update.center.id")
    private String ucId = "juseppe";

    public String getUcId() {
        return ucId;
    }

    public String getPlugins() {
        return plugins;
    }

    public String getName() {
        return name;
    }

    public URI getBaseurl() {
        return baseurl;
    }

    public String getSaveto() {
        return saveto;
    }

    public int getPort() {
        return port;
    }
}
