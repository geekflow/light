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
package com.geeksaga.light.profiler.config;

import com.geeksaga.light.config.Config;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import target.TestMethods;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static com.geeksaga.light.agent.config.ConfigDef.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;

/**
 * @author geeksaga
 */
@RunWith(value = Parameterized.class)
public class ProfilerConfigurationTest
{
    private int expected;
    private int valueOne;
    private int valueTwo;

    @Parameterized.Parameters
    public static Collection<Integer[]> getTestParameters()
    {
        return Arrays.asList(new Integer[][] { { 2, 1, 1 }, { 3, 2, 1 }, { 4, 3, 1 } });
    }

    public ProfilerConfigurationTest(int expected, int valueOne, int valueTwo)
    {
        this.expected = expected;
        this.valueOne = valueOne;
        this.valueTwo = valueTwo;
    }

    @Test
    public void testValidationValue()
    {
        assertEquals(expected, valueOne + valueTwo, 0);
    }

    @Test
    public void testLoad()
    {
        Config config = ProfilerConfiguration.load();

        assertThat(config.read(entry_point, "default"), containsString(TestMethods.class.getName()));

        config = ProfilerConfiguration.load(ProfilerConfigurationTest.class.getClassLoader(), "light.conf");

        assertThat(config, instanceOf(ProfilerConfiguration.class));

        assertThat(config.read(entry_point, "default"), containsString(TestMethods.class.getName()));
    }

    @Test
    public void testReadValue() throws IOException
    {
        Config config = ProfilerConfiguration.load("light.conf");

        assertThat(config.read(entry_point, "default"), containsString(TestMethods.class.getName()));
        assertThat(config.read(class_max_size, 1024 * 1024), is(1048576));
        assertThat(config.read(method_min_size, -1), is(0));
        assertThat(config.read(method_max_size, -1), is(48000));

        System.out.println(config.read(entry_point, "default"));
    }

    @Test
    public void testReadListValue() throws IOException
    {
        Config config = ProfilerConfiguration.load("light.conf");

        assertThat(config.read(entry_point, "default"), containsString(TestMethods.class.getName()));

        assertThat(config.read(entry_point).size(), is(5));
    }
}
