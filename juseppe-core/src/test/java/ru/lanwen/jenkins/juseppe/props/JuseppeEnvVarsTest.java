package ru.lanwen.jenkins.juseppe.props;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.lanwen.jenkins.juseppe.props.JuseppeEnvVars.JuseppeEnvEnum;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author lanwen (Merkushev Kirill)
 */
@RunWith(Parameterized.class)
public class JuseppeEnvVarsTest {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<JuseppeEnvEnum> data() {
        return newArrayList(JuseppeEnvEnum.values());
    }

    @Parameterized.Parameter
    public JuseppeEnvEnum env;

    @Test
    public void shouldResolveAllVars() throws Exception {
        assertThat(env.resolved(), notNullValue());
    }
}
