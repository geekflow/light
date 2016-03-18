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
package com.geeksaga.light.profiler.instrument.transformer;

import com.geeksaga.light.agent.TraceContext;
import com.geeksaga.light.agent.core.TraceRegisterBinder;
import com.geeksaga.light.profiler.asm.ClassNodeWrapper;
import com.geeksaga.light.profiler.filter.Filter;
import com.geeksaga.light.profiler.filter.LightFilter;
import com.geeksaga.light.profiler.util.ASMUtil;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author geeksaga
 */
public class ClassFileTransformerDispatcher implements ClassFileTransformer {
    private static final Logger logger = Logger.getLogger(ClassFileTransformerDispatcher.class.getName());

    public static ThreadLocal<ClassLoader> context = new ThreadLocal<ClassLoader>();

    private TraceRegisterBinder traceRegisterBinder;
    private TraceContext traceContext;
    private ClassFileTransformer classFileTransformer;

    private Filter filter = new LightFilter();

    public ClassFileTransformerDispatcher(TraceRegisterBinder traceRegisterBinder, TraceContext traceContext) {
        this.traceRegisterBinder = traceRegisterBinder;
        this.traceContext = traceContext;
        this.classFileTransformer = new EntryPointTransformer(traceRegisterBinder, traceContext);
    }

    @Override
    public byte[] transform(ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            if (classfileBuffer == null || className == null) { // || className.startsWith("com/geeksaga/light")) {
                return null;
            }

            if (filter.allow(classLoader, className)) {

                long now = System.currentTimeMillis();
                context.set(classLoader);

                ClassNodeWrapper clazz = ASMUtil.parse(classfileBuffer);

                if (clazz.isInterface()) {
                    return classfileBuffer;
                }

                byte[] bytes = classFileTransformer.transform(classLoader, className, classBeingRedefined, protectionDomain, classfileBuffer);

                long dur = System.currentTimeMillis();

                StringBuilder sb = new StringBuilder();
                sb.append("LOAD:[");

                if (classLoader == null) {
                    sb.append("BootstrapClassLoader");
                    sb.append("] [");
                } else {
                    sb.append(Integer.toHexString(classLoader.hashCode()));
                    sb.append("] [");
                    sb.append(classLoader.getClass().getName());

                    sb.append("] ");
                }

                sb.append(clazz.getClassName());
                sb.append(" ");
                sb.append(classfileBuffer.length);
                sb.append(" bytes ");
                sb.append((dur - now));
                sb.append(" ms");

                logger.info(sb.toString());

                if (bytes != null) {
                    return bytes;
                }
            }
        } catch (Throwable throwable) {
            logger.log(Level.WARNING, throwable.getMessage(), throwable);
        }

        return classfileBuffer;
    }
}
