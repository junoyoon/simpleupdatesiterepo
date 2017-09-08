package ru.lanwen.jenkins.juseppe;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;
import ru.lanwen.jenkins.juseppe.gen.SavableSite;
import ru.lanwen.jenkins.juseppe.gen.UpdateSiteGen;
import ru.lanwen.jenkins.juseppe.gen.view.UpdateSiteView;
import ru.lanwen.jenkins.juseppe.props.JuseppeEnvVars;
import ru.lanwen.jenkins.juseppe.props.Props;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static com.google.common.io.Resources.getResource;
import static java.lang.System.clearProperty;
import static java.lang.System.setProperty;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static ru.lanwen.jenkins.juseppe.gen.UpdateSiteGen.updateSite;
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
            clearProperty(JuseppeEnvVars.JuseppeEnvEnum.JUSEPPE_SAVE_TO_DIR.mapping());
            setProperty(JuseppeEnvVars.JuseppeEnvEnum.JUSEPPE_SAVE_TO_DIR.mapping(), folderToSave.getAbsolutePath());
        }
    });

    private static File folderToSave;

    @Test
    public void shouldSaveJsonWithPluginInfo() throws IOException {
        File file = tmp.newFile("temp.json");

        updateSite(
                Props.populated()
                        .withPluginsDir(getResource(PLUGINS_DIR_CLASSPATH).getFile())
                        .withBaseurl(URI.create(BASE_URL_OF_SITE))
                        .withSaveto(file.getParent())
                        .withUcJsonName(file.getName())
        ).withDefaults().toSave().saveAll();

        assertThat(file, exists());
        assertThat(file.length(), greaterThan(200l));
    }

    @Test
    public void shouldSaveJsonWithReleaseHistory() throws IOException {
        File file = new File(folderToSave, Props.populated().getReleaseHistoryJsonName());

        UpdateSiteGen.updateSite(Props.populated()
                .withPluginsDir(getResource(PLUGINS_DIR_CLASSPATH).getFile())).withDefaults().toSave().saveAll();

        assertThat(file, exists());
        assertThat(file.length(), greaterThan(200l));
    }

    @Test
    public void shouldContainPlugin() throws IOException {
        List<String> contents = UpdateSiteGen.updateSite(Props.populated()
                .withPluginsDir(getResource(PLUGINS_DIR_CLASSPATH).getFile())).withDefaults().toSave()
                .savables().stream()
                .map(SavableSite::getView)
                .map(UpdateSiteView::content)
                .collect(toList());

        assertThat(contents, everyItem(containsString("clang-scanbuild-plugin")));
        assertThat(contents, hasItem(containsString(Props.populated().getBaseurl() + "/clang-scanbuild-plugin.hpi")));
        assertThat(contents, hasItem(containsString(Props.populated().getBaseurl() + "/plugins2/clang-scanbuild-plugin.hpi")));
    }
}
