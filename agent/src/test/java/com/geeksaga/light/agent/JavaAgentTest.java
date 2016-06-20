/*
 * Copyright 2015 GeekSaga.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.geeksaga.light.agent;

import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.lang.instrument.Instrumentation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * @author geeksaga
 */
public class JavaAgentTest
{
    private static String LIGHT_AGENT_JAR_PATH = System.getProperty("user.dir");

    @BeforeClass
    public static void init()
    {
        System.setProperty("LIGHT_HOME", LIGHT_AGENT_JAR_PATH + File.separator + ".." + File.separator + "install");
        System.setProperty("light.config", LIGHT_AGENT_JAR_PATH + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "light.conf");
        System.setProperty(XmlConfigurationFactory.CONFIGURATION_FILE_PROPERTY, LIGHT_AGENT_JAR_PATH + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "log4j2.xml");

        //        System.setProperty("java.class.path", System.getProperty("java.class.path") + File.pathSeparator + getAgentClassPath());
    }

    @Test
    public void testDuplicateInitializeCalls()
    {
        assertThat(JavaAgent.STATUS.get(), is(false));

        Instrumentation instrumentation = mock(Instrumentation.class);

        JavaAgent.premain("agent.class.path=", instrumentation);

        for (int i = 0; i < 3; i++)
        {
            assertThat(JavaAgent.STATUS.get(), is(true));

            if (!JavaAgent.STATUS.get())
            {
                JavaAgent.premain("agent.class.path=", instrumentation);

                fail();
            }
        }
    }
}
