package ru.lanwen.jenkins.juseppe.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static com.google.common.collect.FluentIterable.from;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static ru.lanwen.jenkins.juseppe.files.ToPathWatchEvent.toPathWatchEvent;
import static ru.lanwen.jenkins.juseppe.files.WatchEventExtension.hasExt;
import static ru.lanwen.jenkins.juseppe.gen.UpdateSiteGen.createUpdateSite;

/**
 * User: lanwen
 * Date: 26.01.15
 * Time: 12:43
 */
public class WatchFiles extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(WatchFiles.class);

    private WatchService watcher;
    private Path path;

    private WatchFiles() {
        setDaemon(true);
    }

    public WatchFiles configureFor(Path path) throws IOException {
        this.path = path;

        watcher = this.path.getFileSystem().newWatchService();
        this.path.register(watcher,
                ENTRY_CREATE,
                ENTRY_DELETE,
                ENTRY_MODIFY
        );
        return this;
    }

    public static WatchFiles watchFor(Path path) throws IOException {
        return new WatchFiles().configureFor(path);
    }


    @Override
    public void run() {
        LOG.info("Start to watch for changes: {}", path);
        try {
            // get the first event before looping
            WatchKey key = watcher.take();
            while (key != null) {

                if (from(key.pollEvents()).transform(toPathWatchEvent()).anyMatch(hasExt(".hpi"))) {
                    LOG.trace("HPI list modify found!");
                    createUpdateSite(path.toFile()).save();
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
