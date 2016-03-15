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
import com.geeksaga.light.agent.config.Configure;
import com.geeksaga.light.agent.core.*;
import com.geeksaga.light.profiler.instrument.transformer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;

/**
 * @author geeksaga
 */
public class ProfilerModule implements Module {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private Instrumentation instrumentation;
    private TraceRegisterBinder traceRegisterBinder;
    private TraceContext traceContext;

    public ProfilerModule(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
        this.traceRegisterBinder = new DefaultTraceRegisterBinder();
        this.traceRegisterBinder.bind();
        this.traceContext = new AgentTraceContext();
    }

    @Override
    public void start() {
        logger.info("profiler module start");

        Configure configure = new Configure();
        // configure.load("light.conf");

        addTransformer(instrumentation.isRetransformClassesSupported());
    }

    private void addTransformer(boolean canRetransform) {
        // TODO transformer dispatcher
        // instrumentation.addTransformer(new ClassFileTransformerDispatcher(), canRetransform);
        instrumentation.addTransformer(new EntryPointTransformer(traceRegisterBinder, traceContext), canRetransform);
//        instrumentation.addTransformer(new MethodParameterTransformer(), canRetransform);
//        instrumentation.addTransformer(new MethodReturnTransformer(), canRetransform);
//        instrumentation.addTransformer(new LightClassFileTransformer(traceRegisterBinder, traceContext), canRetransform);
    }

    @Override
    public void stop() {
        logger.info("profiler module end");
    }
}
