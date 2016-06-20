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
import com.geeksaga.light.agent.TraceContext;
import com.geeksaga.light.agent.core.AgentTraceContext;
import com.geeksaga.light.agent.core.DefaultTraceRegisterBinder;
import com.geeksaga.light.agent.core.TraceRegisterBinder;
import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;
import com.geeksaga.light.profiler.instrument.transformer.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author geeksaga
 */
public class ProfilerModule implements Module
{
    private Instrumentation instrumentation;
    private TraceRegisterBinder traceRegisterBinder;
    private TraceContext traceContext;
    private LightLogger logger;

    private Map<String, ClassFileTransformer> pointCuts;

    public ProfilerModule(Instrumentation instrumentation)
    {
        this.logger = CommonLogger.getLogger(this.getClass().getName());

        this.instrumentation = instrumentation;
        this.traceRegisterBinder = new DefaultTraceRegisterBinder();
        this.traceRegisterBinder.bind();
        this.traceContext = new AgentTraceContext(ProfilerConfig.load());

        this.pointCuts = new Hashtable<String, ClassFileTransformer>();
    }

    @Override
    public void start()
    {
        logger.info("profiler module start");

        registPointCut();

        addTransformer(instrumentation.isRetransformClassesSupported());
    }

    private void registPointCut()
    {
        pointCuts.put("", new MethodParameterTransformer());
        pointCuts.put("", new MethodReturnTransformer());
        pointCuts.put("", new MethodTransformer());
        pointCuts.put("", new LightClassFileTransformer(traceRegisterBinder, traceContext));
        pointCuts.put("", new PluginsTransformer(traceRegisterBinder, traceContext));
        pointCuts.put("", new EntryPointTransformer(traceRegisterBinder, traceContext)); // must be last put for EntryPointTransformer
    }

    private void addTransformer(boolean canRetransform)
    {
        instrumentation.addTransformer(new ClassFileTransformerDispatcher(traceRegisterBinder, traceContext, pointCuts), canRetransform);
    }

    @Override
    public void stop()
    {
        logger.info("profiler module end");
    }
}
