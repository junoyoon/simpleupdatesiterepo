package hudson.plugins.simpleupdatesite;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class Main {
	public static void main(String[] args) throws IOException {
		UpdateSite site = new UpdateSite();
		site.init(new File("./update"), "http://hudson02.nhncorp.com:9080/update");
		File file = new File("./update/simpleupdatesite.json");
		FileUtils.writeStringToFile(file, site.toUpdateCenterJSONString());
		System.out.println("simpleupdatesite.json is updated.");
	}
}
