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
import com.geeksaga.light.config.ConfigBinder;

/**
 * @author geeksaga
 */
public class DefaultConfigBinder implements ConfigBinder
{
    private Config config;

    public DefaultConfigBinder()
    {
        this(new ProfilerConfig());
    }

    public DefaultConfigBinder(Config config)
    {
        this.config = config;
    }

    @Override
    public Config getConfig()
    {
        return config;
    }
}
