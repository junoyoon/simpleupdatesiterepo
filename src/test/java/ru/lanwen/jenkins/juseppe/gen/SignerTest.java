package ru.lanwen.jenkins.juseppe.gen;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;
import ru.lanwen.jenkins.juseppe.beans.Signature;
import ru.lanwen.jenkins.juseppe.beans.UpdateSite;
import ru.lanwen.jenkins.juseppe.props.JuseppeEnvVars;
import ru.lanwen.jenkins.juseppe.props.Props;

import java.io.File;

import static com.google.common.io.Resources.getResource;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author lanwen (Merkushev Kirill)
 *         Date: 22.06.15
 */
public class SignerTest {
    @Rule
    public ExternalResource setenv = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            File key = new File(getResource("tmp/cert/uc.key").getFile());
            File cert = new File(getResource("tmp/cert/uc.crt").getFile());

            System.setProperty(JuseppeEnvVars.JUSEPPE_PRIVATE_KEY_PATH, key.getAbsolutePath());
            System.setProperty(JuseppeEnvVars.JUSEPPE_CERT_PATH, cert.getAbsolutePath());
            Props.props().reset();
        }
    };

    @Test
    public void shouldIgnoreSignIfNoCertIsConfigured() throws Exception {
        System.clearProperty(JuseppeEnvVars.JUSEPPE_PRIVATE_KEY_PATH);
        System.clearProperty(JuseppeEnvVars.JUSEPPE_CERT_PATH);
        Props.props().reset();
        
        Signature sign = new Signer().sign(new UpdateSite());

        assertThat(sign.getSignature(), nullValue());
    }

    @Test
    public void shouldSignWithSelfSignedCert() throws Exception {
        Signature sign = new Signer().sign(new UpdateSite());

        assertThat("sign", sign.getCorrectSignature(), notNullValue());
        assertThat("digest", sign.getCorrectDigest(), notNullValue());
        assertThat("certs", sign.getCertificates(), hasSize(greaterThan(0)));
    }
}
