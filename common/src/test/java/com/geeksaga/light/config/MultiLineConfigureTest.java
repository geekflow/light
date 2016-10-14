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
package com.geeksaga.light.config;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static com.geeksaga.light.util.SystemProperty.EMBEDDED_LIGHT_CONFIG;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author geeksaga
 */
public class MultiLineConfigureTest
{
    @BeforeClass
    public static void init()
    {
        System.setProperty("light.config", System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "light.conf");
    }

    @Test
    public void testLoad()
    {
        MultiLineConfigure properties = new MultiLineConfigure(EMBEDDED_LIGHT_CONFIG);

        assertThat(properties.getValueOrNull("empty"), nullValue());

        assertThat(properties.getValueOrNull("ignore_bci_pattern"), is("com.geeksaga.light."));

        assertThat(Arrays.asList(properties.getValues("ignore_bci_pattern")), containsInAnyOrder("com.geeksaga.light.", "java.lang.", "java.lang.String.toString()V"));

        assertThat(Arrays.asList(properties.getValues("ignore_bci_pattern")).size(), is(3));
    }

    @Test
    public void testAddAll()
    {
        MultiLineConfigure properties = new MultiLineConfigure(EMBEDDED_LIGHT_CONFIG);

        assertThat(properties.getValues("ignore_bci_pattern").length, is(3));

        properties.addAll(new MultiLineConfigure(EMBEDDED_LIGHT_CONFIG));

        assertThat(properties.getValues("ignore_bci_pattern").length, is(6));

        for (int i = 1; i < 5; i++)
        {
            properties.addAll(new MultiLineConfigure(EMBEDDED_LIGHT_CONFIG));

            assertThat(properties.getValues("ignore_bci_pattern").length, is(6 + (i * 3)));
        }
    }
}