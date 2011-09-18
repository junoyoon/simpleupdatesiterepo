package hudson.plugins.simpleupdatesite;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class MainTest {
	@Test
	public void testMandatoryArgs() {
		Main main = new Main();
		try {
			main.parseArgument(new String[] { "-h", "http://hellworld.com", "" });
			Assert.fail();
		} catch (Exception e) {
			assertThat(e.getMessage(), containsString("Missing required option"));
		}
	}

	@Test
	public void testMandatoryMissingFolder() throws ParseException, IOException {
		Main main = new Main();
		try {
			main.parseArgument(new String[] { "-u", "http://hellworld.com", });
			Assert.fail();
		} catch (Exception e) {
			assertThat(e.getMessage(), containsString("does not exist"));
		}

		try {
			main.parseArgument(new String[] { "-u", "http://hellworld.com", "-f", "ewew" });
			Assert.fail();
		} catch (Exception e) {
			assertThat(e.getMessage(), containsString("does not exist"));
		}

		main.parseArgument(new String[] { "-u", "http://hellworld.com", "-f", "./src/test/resources/tmp" });
	}

	@Test
	public void testDoMainWithCustomName() {
		File updateCenterFile = new File("./src/test/resources/tmp", "my.json");
		if (updateCenterFile.exists()) {
			updateCenterFile.delete();
		}
		Main main = new Main();
		main.doMain(new String[] { "-u", "http://hellworld.com", "-f", "./src/test/resources/tmp", "-n", "my.json" });
		assertThat(updateCenterFile.exists(), is(true));
	}

	@Test
	public void testDoMainWithDefaultName() {
		File updateCenterFile = new File("./src/test/resources/tmp", "update-center.json");
		if (updateCenterFile.exists()) {
			updateCenterFile.delete();
		}
		Main main = new Main();
		main.doMain(new String[] { "-u", "http://hellworld.com", "-f", "./src/test/resources/tmp" });
		assertThat(updateCenterFile.exists(), is(true));
	}
}
