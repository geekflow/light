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
package com.geeksaga.light.repository;

import com.geeksaga.light.agent.Module;
import com.geeksaga.light.agent.RepositoryContext;
import com.geeksaga.light.agent.config.ConfigValueDef;
import com.geeksaga.light.agent.core.ActiveObject;
import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;
import com.geeksaga.light.repository.util.IdentifierUtils;

import java.util.concurrent.BlockingQueue;

import static com.geeksaga.light.agent.config.ConfigDef.instance_id;
import static com.geeksaga.light.repository.util.ModuleExecutors.REPOSITORY_WORKER;
import static com.geeksaga.light.repository.util.ModuleExecutors.shutdownNowAll;

/**
 * @author geeksaga
 */
public class RepositoryModule implements Module
{
    private LightLogger logger;
    private RepositoryContext repositoryContext;
    private BlockingQueue<ActiveObject> queue;

    public RepositoryModule(RepositoryContext repositoryContext, BlockingQueue<ActiveObject> queue)
    {
        this.logger = CommonLogger.getLogger(getClass().getName());
        this.repositoryContext = repositoryContext;
        this.queue = queue;
    }

    @Override
    public void start()
    {
        logger.info("repository module start");

        IdentifierUtils.seed(System.nanoTime() ^ repositoryContext.getConfig().read(instance_id, ConfigValueDef.instance_id));

        REPOSITORY_WORKER.execute(new RepositoryWorker(queue));
    }

    @Override
    public void stop()
    {
        shutdownNowAll();

        logger.info("repository module stop");
    }
}
