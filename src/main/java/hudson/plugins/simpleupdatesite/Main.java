package hudson.plugins.simpleupdatesite;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class Main {
	private Options options = new Options();
	private String jsonFileName = "update-center.json";
	private String baseUrl;
	private File baseFolder = new File(".");
	private boolean verbose = false;
	private static final String VERSION = "simpleupdatesite - 1.0";

	public Main() {
		OptionGroup requiredOption = new OptionGroup();
		requiredOption.setRequired(true);
		requiredOption.addOption(new Option("u", "baseurl", true, "Base URL in which update-center.json locates"));
		options.addOptionGroup(requiredOption);
		options.addOption("f", "basefolder", true, "Base folder in which plugins folder locates. The current folder is in default");
		options.addOption("n", "jsonfilename", true, "Update info file name. update-center.json is in default");
		options.addOption("v", "verbose", false, "Show the verbose error message");
		options.addOption("h", "help", false, "Show this help");

	}

	public static void main(String[] args) {
		new Main().doMain(args);
	}

	public void showHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(VERSION, options);
	}

	public void doMain(String[] args) {
		try {
			parseArgument(args);

			UpdateSite site = new UpdateSite();
			site.init(baseFolder, baseUrl);
			File file = new File(baseFolder, jsonFileName);
			FileUtils.writeStringToFile(file, site.toUpdateCenterJSONString());
			System.out.println(VERSION);
			System.out.println("-- " + jsonFileName + " is creatated in " + baseFolder);
			System.out.println("-- " + "Please upload " + baseFolder + " folder to " + baseUrl);
		} catch (Exception e) {
			showHelp();
			if (verbose) {
				e.printStackTrace();
			} else {
				System.out.println("ERROR " + e.getMessage());
			}
		}
	}

	void parseArgument(String[] args) throws ParseException, IOException {
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(options, args);

		if (cmd.hasOption("h")) {
			showHelp();
			System.exit(0);
		}

		if (cmd.hasOption("v")) {
			verbose = true;
		}

		baseUrl = cmd.getOptionValue("u");

		try {
			baseUrl = StringUtils.trimToEmpty(baseUrl);
			new URL(baseUrl);
		} catch (MalformedURLException e) {

			throw new ParseException("baseurl '" + baseUrl + "' is malformed.");
		}

		if (cmd.hasOption("n")) {
			jsonFileName = StringUtils.trimToEmpty(cmd.getOptionValue("n"));
		}

		if (cmd.hasOption("f")) {
			baseFolder = new File(cmd.getOptionValue("f"));
		}

		if (!baseFolder.exists()) {
			throw new ParseException("Base folder '" + baseFolder.getCanonicalPath() + "' does not exist.");
		}
		if (!new File(baseFolder, "plugins").exists()) {
			throw new ParseException("In base folder '" + baseFolder.getCanonicalPath() + "', 'plugins' folder does not exist.");
		}
	}
}
