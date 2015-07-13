package ru.lanwen.jenkins.juseppe.cli;

import com.google.common.base.Charsets;
import io.airlift.airline.Command;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lanwen.jenkins.juseppe.props.Props;

import java.io.File;
import java.io.IOException;

/**
 * @author lanwen (Merkushev Kirill)
 */
@Command(name = "cert",
        description = "prints content of certificate to paste it into update-site-manager plugin if needed")
public class PrintCertCommand implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(PrintCertCommand.class);

    @Override
    public void run() {
        try {
            System.out.println(FileUtils.readFileToString(new File(Props.props().getCert()), Charsets.UTF_8));
        } catch (IOException e) {
            LOG.error("Can't read certificate {}", Props.props().getCert(), e);
        }
    }
}
