package hudson.plugins.simpleupdatesite.files;

import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: lanwen
 * Date: 26.01.15
 * Time: 13:00
 */
public class WatchStarter extends AbstractLifeCycle.AbstractLifeCycleListener {

    private ExecutorService executor;
    private Thread watch;

    public WatchStarter(Thread watch) {
        this.watch = watch;
    }

    @Override
    public void lifeCycleStarting(LifeCycle event) {
        executor = Executors.newSingleThreadExecutor();
        executor.submit(watch);
    }

    @Override
    public void lifeCycleStopping(LifeCycle event) {
        executor.shutdown();
    }

}
