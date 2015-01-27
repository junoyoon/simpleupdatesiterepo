/*
 * Copyright 2010 NHN Corp. All rights Reserved.
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package hudson.plugins.simpleupdatesite.gen;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import hudson.plugins.simpleupdatesite.props.Props;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UpdateSite representation including {@link hudson.plugins.simpleupdatesite.gen.Plugin}
 * 
 * @author JunHo Yoon (junoyoon@gmail.com)
 */
public class UpdateSite {
    public static final String[] HPI_EXT = new String[]{"hpi"};
    private static Logger LOG = LoggerFactory.getLogger(UpdateSite.class);

    public static Props props = Props.props();

    public final int updateCenterVersion = 1;
	public List<Plugin> plugins = new ArrayList<Plugin>();

    private UpdateSite() {
    }

    public static UpdateSite createUpdateSite(File updateCenterBasePath, URI urlBasePath) {
        UpdateSite updateSite = new UpdateSite();
        updateSite.init(updateCenterBasePath, urlBasePath);
        return updateSite;
    }

    public static UpdateSite createUpdateSite(File updateCenterBasePath) {
        return createUpdateSite(updateCenterBasePath, props.getBaseurl());
    }


    /**
	 * Init {@link UpdateSite} class with the .hpi files. This method should be
	 * called after {@link UpdateSite} object is created, to construct update
	 * info.
	 * 
	 * @param updateCenterBasePath
	 *            the file path in which the "plugins" folder exist
	 * @param urlBasePath
	 *            base URL for downloading hpi files.
	 */
	@SuppressWarnings("unchecked")
	public void init(File updateCenterBasePath, URI urlBasePath) {
        LOG.info("UpdateSite will be available at {}/{}", urlBasePath, props.getName());

        Collection<File> collection = (Collection<File>) FileUtils.listFiles(updateCenterBasePath, HPI_EXT, false);
        LOG.info("Found {} hpi files... Regenerate json...", collection.size());

        for (File hpiFile :  collection) {
			try {
				Plugin plugin = new Plugin();
				plugin.init(hpiFile, urlBasePath);
				this.plugins.add(plugin);
			} catch (IOException e) {
				System.out.printf("Fail to get the %s info\n", hpiFile.getName());
			}
		}
	}

	/**
	 * Convert {@link UpdateSite} to JSON format
	 * 
	 * @return converted {@link JSONObject}
	 */
	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("updateCenterVersion", this.updateCenterVersion);
		jsonObject.put("id", props.getUcId());
		JSONObject pluginsJSON = new JSONObject();
		for (Plugin plugin : this.plugins) {
			pluginsJSON.element(plugin.getName(), plugin.toJSON());
		}
		jsonObject.put("plugins", pluginsJSON);
		return jsonObject;
	}

	/**
	 * Convert {@link UpdateSite} to JSON String including JSONP callback
	 * function.
	 * 
	 * @return conveted JSON String
	 */
	public String toUpdateCenterJSONString() {
		return "updateCenter.post(\n" + toJSON().toString(4) + "\n);";
	}


    public void saveTo(File file) {
        LOG.info("Save json to {}", file.getAbsolutePath());
        try {
            FileUtils.writeStringToFile(file, toUpdateCenterJSONString());
        } catch (IOException e) {
            throw new RuntimeException(String.format("Can't save json to file %s", file.getAbsolutePath()), e);
        }
    }

    public void save() {
        saveTo(new File(props.getSaveto(), props.getName()));
    }
}
