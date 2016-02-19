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
 *
 */
package com.geeksaga.light.agent;

import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author geeksaga
 */
public class AgentClassPathResolverTest {
    private String LIGHT_AGENT_JAR = "light.agent-0.0.1-SNAPSHOT.jar";
    private String LIGHT_AGENT_JAR_PATH = System.getProperty("user.dir");
    private String CLASS_PATH = System.getProperty("java.class.path") + File.pathSeparator + LIGHT_AGENT_JAR_PATH + File.separator + LIGHT_AGENT_JAR;

    @Test
    public void testFindAgentJar() {
        AgentClassPathResolver resolver = new AgentClassPathResolver(CLASS_PATH);
        assertThat(true, is(resolver.findAgentJar()));

        assertThat(resolver.getAgentJarName(), is(LIGHT_AGENT_JAR));
        assertThat(resolver.getAgentJarPath(), is(LIGHT_AGENT_JAR_PATH));
        assertThat(resolver.getAgentCoreJarName(), nullValue());
    }
}
