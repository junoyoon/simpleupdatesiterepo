package hudson.plugins.simpleupdatesite.files;

import com.google.common.base.Predicate;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * User: lanwen
 * Date: 27.01.15
 * Time: 12:50
 */
public class WatchEventExtension implements Predicate<WatchEvent<Path>> {
    private String ext;

    private WatchEventExtension(String ext) {
        this.ext = ext;
    }

    public static WatchEventExtension hasExt(String ext) {
        return new WatchEventExtension(ext);
    }

    @Override
    public boolean apply(WatchEvent<Path> input) {
        return input.context().toString().endsWith(ext);
    }
}