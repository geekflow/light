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
import com.geeksaga.light.agent.core.DefaultTraceRegistryAdaptor;
import com.geeksaga.light.agent.core.TraceRegistry;
import com.geeksaga.light.profiler.instrument.transformer.ClassFileTransformerDispatcher;
import com.geeksaga.light.profiler.instrument.transformer.LightClassFileTransformer;
import com.geeksaga.light.profiler.instrument.transformer.MethodParameterTransformer;
import com.geeksaga.light.profiler.instrument.transformer.MethodReturnTransformer;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

/**
 * @author geeksaga
 */
public class ProfilerModule implements Module {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private final Object lock = new Object();

    private Instrumentation instrumentation;

    public ProfilerModule(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;

        // FIXME separate bind ?
        TraceRegistry.bind(new DefaultTraceRegistryAdaptor(), lock);
    }

    @Override
    public void start() {
        logger.info("profiler module start");

        // TODO transformer dispatcher
        // instrumentation.addTransformer(new ClassFileTransformerDispatcher(), true);
        instrumentation.addTransformer(new MethodParameterTransformer(), true);
        instrumentation.addTransformer(new MethodReturnTransformer(), true);
        instrumentation.addTransformer(new LightClassFileTransformer(), true);
    }

    @Override
    public void stop() {
        logger.info("profiler module end");
    }
}
