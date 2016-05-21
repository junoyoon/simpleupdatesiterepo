package ru.lanwen.jenkins.juseppe.gen.source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lanwen.jenkins.juseppe.beans.Plugin;
import ru.lanwen.jenkins.juseppe.gen.HPI;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.lang.String.format;

/**
 * @author lanwen (Merkushev Kirill)
 */
public class PathPluginSource implements PluginSource {

    private static final Logger LOG = LoggerFactory.getLogger(PathPluginSource.class);
    private final Path pluginsDir;

    public PathPluginSource(Path pluginsDir) {
        this.pluginsDir = pluginsDir;
    }

    @Override
    public List<Plugin> plugins() {
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(pluginsDir, "*.{hpi,jpi}")) {
            return StreamSupport.stream(paths.spliterator(), false).map(path -> {
                try {
                    LOG.trace("Process file {}", path);

                    return HPI.loadHPI(path.toFile())
                            .withUrl(pluginsDir.relativize(path).toString());

                } catch (Exception e) {
                    LOG.error("Fail to get the {} info", path.toAbsolutePath(), e);
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(format("Can't read path %s", pluginsDir.toAbsolutePath()), e);
        }
    }
}
