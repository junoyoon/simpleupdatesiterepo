package ru.lanwen.jenkins.juseppe;

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
 * User: lanwen
 * Date: 25.01.15
 * Time: 2:46
 */
public class ServerMain {
    public static Props props = Props.props();

    public static void main(String[] args) throws Exception {
        Server server = new Server(props.getPort());

        Path path = Paths.get(props.getPlugins());

        server.addLifeCycleListener(new GenStarter(path));
        server.addLifeCycleListener(new WatchStarter(watchFor(path)));

        ServletContextHandler context = new ServletContextHandler();

        context.setBaseResource(new ResourceCollection(
                Resource.newResource(props.getSaveto()),
                Resource.newResource(props.getPlugins())
        ));

        context.addServlet(new ServletHolder("update-site", new DefaultServlet()), "/" + props.getName());
        context.addServlet(new ServletHolder("plugins", new DefaultServlet()), "*.hpi");

        Slf4jRequestLog requestLog = new Slf4jRequestLog();
        requestLog.setLogDateFormat(null);

        RequestLogHandler log = new RequestLogHandler();
        log.setHandler(context);
        log.setRequestLog(requestLog);

        server.setHandler(log);

        server.start();
        server.join();
    }
}
