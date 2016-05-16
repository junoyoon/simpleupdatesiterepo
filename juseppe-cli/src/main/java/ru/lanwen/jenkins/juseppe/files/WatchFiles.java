package ru.lanwen.jenkins.juseppe.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lanwen.jenkins.juseppe.gen.UpdateSiteGen;
import ru.lanwen.jenkins.juseppe.props.Props;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.lang.String.format;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static ru.lanwen.jenkins.juseppe.files.WatchEventExtension.hasExt;

/**
 * User: lanwen
 * Date: 26.01.15
 * Time: 12:43
 */
public class WatchFiles extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(WatchFiles.class);

    private WatchService watcher;
    private Path path;
    private Props props;

    private WatchFiles() {
        setDaemon(true);
    }

    public WatchFiles configureFor(Props props) throws IOException {
        this.props = props;
        path = Paths.get(props.getPluginsDir());
        setName(format("file-watcher-%s", path.getFileName()));


        watcher = this.path.getFileSystem().newWatchService();
        path.register(watcher,
                ENTRY_CREATE,
                ENTRY_DELETE,
                ENTRY_MODIFY
        );
        return this;
    }

    public static WatchFiles watchFor(Props props) throws IOException {
        return new WatchFiles().configureFor(props);
    }

    @Override
    public void run() {
        LOG.info("Start to watch for changes: {}", path);
        try {
            // get the first event before looping
            WatchKey key = watcher.take();
            while (key != null) {

                if (key.pollEvents().stream().anyMatch(hasExt(".hpi"))) {
                    LOG.trace("HPI list modify found!");
                    UpdateSiteGen.updateSite(props).withDefaults().fill().save();
                }

                key.reset();
                key = watcher.take();
            }
        } catch (InterruptedException e) {
            LOG.debug("Cancelled watch service");
        }
        LOG.info("Stopping to watch {}", path);
    }
}
