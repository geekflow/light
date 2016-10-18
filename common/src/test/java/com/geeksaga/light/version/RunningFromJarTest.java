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
package com.geeksaga.light.version;

import org.junit.Test;

import java.net.URL;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

/**
 * @author geeksaga
 */
public class RunningFromJarTest
{
    @Test
    public void testGetJarFilePathOrNull()
    {
        URL url = RunningFromJarTest.class.getResource("/" + RunningFromJarTest.class.getName().replace('.', '/').trim() + ".class");
        String urlString = (url != null) ? url.toString() : "";

        assertThat(urlString, startsWith("file:/"));
        assertThat(urlString, containsString(RunningFromJarTest.class.getSimpleName()));

        assertThat(RunningFromJar.getJarFilePathOrNull(RunningFromJarTest.class), nullValue());
    }

    @Test
    public void testGetVersionNumberOrNull()
    {
        assumeThat("", is(""));
        //        RunningFromJar runningFromJar = mock(RunningFromJar.class);
        //        when(RunningFromJar.getJarFilePathOrNull(any(Class.class))).thenReturn("/light.profiler-0.0.1.jar");

        assertThat(RunningFromJar.getVersionNumberOrNull(), nullValue());
    }
}
