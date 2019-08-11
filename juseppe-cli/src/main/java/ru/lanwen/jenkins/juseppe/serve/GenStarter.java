package ru.lanwen.jenkins.juseppe.serve;

import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import ru.lanwen.jenkins.juseppe.gen.UpdateSiteGen;
import ru.lanwen.jenkins.juseppe.props.Props;

/**
 * User: lanwen
 * Date: 27.01.15
 * Time: 13:00
 */
public class GenStarter extends AbstractLifeCycle.AbstractLifeCycleListener {

    private Props props;

    public GenStarter(Props props) {
        this.props = props;
    }

    @Override
    public void lifeCycleStarted(LifeCycle event) {
        UpdateSiteGen.updateSite(props).withDefaults().toSave().saveAll();
    }
}
