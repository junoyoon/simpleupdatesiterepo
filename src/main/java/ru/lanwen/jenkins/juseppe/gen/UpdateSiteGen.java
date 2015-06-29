package ru.lanwen.jenkins.juseppe.gen;

import ru.lanwen.jenkins.juseppe.beans.Plugin;
import ru.lanwen.jenkins.juseppe.beans.UpdateSite;
import ru.lanwen.jenkins.juseppe.props.Props;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lanwen.jenkins.juseppe.util.Marshaller;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Collection;

/**
 * @author Merkushev Kirill (github: lanwen)
 */
public class UpdateSiteGen {

    public static final String[] PLUGIN_EXT = new String[]{"hpi"};
    private static final Logger LOG = LoggerFactory.getLogger(UpdateSiteGen.class);

    public static Props props = Props.props();

    private UpdateSite site = new UpdateSite()
            .withUpdateCenterVersion(Props.UPDATE_CENTER_VERSION)
            .withId(props.getUcId());

    private UpdateSiteGen() {
    }

    public static UpdateSiteGen createUpdateSite(File updateCenterBasePath, URI urlBasePath) {
        return new UpdateSiteGen().init(updateCenterBasePath, urlBasePath);
    }

    public static UpdateSiteGen createUpdateSite(File updateCenterBasePath) {
        return createUpdateSite(updateCenterBasePath, props.getBaseurl());
    }


    /**
     * Init {@link UpdateSiteGen} class with the .hpi files. This method should be
     * called after {@link UpdateSiteGen} object is created, to construct update
     * info.
     *
     * @param updateCenterBasePath the file path in which the "plugins" folder exist
     * @param urlBasePath          base URL for downloading hpi files.
     */
    public UpdateSiteGen init(File updateCenterBasePath, final URI urlBasePath) {
        LOG.info("UpdateSite will be available at {}/{}", urlBasePath, props.getName());

        Collection<File> collection = FileUtils.listFiles(updateCenterBasePath, PLUGIN_EXT, false);
        LOG.info("Found {} hpi files in {}... Regenerate json...",
                collection.size(), updateCenterBasePath.getAbsolutePath());

        for (File hpiFile : collection) {
            try {
                Plugin plugin = HPI.loadHPI(hpiFile)
                        .withUrl(String.format("%s/%s", urlBasePath, hpiFile.getName()));

                this.site.getPlugins().add(plugin);

            } catch (Exception e) {
                LOG.error("Fail to get the {} info", hpiFile.getAbsolutePath(), e);
            }
        }

        try {
            site.setSignature(new Signer().sign(site));
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Can't generate signature", e);
        }

        return this;
    }

    /**
     * Convert {@link UpdateSite} to JSON String including JSONP callback
     * function.
     *
     * @return conveted JSON String
     */
    public String asJsonp() {
        String json = Marshaller.serializer().toJson(site);
        return String.format("updateCenter.post(%n%s%n);", json);
    }


    public void saveTo(File file) {
        LOG.info("Save json to {}", file.getAbsolutePath());
        try {
            FileUtils.writeStringToFile(file, asJsonp());
        } catch (IOException e) {
            throw new RuntimeException(String.format("Can't save json to file %s", file.getAbsolutePath()), e);
        }
    }

    public void save() {
        saveTo(new File(props.getSaveto(), props.getName()));
    }

}
