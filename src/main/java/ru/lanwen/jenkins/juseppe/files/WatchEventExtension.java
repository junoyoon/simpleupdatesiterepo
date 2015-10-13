package ru.lanwen.jenkins.juseppe.files;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.function.Predicate;

/**
 * User: lanwen
 * Date: 27.01.15
 * Time: 12:50
 */
public class WatchEventExtension implements Predicate<WatchEvent<?>> {
    private String ext;

    private WatchEventExtension(String ext) {
        this.ext = ext;
    }

    public static WatchEventExtension hasExt(String ext) {
        return new WatchEventExtension(ext);
    }

    @Override
    public boolean test(WatchEvent<?> input) {
        if(!Path.class.isAssignableFrom(input.kind().type())) {
            return false;
        }
        return input.context().toString().endsWith(ext);
    }
}
