package ru.lanwen.jenkins.juseppe;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;
import ru.lanwen.jenkins.juseppe.gen.UpdateSiteGen;
import ru.lanwen.jenkins.juseppe.props.JuseppeEnvVars;
import ru.lanwen.jenkins.juseppe.props.Props;
import ru.lanwen.jenkins.juseppe.util.JuseppeMatchers;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static com.google.common.io.Resources.getResource;
import static java.lang.System.clearProperty;
import static java.lang.System.setProperty;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static ru.lanwen.jenkins.juseppe.gen.UpdateSiteGen.createUpdateSite;
import static ru.lanwen.jenkins.juseppe.util.JuseppeMatchers.exists;

public class HPICreationTest {

    public static final String BASE_URL_OF_SITE = "http://hudson02.nhncorp.com:9080/update";
    public static final String PLUGINS_DIR_CLASSPATH = "tmp/plugins";

    public static TemporaryFolder tmp = new TemporaryFolder();

    @ClassRule
    public static RuleChain setenv = RuleChain.emptyRuleChain().around(tmp).around(new ExternalResource() {
        @Override
        protected void before() throws Throwable { 
            folderToSave = tmp.newFolder();
            System.out.println(folderToSave.getAbsolutePath());
            clearProperty(JuseppeEnvVars.JUSEPPE_SAVE_TO_DIR);
            setProperty(JuseppeEnvVars.JUSEPPE_SAVE_TO_DIR, folderToSave.getAbsolutePath());
            Props.props().reset();
        }
    });

    private static File folderToSave;

    @Test
    public void shouldSaveJsonWithPluginInfo() throws IOException {
        File file = tmp.newFile("temp.json");

        UpdateSiteGen updateSite = createUpdateSite(
                new File(getResource(PLUGINS_DIR_CLASSPATH).getFile()),
                URI.create(BASE_URL_OF_SITE)
        );
        updateSite.saveTo(file, updateSite.updateCenterJsonp());

        assertThat(file, exists());
        assertThat(file.length(), greaterThan(200l));
    }

    @Test
    public void shouldSaveJsonWithReleaseHistory() throws IOException {
        File file = new File(folderToSave, Props.props().getReleaseHistoryJsonName());

        UpdateSiteGen updateSite = createUpdateSite(new File(getResource(PLUGINS_DIR_CLASSPATH).getFile()));

        updateSite.save();

        assertThat(file, exists());
        assertThat(file.length(), greaterThan(200l));
    }

    @Test
    public void shouldContainPlugin() throws IOException {
        String json = createUpdateSite(
                new File(getResource(PLUGINS_DIR_CLASSPATH).getFile()),
                URI.create(BASE_URL_OF_SITE)
        ).updateCenterJsonp();

        assertThat(json, containsString("clang-scanbuild-plugin"));
    }
}
