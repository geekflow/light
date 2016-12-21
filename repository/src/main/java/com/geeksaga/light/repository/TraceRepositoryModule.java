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
import com.geeksaga.light.agent.TraceRepository;
import com.geeksaga.light.agent.core.ActiveObject;
import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;
import com.geeksaga.light.repository.connect.RepositoryExecutor;
import com.geeksaga.light.repository.connect.RepositorySource;
import com.geeksaga.light.util.IdentifierUtils;

import java.util.concurrent.BlockingQueue;

import static com.geeksaga.light.agent.config.ConfigDef.instance_id;
import static com.geeksaga.light.agent.config.ConfigDefaultValueDef.default_instance_id;
import static com.geeksaga.light.repository.util.ModuleExecutors.REPOSITORY_WORKER;
import static com.geeksaga.light.repository.util.ModuleExecutors.shutdownNowAll;

/**
 * @author geeksaga
 */
public class TraceRepositoryModule implements Module
{
    private final LightLogger logger;
    private final TraceRepository traceRepository;
    private final RepositoryExecutor repositoryExecutor;
    private final BlockingQueue<ActiveObject> queue;

    public TraceRepositoryModule(TraceRepository traceRepository, RepositoryExecutor repositoryExecutor, BlockingQueue<ActiveObject> queue)
    {
        this.logger = CommonLogger.getLogger(getClass().getName());
        this.traceRepository = traceRepository;
        this.repositoryExecutor = repositoryExecutor;
        this.queue = queue;
    }

    @Override
    public void start()
    {
        logger.info("repository module start");

        IdentifierUtils.seed(System.nanoTime() ^ traceRepository.getConfig().read(instance_id, default_instance_id));

        REPOSITORY_WORKER.execute(new TraceRepositoryWorker(traceRepository, repositoryExecutor, queue));
    }

    @Override
    public void stop()
    {
        shutdownNowAll();

        logger.info("repository module stop");
    }
}