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
package com.geeksaga.light.tools.vm;

import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;

/**
 * @author geeksaga
 */
public class VirtualMachineAttacheTest
{
    private static final LightLogger logger = CommonLogger.getLogger(VirtualMachineAttacheTest.class.getName());

    private VirtualMachineAttache attache = new VirtualMachineAttache();

    @BeforeClass
    public static void init()
    {
        System.setProperty("LIGHT_HOME", System.getProperty("user.dir") + File.separator + ".." + File.separator + "install");
        System.setProperty("light.config", System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "light.conf");
        System.setProperty(XmlConfigurationFactory.CONFIGURATION_FILE_PROPERTY, System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "log4j2.xml");
    }

    @Test
    public void testList()
    {
        attache.show();
    }

    @Test
    public void testAttach() throws InterruptedException
    {
        logger.info("testAttach {}", "agent.class.path=");

        attache.attach("agent.class.path=" + attache.getAgentClassPath());
    }

    @Test
    public void testFindLastAgentJarOrNull()
    {
        String CLASS_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "AGENT_HOME";

        assertThat(attache.findLastAgentJarOrNull(), isEmptyOrNullString());
        assertThat(attache.findLastAgentJarOrNull(CLASS_PATH), is("0.0.2"));
    }
}
