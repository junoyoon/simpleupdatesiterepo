package hudson.plugins.simpleupdatesite;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class KBCreationTest {
	@Test
	public void testKBCreation() throws IOException {
		File file = new File("./kbs_prepare");
		File destFile = new File("./kbs");
		File kbsPrepare = file.getCanonicalFile();
		File[] listFiles = kbsPrepare.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}

		});
		for (File eachFile : listFiles) {
			if (eachFile.isHidden() || eachFile.isFile()) {
				continue;
			}
			zipFolder(eachFile, new File(destFile, eachFile.getName() + ".zip"), eachFile);
		}
	}

	public void zipFolder(File fromFolder, File toZip, File basePath) throws IOException {
		ZipOutputStream zout = null;
		try {
			// create object of FileOutputStream
			FileOutputStream fout = new FileOutputStream(toZip);
			// create object of ZipOutputStream from FileOutputStream
			zout = new ZipOutputStream(fout);
			// create File object from source directory
			addDirectory(zout, fromFolder, basePath);
			// close the ZipOutputStream
			zout.close();
		} finally {
			IOUtils.closeQuietly(zout);
		}
	}

	private void addDirectory(ZipOutputStream zout, File fileSource, File basePath) throws IOException {
		FileInputStream fin = null;
		try {
			for (File file : fileSource.listFiles()) {
				if (file.isHidden()) {
					continue;
				}
				// if the file is directory, call the function recursively
				if (file.isDirectory()) {
					addDirectory(zout, file, basePath);
					continue;
				}
				fin = new FileInputStream(file);
				ZipEntry zipEntry = new ZipEntry(basePath.toURI().relativize(file.toURI()).getPath());
				zipEntry.setTime(file.lastModified());
				zout.putNextEntry(zipEntry);
				IOUtils.copy(fin, zout);
				zout.closeEntry();
				fin.close();
			}
		} finally {
			IOUtils.closeQuietly(fin);
		}
	}
}
