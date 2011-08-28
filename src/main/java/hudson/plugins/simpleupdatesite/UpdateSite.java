/*
 * Copyright 2010 NHN Corp. All rights Reserved.
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package hudson.plugins.simpleupdatesite;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;

/**
 * UpdateSite representation including {@link Plugin}
 * 
 * @author JunHo Yoon (junoyoon@gmail.com)
 */
public class UpdateSite {
	public final int updateCenterVersion = 1;
	public List<Plugin> plugins = new ArrayList<Plugin>();
	public final String id = "simpleupdatesite";

	/**
	 * Init {@link UpdateSite} class with the .hpi files. This method should be
	 * called after {@link UpdateSite} object is created, to construct update
	 * info.
	 * 
	 * @param updateCenterBasePath
	 *            the file path in which the "plugins" folder exist
	 * @param urlBasePath
	 *            base URL for downloading hpi files.
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void init(File updateCenterBasePath, String urlBasePath) throws IOException {
		String downloadBaseUrl = urlBasePath + "/plugins";
		for (File hpiFile : (Collection<File>) FileUtils.listFiles(new File(updateCenterBasePath, "plugins"),
			new String[] { "hpi" }, false)) {
			try {
				Plugin plugin = new Plugin();
				plugin.init(hpiFile, downloadBaseUrl);
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
		jsonObject.put("id", this.id);
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
}
