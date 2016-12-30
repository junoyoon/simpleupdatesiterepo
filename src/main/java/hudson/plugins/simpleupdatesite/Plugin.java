/*
 * Copyright 2010 NHN Corp. All rights Reserved.
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package hudson.plugins.simpleupdatesite;

import java.io.File;
import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Plugin info This class has each hpi specific info and the corresponding info
 * including except and download url.
 * 
 * @author JunHo Yoon (junoyoon@gmail.com)
 */
public class Plugin {
	private HPI hpi;
	private String excerpt;
	private String url;

	/**
	 * Init
	 * 
	 * @param hpiFile
	 *            the hpi file
	 * @param baseUrl
	 * @throws IOException
	 */
	public void init(File hpiFile, String baseUrl) throws IOException {
		this.hpi = HPI.loadHPI(hpiFile);
		this.excerpt = getExcerpt(hpiFile);
		this.excerpt = this.excerpt.replace("\r\n", "<br/>\n");
		String wiki = getWiki(hpiFile);
		if (StringUtils.isNotBlank(wiki)) {
			this.hpi.setWiki(wiki);
		}
		this.url = String.format("%s/%s.hpi", baseUrl, this.hpi.getName());
	}

	public String getName() {
		return this.hpi.getName();
	}
	
	public String getVersion() {
		return this.hpi.getVersion();
	}

	private String getExcerpt(File hpiFile) {
		try {
			String baseName = FilenameUtils.getBaseName(hpiFile.getName());
			File exceptFile = new File(hpiFile.getParent(), baseName + ".info");
			if (exceptFile.exists()) {
				return FileUtils.readFileToString(exceptFile, "UTF-8");
			} else {
				return "";
			}
		} catch (Exception e) {
			return "";
		}
	}

	private String getWiki(File hpiFile) {
		try {
			String baseName = FilenameUtils.getBaseName(hpiFile.getName());
			File exceptFile = new File(hpiFile.getParent(), baseName + ".wiki");
			if (exceptFile.exists()) {
				return FileUtils.readFileToString(exceptFile, "UTF-8");
			} else {
				return "";
			}
		} catch (Exception e) {
			return "";
		}
	}

	JSONObject toJSON() {
		JSONObject json = this.hpi.toJSON();
		json.element("excerpt", this.excerpt);
		json.element("url", this.url);
		return json;
	}
}
