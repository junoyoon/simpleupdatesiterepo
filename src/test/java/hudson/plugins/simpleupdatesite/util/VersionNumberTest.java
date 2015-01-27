package hudson.plugins.simpleupdatesite.util;

import hudson.plugins.simpleupdatesite.util.VersionNumber;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;

public class VersionNumberTest {
	@Test
	public void testVersionNumber() {
		VersionNumber versionNumber = new VersionNumber("1.0.0");
		assertThat(versionNumber.isNewerThan(new VersionNumber("0.9.9")), is(true));
		assertThat(versionNumber.isNewerThan(new VersionNumber("1.0.9")), is(false));
		assertThat(versionNumber.isOlderThan(new VersionNumber("1.0.1")), is(true));
		assertThat(versionNumber.isOlderThan(new VersionNumber("0.9.9")), is(false));
		assertThat(versionNumber.compareTo(new VersionNumber("0.9.9")), is(1));
		assertThat(versionNumber.compareTo(new VersionNumber("0.9.9-ea")), is(1));
	}
}
