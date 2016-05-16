package ru.lanwen.jenkins.juseppe.gen.view;

import ru.lanwen.jenkins.juseppe.beans.UpdateSite;
import ru.lanwen.jenkins.juseppe.util.Marshaller;

/**
 * @author lanwen (Merkushev Kirill)
 */
public class JsonpUpdateSite implements UpdateSiteView {

    private UpdateSite site;
    private String name;

    public JsonpUpdateSite(UpdateSite site, String name) {
        this.site = site;
        this.name = name;
    }

    /**
     * Convert {@link UpdateSite} to JSON String including JSONP callback
     * function.
     *
     * @return conveted JSON String
     */
    @Override
    public String content() {
        String json = Marshaller.serializerForUpdateCenter().toJson(site);
        return String.format("updateCenter.post(%n%s%n);", json);
    }

    @Override
    public String name() {
        return name;
    }
}
