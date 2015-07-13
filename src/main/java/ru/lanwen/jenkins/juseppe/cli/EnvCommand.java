package ru.lanwen.jenkins.juseppe.cli;

import io.airlift.airline.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lanwen.jenkins.juseppe.props.JuseppeEnvVars.JuseppeEnvEnum;
import ru.lanwen.jenkins.juseppe.props.Props;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.of;

/**
 * @author lanwen (Merkushev Kirill)
 */
@Command(name = "env", description = "list all available env vars to setup")
public class EnvCommand implements Runnable {

    public String plugins = Props.props().getPlugins();

    @Override
    public void run() {
        System.out.println(of(JuseppeEnvEnum.values())
                        .map(env -> format("\t%s (%s) %n\t\t- %s%n\t\tresolved: %s%n", 
                                env.name(), env.mapping(), env.description(), env.resolved()))
                        .collect(joining("\n"))
        );
    }

}