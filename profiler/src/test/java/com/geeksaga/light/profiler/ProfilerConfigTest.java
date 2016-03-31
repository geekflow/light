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
package com.geeksaga.light.profiler;

import com.geeksaga.light.agent.config.Config;
import com.geeksaga.light.agent.config.ConfigDef;
import org.junit.Test;
import target.TestMethods;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

/**
 * @author geeksaga
 */
public class ProfilerConfigTest {
    @Test
    public void testLoad() {
        ProfilerConfig profilerConfig = new ProfilerConfig();

        assertThat(profilerConfig.read(ConfigDef.entry_point, "default"), is("default"));

        Config config = ProfilerConfig.load(ProfilerConfigTest.class.getClassLoader(), "light.conf");

        assertThat(config, instanceOf(ProfilerConfig.class));

        assertThat(config.read(ConfigDef.entry_point, "default"), containsString(TestMethods.class.getName()));
    }

    @Test
    public void testReadValue() throws IOException {
        Config config = ProfilerConfig.load(ProfilerConfigTest.class.getClassLoader(), "light.conf");

        assertThat(config.read(ConfigDef.entry_point, "default"), containsString(TestMethods.class.getName()));
        assertThat(config.read(ConfigDef.class_max_size, 1024 * 1024), is(1048576));
        assertThat(config.read(ConfigDef.method_min_size, -1), is(0));
        assertThat(config.read(ConfigDef.method_max_size, -1), is(48000));
    }
}
