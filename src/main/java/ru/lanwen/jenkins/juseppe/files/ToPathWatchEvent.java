package ru.lanwen.jenkins.juseppe.files;

import com.google.common.base.Function;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * User: lanwen
 * Date: 27.01.15
 * Time: 13:13
 */
public class ToPathWatchEvent implements Function<WatchEvent<?>, WatchEvent<Path>> {

    private ToPathWatchEvent() {
    }

    public static ToPathWatchEvent toPathWatchEvent() {
        return new ToPathWatchEvent();
    }

    @Override
    @SuppressWarnings("unchecked")
    public WatchEvent<Path> apply(WatchEvent<?> input) {
        return (WatchEvent<Path>) input;
    }
}
