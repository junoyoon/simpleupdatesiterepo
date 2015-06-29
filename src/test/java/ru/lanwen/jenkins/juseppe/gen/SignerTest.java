package ru.lanwen.jenkins.juseppe.gen;

import org.junit.Test;
import ru.lanwen.jenkins.juseppe.beans.Signature;
import ru.lanwen.jenkins.juseppe.beans.UpdateSite;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author lanwen (Merkushev Kirill)
 *         Date: 22.06.15
 */
public class SignerTest {
    @Test
    public void shouldIgnoreSignIfNoCertIsConfigured() throws Exception {
        Signature sign = new Signer().sign(new UpdateSite());
        
        assertThat(sign.getSignature(), nullValue());
    }
}
