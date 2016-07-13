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
import com.geeksaga.light.util.SimpleProperties;
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

    private SimpleProperties properties;

    public ProfilerConfiguration()
    {
        this(new SimpleProperties(SystemProperty.LIGHT_CONFIG));

        load();
    }

    public ProfilerConfiguration(SimpleProperties properties)
    {
        this.properties = properties;
    }

    public static Config load()
    {
        return load(SystemProperty.LIGHT_CONFIG);
    }

    public static Config load(String fileName)
    {
        return new ProfilerConfiguration(new SimpleProperties(fileName));
    }

    public static Config load(File file, String name)
    {
        return new ProfilerConfiguration(new SimpleProperties(file, name));
    }

    public String read(String propertyKey, String defaultValue)
    {
        return properties.getValueOrNull(propertyKey, defaultValue);
    }

    public int read(String propertyKey, int defaultValue)
    {
        String value = properties.getValueOrNull(propertyKey, String.valueOf(defaultValue));

        return Integer.valueOf(value);
    }

    public long read(String propertyKey, long defaultValue)
    {
        String value = properties.getValueOrNull(propertyKey, String.valueOf(defaultValue));

        return Long.valueOf(value);
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

    public boolean read(String propertyKey, boolean defaultValue)
    {
        String value = properties.getValueOrNull(propertyKey, String.valueOf(defaultValue));

        return Boolean.valueOf(value);
    }
}
