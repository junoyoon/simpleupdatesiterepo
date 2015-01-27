package ru.lanwen.jenkins.juseppe.gen;

import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;

import java.nio.file.Path;

/**
 * User: lanwen
 * Date: 27.01.15
 * Time: 13:00
 */
public class GenStarter extends AbstractLifeCycle.AbstractLifeCycleListener {

    private Path path;

    public GenStarter(Path path) {
        this.path = path;
    }

    @Override
    public void lifeCycleStarted(LifeCycle event) {
         UpdateSiteGen.createUpdateSite(path.toFile()).save();
    }
}
