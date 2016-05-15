package ru.lanwen.jenkins.juseppe.gen.view;

import ru.lanwen.jenkins.juseppe.beans.UpdateSite;

import static ru.lanwen.jenkins.juseppe.util.Marshaller.serializerForReleaseHistory;

/**
 * @author lanwen (Merkushev Kirill)
 */
public class ReleaseHistoryUpdateSite implements UpdateSiteView {

    private UpdateSite site;
    private String name;

    public ReleaseHistoryUpdateSite(UpdateSite site, String name) {
        this.site = site;
        this.name = name;
    }

    @Override
    public String content() {
        return serializerForReleaseHistory().toJson(site);
    }

    @Override
    public String name() {
        return name;
    }
}
