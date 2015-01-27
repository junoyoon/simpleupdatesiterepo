package hudson.plugins.simpleupdatesite.props;

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
@Resource.Classpath("updatesite.properties")
public class Props {

    private Props() {
        PropertyLoader.populate(this);
    }

    public static Props props() {
        return new Props();
    }

    @Property("update.center.plugins")
    private String plugins = new File("/Users/lanwen/git/simpleupdatesiterepo/src/test/resources/tmp/plugins").getAbsolutePath();

    @Property("update.center.saveto")
    private String saveto = plugins;

    @Property("update.center.name")
    private String name = "update-center.json";

    @Property("jetty.port")
    private int port = 8080;

    @Property("update.center.baseurl")
    private URI baseurl = URI.create("http://localhost:" + port);

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
