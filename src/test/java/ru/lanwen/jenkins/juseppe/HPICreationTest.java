package ru.lanwen.jenkins.juseppe;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.lanwen.jenkins.juseppe.gen.UpdateSiteGen;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static com.google.common.io.Resources.getResource;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class HPICreationTest {

    public static final String BASE_URL_OF_SITE = "http://hudson02.nhncorp.com:9080/update";
    public static final String PLUGINS_DIR_CLASSPATH = "tmp/plugins";

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void shouldSaveJsonWithPluginInfo() throws IOException {
        File file = tmp.newFile("temp.json");

        UpdateSiteGen.createUpdateSite(
                new File(getResource(PLUGINS_DIR_CLASSPATH).getFile()),
                URI.create(BASE_URL_OF_SITE)
        ).saveTo(file);

        assertThat(file.exists(), is(true));
        assertThat(file.length(), greaterThan(200l));
    }

    @Test
    public void shouldContainPlugin() throws IOException {
        String json = UpdateSiteGen.createUpdateSite(
                new File(getResource(PLUGINS_DIR_CLASSPATH).getFile()),
                URI.create(BASE_URL_OF_SITE)
        ).asJsonp();

        assertThat(json, containsString("clang-scanbuild-plugin"));
    }
}
