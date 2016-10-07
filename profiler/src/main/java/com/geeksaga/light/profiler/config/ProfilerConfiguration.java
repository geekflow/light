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
import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;
import com.geeksaga.light.config.MultiLineConfigure;
import com.geeksaga.light.util.SystemProperty;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author geeksaga
 */
public class ProfilerConfiguration implements Config
{
    private static final LightLogger logger = CommonLogger.getLogger(ProfilerConfiguration.class.getName());

    private MultiLineConfigure properties;

    private ProfilerConfiguration(MultiLineConfigure properties)
    {
        this.properties = properties;
    }

    public static Config load()
    {
        return new ProfilerConfiguration(new MultiLineConfigure());
    }

    public static Config load(String name)
    {
        return new ProfilerConfiguration(new MultiLineConfigure(name));
    }

    public static Config load(File file, String name)
    {
        return new ProfilerConfiguration(new MultiLineConfigure(file, name));
    }

    public static Config load(ClassLoader classLoader, String name)
    {
        return new ProfilerConfiguration(new MultiLineConfigure(classLoader, name));
    }

    public String read(String propertyKey, String defaultValue)
    {
        return properties.getValueOrNull(propertyKey, defaultValue);
    }

    public boolean read(String propertyKey, boolean defaultValue)
    {
        return Boolean.valueOf(read(propertyKey, String.valueOf(defaultValue)));
    }

    public short read(String propertyKey, short defaultValue)
    {
        return Short.valueOf(read(propertyKey, String.valueOf(defaultValue)));
    }

    public int read(String propertyKey, int defaultValue)
    {
        return Integer.valueOf(read(propertyKey, String.valueOf(defaultValue)));
    }

    public long read(String propertyKey, long defaultValue)
    {
        return Long.valueOf(read(propertyKey, String.valueOf(defaultValue)));
    }

    public List<String> read(String propertyKey)
    {
        String[] values = properties.getValues(propertyKey);
        if (values == null || values.length == 0)
        {
            return Collections.emptyList();
        }

        return Arrays.asList(values);
    }
}
