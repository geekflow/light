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
package com.geeksaga.light.agent.core;

import com.geeksaga.light.agent.RepositoryContext;
import com.geeksaga.light.config.Config;
import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author geeksaga
 */
public class AgentRepositoryContext implements RepositoryContext
{
    private LightLogger logger;
    private Config config;
    private BlockingQueue<ActiveObject> queue;

    public AgentRepositoryContext(Config config)
    {
        this(config, new ArrayBlockingQueue<ActiveObject>(1000));
    }

    public AgentRepositoryContext(Config config, BlockingQueue<ActiveObject> queue)
    {
        this.logger = CommonLogger.getLogger(getClass().getName());
        this.config = config;
        this.queue = queue;
    }

    public void save(ActiveObject activeObject)
    {
        try
        {
            queue.put(activeObject);
        }
        catch (InterruptedException e)
        {
            logger.info(e);
        }
    }

    public Config getConfig()
    {
        return config;
    }
}
