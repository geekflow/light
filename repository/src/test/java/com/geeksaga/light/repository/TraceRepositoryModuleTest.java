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
import com.geeksaga.light.config.Config;
import com.geeksaga.light.repository.connect.RepositoryConnection;
import com.geeksaga.light.repository.util.ModuleExecutors;
import com.geeksaga.light.test.TestConfigure;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author geeksaga
 */
public class TraceRepositoryModuleTest
{
    @BeforeClass
    public static void init()
    {
        TestConfigure.load();
    }

    @Test
    public void testLifeCycle() throws InterruptedException
    {
        Config config = mock(Config.class);
        TraceRepository traceRepository = mock(TraceRepository.class);
        RepositoryConnection repositoryConnection = mock(RepositoryConnection.class);
        when(traceRepository.getConfig()).thenReturn(config);

        //        when(config.read(any(String.class), any(String.class))).thenReturn("memory:/%s/");

        Module module = new TraceRepositoryModule(traceRepository, repositoryConnection, new ArrayBlockingQueue<ActiveObject>(10));
        module.start();

        assertThat(false, is(ModuleExecutors.REPOSITORY_WORKER.isShutdown()));

        module.stop();

        assertThat(true, is(ModuleExecutors.REPOSITORY_WORKER.isShutdown()));
    }
}