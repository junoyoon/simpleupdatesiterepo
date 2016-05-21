package ru.lanwen.jenkins.juseppe.cli;

import io.airlift.airline.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lanwen.jenkins.juseppe.gen.UpdateSiteGen;
import ru.lanwen.jenkins.juseppe.props.Props;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static ru.lanwen.jenkins.juseppe.files.WatchFiles.watchFor;

/**
 * @author lanwen (Merkushev Kirill)
 */
@Command(name = "generate", description = "just generate json with Juseppe without starting jetty server")
public class GenerateCommand extends JuseppeCommand {
    private static final Logger LOG = LoggerFactory.getLogger(GenerateCommand.class);

    @Override
    public void unsafeRun(Props props) throws Exception {
        UpdateSiteGen.updateSite(props).withDefaults().toSave().saveAll();
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        if (isWatch()) {
            Runtime.getRuntime().addShutdownHook(stopOnShutdown(executorService));
            executorService.submit(watchFor(props)).get();
        }
    }

    private Thread stopOnShutdown(final ExecutorService executorService) {
        return new Thread() {
            @Override
            public void run() {
                executorService.shutdownNow();
                try {
                    executorService.awaitTermination(2, TimeUnit.SECONDS);
                } catch (InterruptedException ignored) {
                    LOG.trace("Watcher didn't stop properly");
                }
            }
        };
    }
}
