package hudson.plugins.simpleupdatesite.files;

import com.google.common.collect.FluentIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static hudson.plugins.simpleupdatesite.files.ToPathWatchEvent.toPathWatchEvent;
import static hudson.plugins.simpleupdatesite.files.WatchEventExtension.hasExt;
import static hudson.plugins.simpleupdatesite.gen.UpdateSite.createUpdateSite;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * User: lanwen
 * Date: 26.01.15
 * Time: 12:43
 */
public class WatchFiles extends Thread {

    private static Logger LOG = LoggerFactory.getLogger(WatchFiles.class);

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
        LOG.info("Start to watch for: {}", path);
        try {
            // get the first event before looping
            WatchKey key = watcher.take();
            while (key != null) {
                if(FluentIterable.from(key.pollEvents()).transform(toPathWatchEvent()).anyMatch(hasExt(".hpi"))) {
                    LOG.info("HPI list modify found!");
                    createUpdateSite(path.toFile()).save();
                }

                key.reset();
                key = watcher.take();
            }
        } catch (InterruptedException e) {
            System.out.println("Cancelled watch service");
        }
        System.out.println("Stopping thread");
    }
}
