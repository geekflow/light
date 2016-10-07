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

import com.geeksaga.light.agent.Module;
import com.geeksaga.light.agent.RepositoryContext;
import com.geeksaga.light.agent.TraceContext;
import com.geeksaga.light.agent.core.*;
import com.geeksaga.light.config.ConfigBinder;
import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;
import com.geeksaga.light.logger.LightLoggerBinder;
import com.geeksaga.light.profiler.config.ProfilerConfiguration;
import com.geeksaga.light.profiler.instrument.transformer.ClassFileTransformerDispatcher;
import com.geeksaga.light.profiler.instrument.transformer.EntryPointTransformer;
import com.geeksaga.light.profiler.instrument.transformer.LightClassFileTransformer;
import com.geeksaga.light.profiler.logger.Slf4jLoggerBinder;
import com.geeksaga.light.repository.RepositoryModule;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author geeksaga
 */
public class ProfilerModule implements Module
{
    private Instrumentation instrumentation;
    private TraceRegisterBinder traceRegisterBinder;
    private TraceContext traceContext;
    private RepositoryContext repositoryContext;
    private LightLogger logger;
    private LightLoggerBinder loggerBinder;
    private ConfigBinder configBinder;

    // TEST
    private BlockingQueue<ActiveObject> queue = new ArrayBlockingQueue<ActiveObject>(1000);

    private List<LightClassFileTransformer> classFileTransformerList;

    public ProfilerModule(Instrumentation instrumentation)
    {
        this.loggerBinder = new Slf4jLoggerBinder();
        loggerBinder();

        this.logger = CommonLogger.getLogger(getClass().getName());

        this.instrumentation = instrumentation;
        this.traceRegisterBinder = new DefaultTraceRegisterBinder();
        this.traceRegisterBinder.bind();

        //        this.configBinder = new DefaultConfigBinder(new ProfilerConfiguration());

        //        this.traceContext = new AgentTraceContext(configBinder.getConfig());

        this.traceContext = new AgentTraceContext(ProfilerConfiguration.load());
        this.repositoryContext = new AgentRepositoryContext(traceContext.getConfig(), queue);

        this.classFileTransformerList = Collections.synchronizedList(new ArrayList<LightClassFileTransformer>());
    }

    @Override
    public void start()
    {
        logger.info("profiler module start.");

        registPointCut();

        Module module = new RepositoryModule(repositoryContext, queue);
        module.start();

        addTransformer(instrumentation.isRetransformClassesSupported());
    }

    private void loggerBinder()
    {
        CommonLogger.initialize(loggerBinder);
    }

    private void registPointCut()
    {
        // FIXME need to order
        //        classFileTransformerList.add(new MethodParameterTransformer(traceRegisterBinder, traceContext));
        //        classFileTransformerList.add(new MethodReturnTransformer(traceRegisterBinder, traceContext));
        //        classFileTransformerList.add(new MethodTransformer(traceRegisterBinder, traceContext));
        //        classFileTransformerList.add(new PluginsTransformer(traceRegisterBinder, traceContext));
        //        classFileTransformerList.add(new EntryPointTransformer(traceRegisterBinder, traceContext)); // must be last put for EntryPointTransformer
        classFileTransformerList.add(new EntryPointTransformer(traceRegisterBinder, traceContext, repositoryContext)); // must be last put for EntryPointTransformer
    }

    private void addTransformer(boolean canRetransform)
    {
        instrumentation.addTransformer(new ClassFileTransformerDispatcher(traceRegisterBinder, traceContext, classFileTransformerList), canRetransform);
    }

    @Override
    public void stop()
    {
        logger.info("profiler module stop.");
    }
}
