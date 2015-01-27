package hudson.plugins.simpleupdatesite;

import hudson.plugins.simpleupdatesite.files.WatchStarter;
import hudson.plugins.simpleupdatesite.gen.GenStarter;
import hudson.plugins.simpleupdatesite.props.Props;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;

import static hudson.plugins.simpleupdatesite.files.WatchFiles.watchFor;
import static javax.servlet.DispatcherType.REQUEST;

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
                Resource.newResource(props.getPlugins()),
                Resource.newResource(props.getSaveto())
        ));
        context.addFilter(RequestLoggingFilter.class, "/*", EnumSet.of(REQUEST));

        context.addServlet(new ServletHolder("plugins", new DefaultServlet()), "*.hpi");
        context.addServlet(new ServletHolder("update-site", new DefaultServlet()), "/" + props.getName());

        server.setHandler(context);

        server.start();
        server.join();
    }
}
