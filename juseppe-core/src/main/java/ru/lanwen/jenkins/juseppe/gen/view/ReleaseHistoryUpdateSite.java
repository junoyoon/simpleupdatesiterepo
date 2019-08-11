package ru.lanwen.jenkins.juseppe.gen.view;

import ru.lanwen.jenkins.juseppe.beans.UpdateSite;
import ru.lanwen.jenkins.juseppe.gen.json.ReleaseHistorySerializer;

/**
 * @author lanwen (Merkushev Kirill)
 */
public class ReleaseHistoryUpdateSite implements UpdateSiteView {

    private final UpdateSite site;
    private final String name;

    public ReleaseHistoryUpdateSite(UpdateSite site, String name) {
        this.site = site;
        this.name = name;
    }

    @Override
    public String content() {
        return ReleaseHistorySerializer.serializer().toJson(site);
    }

    @Override
    public String name() {
        return name;
    }
}
