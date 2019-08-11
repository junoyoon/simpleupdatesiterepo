package ru.lanwen.jenkins.juseppe.cli;

import io.airlift.airline.Option;
import io.airlift.airline.OptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lanwen.jenkins.juseppe.props.Props;

/**
 * @author lanwen (Merkushev Kirill)
 */
public abstract class JuseppeCommand implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(JuseppeCommand.class);
    private static Props props = Props.populated();
    private int exitCode = 0;


    @Option(name = {"-w", "--watch"},
            description = "watch plugins in directory and update json on change",
            arity = 0,
            type = OptionType.GLOBAL
    )
    private boolean watch;

    @Option(name = {"-p", "--plugins-directory"},
            title = "plugins directory",
            description = "where the plugins are. Searches only <*.hpi>. Defaults to <current working dir>",
            arity = 1,
            type = OptionType.GLOBAL)
    private String plugins = props.getPluginsDir();

    @Override
    public void run() {
        try {
            unsafeRun(props.withPluginsDir(plugins));
        } catch (Exception e) {
            LOG.error("Can't execute command", e);
            exitCode = 1;
        }
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public boolean isWatch() {
        return watch;
    }

    public void setWatch(boolean watch) {
        this.watch = watch;
    }

    public String getPlugins() {
        return plugins;
    }

    public void setPlugins(String plugins) {
        this.plugins = plugins;
    }

    public abstract void unsafeRun(Props props) throws Exception;
}
