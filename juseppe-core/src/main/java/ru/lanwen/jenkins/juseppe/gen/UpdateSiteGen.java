package ru.lanwen.jenkins.juseppe.gen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lanwen.jenkins.juseppe.beans.UpdateSite;
import ru.lanwen.jenkins.juseppe.gen.source.FilePluginSource;
import ru.lanwen.jenkins.juseppe.props.Props;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.String.format;

/**
 * @author Merkushev Kirill (github: lanwen)
 */
public class UpdateSiteGen {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateSiteGen.class);

    private Props props;
    private UpdateSite site = new UpdateSite();
    private List<Consumer<UpdateSite>> siteConsumers = new ArrayList<>();

    private UpdateSiteGen(Props props) {
        this.props = props;
    }

    public static UpdateSiteGen updateSite() {
        return updateSite(Props.populated());
    }

    public static UpdateSiteGen updateSite(Props config) {
        return new UpdateSiteGen(config);
    }

    public UpdateSiteGen register(Consumer<UpdateSite> siteConsumer) {
        siteConsumers.add(siteConsumer);
        return this;
    }

    public UpdateSiteGen withDefaults() {
        return register(
                site -> site.withUpdateCenterVersion(Props.UPDATE_CENTER_VERSION)
                        .withId(props.getUcId())
        ).register(
                site -> Collections.singleton(new FilePluginSource(props))
                        .forEach(source -> site.getPlugins().addAll(source.plugins()))
        ).register(
                site -> site.getPlugins()
                        .forEach(plugin -> plugin.setUrl(format("%s/%s", props.getBaseurl(), plugin.getUrl())))
        ).register(
                site -> {
                    try {
                        site.setSignature(new Signer().sign(site));
                    } catch (GeneralSecurityException | IOException e) {
                        throw new RuntimeException("Can't generate signature", e);
                    }
                });
    }

    public FilledUpdateSite fill() {
        siteConsumers.forEach(consumer -> consumer.accept(site));
        return new FilledUpdateSite(site, props);
    }
}
