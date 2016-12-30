/*
 * Copyright 2010 NHN Corp. All rights Reserved.
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package hudson.plugins.simpleupdatesite;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

/**
 * HPI Class for extracting HPI information from hpi file.
 * 
 * @author JunHo Yoon
 */
public class HPI {
	private final Pattern developersPattern = Pattern.compile("([^:]*):([^:]*):([^,]*),?");

	// lazily computed
	public long timestamp;
	public Manifest manifest;
	public String version;
	private String title;
	@SuppressWarnings("unused")
	private String shortName;
	private String wiki;
	private String pluginVersion;
	private String compatibleSinceVersion;
	private List<Dependency> dependencies;
	private List<Developer> developers;
	private String requiredCore;
	private String builtBy;
	private String name;

	public static HPI loadHPI(File file) throws IOException {
		JarFile jarFile = new JarFile(file);
		try {
			ZipEntry entry = jarFile.getEntry("META-INF/MANIFEST.MF");
			HPI hpi = new HPI();
			hpi.init(jarFile.getManifest().getMainAttributes(), entry.getTime());
			return hpi;
		}
		finally {
			jarFile.close();
		}
	}

	private void init(Attributes attributes, long timestamp) throws IOException {
		this.timestamp = timestamp;
		this.name = attributes.getValue("Implementation-Title");
		this.version = attributes.getValue("Implementation-Version");
		this.pluginVersion = attributes.getValue("Plugin-Version");
		this.wiki = attributes.getValue("Url");
		this.shortName = attributes.getValue("Short-Name");
		this.title = attributes.getValue("Long-Name");
		this.compatibleSinceVersion = attributes.getValue("Compatible-Since-Version");
		this.requiredCore = attributes.getValue("Hudson-Version");
		this.setDevelopers(getDevelopers(attributes));
		this.setDependencies(getDependencies(attributes));
		this.builtBy = getBuiltBy(attributes);
	}

	public List<Dependency> getDependencies(Attributes attributes) throws IOException {
		String deps = attributes.getValue("Plugin-Dependencies");
		if (deps == null) {
			return Collections.emptyList();
		}

		List<Dependency> result = new ArrayList<Dependency>();
		for (String token : deps.split(",")) {
			result.add(new Dependency(token));
		}
		return result;
	}

	public List<Developer> getDevelopers(Attributes attributes) throws IOException {
		String devs = attributes.getValue("Plugin-Developers");
		if (devs == null || devs.trim().length() == 0) {
			return Collections.emptyList();
		}

		List<Developer> result = new ArrayList<Developer>();
		Matcher matcher = this.developersPattern.matcher(devs);
		int totalMatched = 0;
		while (matcher.find()) {
			result.add(new Developer(StringUtils.trimToEmpty(matcher.group(1)), StringUtils.trimToEmpty(matcher
				.group(2)), StringUtils.trimToEmpty(matcher.group(3))));
			totalMatched += matcher.end() - matcher.start();
		}
		if (totalMatched < devs.length()) {
			// ignore and move on
			System.err.println("Unparsable developer info: '" + devs.substring(totalMatched) + "'");
		}
		return result;
	}

	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name", this.name);
		jsonObject.put("version", this.version);
		jsonObject.put("wiki", this.wiki);
		jsonObject.put("title", this.title);
		jsonObject.put("version", this.version);
		jsonObject.put("pluginVersion", this.pluginVersion);
		jsonObject.put("buildDate", getTimestampAsString());
		jsonObject.put("requiredCore", this.requiredCore);
		jsonObject.put("compatibleSinceVersion", this.compatibleSinceVersion);
		SimpleDateFormat fisheyeDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.00Z'", Locale.US);
		fisheyeDateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		jsonObject.put("releaseTimestamp", fisheyeDateFormatter.format(this.timestamp));
		JSONArray deps = new JSONArray();
		for (HPI.Dependency dependency : getDependencies()) {
			deps.add(dependency.toJSON());
		}
		jsonObject.put("dependencies", deps);

		JSONArray devs = new JSONArray();
		List<HPI.Developer> devList = getDevelopers();
		if (!devList.isEmpty()) {
			for (HPI.Developer dev : devList) {
				devs.add(dev.toJSON());
			}
		} else {
			devs.add(new HPI.Developer("", this.builtBy, "").toJSON());
		}
		jsonObject.put("developers", devs);
		return jsonObject;
	}

	public String getBuiltBy(Attributes attributes) throws IOException {
		return attributes.getValue("Built-By");
	}

	public String getTimestampAsString() {
		long lastModified = this.timestamp;
		SimpleDateFormat bdf = getDateFormat();
		return bdf.format(lastModified);
	}

	public Date getTimestampAsDate() {
		long lastModified = this.timestamp;
		SimpleDateFormat bdf = getDateFormat();

		Date tsDate = null;

		try {
			tsDate = bdf.parse(bdf.format(new Date(lastModified)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tsDate;
	}

	public static SimpleDateFormat getDateFormat() {
		return new SimpleDateFormat("MMM dd, yyyy", Locale.US);
	}

	public void setDependencies(List<HPI.Dependency> dependencies) {
		this.dependencies = dependencies;
	}

	public List<HPI.Dependency> getDependencies() {
		return this.dependencies;
	}

	public void setDevelopers(List<Developer> developers) {
		this.developers = developers;
	}

	public List<Developer> getDevelopers() {
		return this.developers;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	
	public String getVersion() {
		return this.version;
	}

	public void setWiki(String wiki) {
		this.wiki = wiki;
	}

	public String getWiki() {
		return this.wiki;
	}

	/**
	 * Dependency Class
	 * 
	 * @author JunHo Yoon
	 */
	public static class Dependency {
		public final String name;
		public final String version;
		public final boolean optional;

		Dependency(String token) {
			this.optional = token.endsWith(Dependency.OPTIONAL);
			if (this.optional) {
				token = token.substring(0, token.length() - Dependency.OPTIONAL.length());
			}

			String[] pieces = token.split(":");
			this.name = pieces[0];
			this.version = pieces[1];
		}

		private static final String OPTIONAL = ";resolution:=optional";

		public JSONObject toJSON() {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", this.name);
			jsonObject.put("version", this.version);
			jsonObject.put("optional", this.optional);
			return jsonObject;
		}
	}

	/**
	 * Developer Info Class
	 * 
	 * @author JunHo Yoon
	 */
	public static class Developer {
		public final String name;
		public final String developerId;
		public final String email;

		Developer(String name, String developerId, String email) {
			this.name = name;
			this.developerId = developerId;
			this.email = email;
		}

		public JSONObject toJSON() {
			JSONObject jsonObject = new JSONObject();
			if (!this.name.equals("")) {
				jsonObject.put("name", this.name);
			}
			if (!this.developerId.equals("")) {
				jsonObject.put("developerId", this.developerId);
			}
			if (!this.email.equals("")) {
				jsonObject.put("email", this.email);
			}

			if (!jsonObject.isEmpty()) {
				return jsonObject;
			} else {
				return null;
			}
		}
	}
}
