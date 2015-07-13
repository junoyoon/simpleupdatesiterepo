package ru.lanwen.jenkins.juseppe.cli;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Slf4jRequestLog;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import ru.lanwen.jenkins.juseppe.files.WatchStarter;
import ru.lanwen.jenkins.juseppe.gen.GenStarter;
import ru.lanwen.jenkins.juseppe.props.Props;

import java.nio.file.Path;
import java.nio.file.Paths;

import static ru.lanwen.jenkins.juseppe.files.WatchFiles.watchFor;

/**
 * @author lanwen (Merkushev Kirill)
 */
@Command(name = "serve", description = "starts the jetty server with Juseppe to serve generated json and plugins")
public class ServeCommand extends JuseppeCommand {

    public static final String JENKINS_PLUGIN_WILDCART = "*.hpi";

    @Arguments(title = "port", description = "Port to bind jetty on")
    public int port = Props.props().getPort();

    @Override
    public void unsafeRun() throws Exception {
        Server server = new Server(port);
        Path path = Paths.get(plugins);

        server.addLifeCycleListener(new GenStarter(path));

        if (watch) {
            server.addLifeCycleListener(new WatchStarter(watchFor(path)));
        }

        ServletContextHandler context = new ServletContextHandler();

        context.setBaseResource(new ResourceCollection(
                Resource.newResource(Props.props().getSaveto()),
                Resource.newResource(Props.props().getPlugins())
        ));

        context.addServlet(new ServletHolder("update-site", new DefaultServlet()), "/" + Props.props().getUcJsonName());
        context.addServlet(new ServletHolder("release-history",
                new DefaultServlet()), "/" + Props.props().getReleaseHistoryJsonName());
        context.addServlet(new ServletHolder("plugins", new DefaultServlet()), JENKINS_PLUGIN_WILDCART);

        Slf4jRequestLog requestLog = new Slf4jRequestLog();
        requestLog.setLogDateFormat(null);

        RequestLogHandler log = new RequestLogHandler();
        log.setHandler(context);
        log.setRequestLog(requestLog);

        server.setHandler(log);

        server.setStopAtShutdown(true);
        server.start();
        server.join();
    }
}