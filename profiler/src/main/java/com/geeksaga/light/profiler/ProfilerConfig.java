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
import com.geeksaga.light.agent.config.Configure;
import com.geeksaga.light.util.SystemProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * @author geeksaga
 */
public class ProfilerConfig implements Config {
    private static Logger logger = LoggerFactory.getLogger(ProfilerConfig.class.getName());

    private Properties properties;

    public ProfilerConfig() {
        this(new Properties());
    }

    public ProfilerConfig(Properties properties) {
        this.properties = properties;
    }

    public static Config load() {
        return load(SystemProperty.LIGHT_CONFIG);
    }

    public static Config load(String file) {
        try {
            Configure configure = new Configure();
            return new ProfilerConfig(configure.load(file));
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }

        return new ProfilerConfig();
    }

    public static Config load(ClassLoader classLoader, String file) {
        try {
            Configure configure = new Configure();
            return new ProfilerConfig(configure.load(classLoader, file));
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }

        return new ProfilerConfig();
    }

    public String read(String propertyKey, String defaultValue) {
        return properties.getProperty(propertyKey, defaultValue);
    }

    public int read(String propertyKey, int defaultValue) {
        String value = properties.getProperty(propertyKey, String.valueOf(defaultValue));

        return Integer.valueOf(value);
    }

    public long read(String propertyKey, long defaultValue) {
        String value = properties.getProperty(propertyKey, String.valueOf(defaultValue));

        return Long.valueOf(value);
    }

    public List<String> read(String propertyKey) {
        String value = properties.getProperty(propertyKey);
        if (value == null) {
            return Collections.emptyList();
        }

        return Arrays.asList(value.trim().split("\\s+"));
    }

    public boolean read(String propertyKey, boolean defaultValue) {
        String value = properties.getProperty(propertyKey, String.valueOf(defaultValue));

        return Boolean.valueOf(value);
    }
}
