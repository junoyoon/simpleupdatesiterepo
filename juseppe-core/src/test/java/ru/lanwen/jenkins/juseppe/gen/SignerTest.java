package ru.lanwen.jenkins.juseppe.gen;

import org.junit.Before;
import org.junit.Test;
import ru.lanwen.jenkins.juseppe.beans.Signature;
import ru.lanwen.jenkins.juseppe.beans.UpdateSite;
import ru.lanwen.jenkins.juseppe.props.JuseppeEnvVars;

import java.io.File;

import static com.google.common.io.Resources.getResource;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static ru.lanwen.jenkins.juseppe.props.Props.populated;

/**
 * @author lanwen (Merkushev Kirill)
 *         Date: 22.06.15
 */
public class SignerTest {

    @Before
    public void setUp() throws Exception {
        System.clearProperty(JuseppeEnvVars.JuseppeEnvEnum.JUSEPPE_PRIVATE_KEY_PATH.mapping());
        System.clearProperty(JuseppeEnvVars.JuseppeEnvEnum.JUSEPPE_CERT_PATH.mapping());
    }

    @Test
    public void shouldIgnoreSignIfNoCertIsConfigured() throws Exception {
        Signature sign = new Signer(
                populated().getKeyPath(),
                singletonList(populated().getCertPath()),
                singletonList(populated().getCertPath())
        ).sign(new UpdateSite());

        assertThat(sign.getSignature(), nullValue());
    }

    @Test
    public void shouldSignWithSelfSignedCert() throws Exception {
        File key = new File(getResource("tmp/cert/uc.key").getFile());
        File cert = new File(getResource("tmp/cert/uc.crt").getFile());

        System.setProperty(JuseppeEnvVars.JuseppeEnvEnum.JUSEPPE_PRIVATE_KEY_PATH.mapping(), key.getAbsolutePath());
        System.setProperty(JuseppeEnvVars.JuseppeEnvEnum.JUSEPPE_CERT_PATH.mapping(), cert.getAbsolutePath());

        Signature sign = new Signer().sign(new UpdateSite());

        assertThat("sign", sign.getCorrectSignature(), notNullValue());
        assertThat("digest", sign.getCorrectDigest(), notNullValue());
        assertThat("certs", sign.getCertificates(), hasSize(greaterThan(0)));
    }
}
