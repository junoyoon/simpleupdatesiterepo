package ru.lanwen.jenkins.juseppe.gen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lanwen.jenkins.juseppe.beans.UpdateSite;
import ru.lanwen.jenkins.juseppe.gen.view.JsonpUpdateSite;
import ru.lanwen.jenkins.juseppe.gen.view.ReleaseHistoryUpdateSite;
import ru.lanwen.jenkins.juseppe.props.Props;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

import static java.util.Arrays.asList;

/**
 * @author lanwen (Merkushev Kirill)
 */
public class FilledUpdateSite {

    private static final Logger LOG = LoggerFactory.getLogger(FilledUpdateSite.class);

    private UpdateSite site;
    private Props props;

    public FilledUpdateSite(UpdateSite site, Props props) {
        this.site = site;
        this.props = props;
    }


    private void saveTo(File file, String content) {
        LOG.info("Save json to {}", file.getAbsolutePath());
        try {
            Files.write(file.toPath(), Collections.singleton(content));
        } catch (IOException e) {
            throw new RuntimeException(String.format("Can't save json to file %s", file.getAbsolutePath()), e);
        }
    }
    
    public UpdateSite getSite() {
        return site;
    }

    public void save() {
        asList(
                new JsonpUpdateSite(site, props.getUcJsonName()),
                new ReleaseHistoryUpdateSite(site, props.getReleaseHistoryJsonName())
        ).forEach(view -> saveTo(new File(props.getSaveto(), view.name()), view.content()));
    }
}
