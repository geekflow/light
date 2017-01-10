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
import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;
import com.geeksaga.light.profiler.asm.ClassNodeWrapper;
import com.geeksaga.light.profiler.filter.Filter;
import com.geeksaga.light.profiler.filter.LightFilter;
import com.geeksaga.light.profiler.util.ASMUtil;
import com.geeksaga.light.profiler.util.ClassFileDumper;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.geeksaga.light.agent.config.ConfigDef.dump_mode;
import static com.geeksaga.light.agent.config.ConfigDefaultValueDef.default_dump_mode;

/**
 * @author geeksaga
 */
public class ClassFileTransformerDispatcher implements ClassFileTransformer
{
    public static final ThreadLocal<ClassLoader> context = new ThreadLocal<ClassLoader>();

    private LightLogger logger;
    private TraceRegisterBinder traceRegisterBinder;
    private TraceContext traceContext;
    private Filter filter;
    private List<LightClassFileTransformer> classFileTransformerList;

    public ClassFileTransformerDispatcher(TraceRegisterBinder traceRegisterBinder, TraceContext traceContext)
    {
        this(traceRegisterBinder, traceContext, Collections.synchronizedList(new ArrayList<LightClassFileTransformer>()));
    }

    public ClassFileTransformerDispatcher(TraceRegisterBinder traceRegisterBinder, TraceContext traceContext, List<LightClassFileTransformer> classFileTransformerList)
    {
        this.logger = CommonLogger.getLogger(getClass().getName());
        this.filter = new LightFilter(traceContext);

        this.traceRegisterBinder = traceRegisterBinder;
        this.traceContext = traceContext;
        this.classFileTransformerList = classFileTransformerList;

        logger.info("create transformer {}", getClass().getName());
    }

    @Override
    public byte[] transform(ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
    {
        try
        {
            if (!filter.allow(classLoader, className, classfileBuffer))
            {
                return classfileBuffer;
            }

            long now = System.currentTimeMillis();
            context.set(classLoader);

            ClassNodeWrapper classNodeWrapper = ASMUtil.parse(classfileBuffer);

            if (classNodeWrapper.isInterface())
            {
                return classfileBuffer;
            }

            //            byte[] bytes = null;
            //            for (ClassFileTransformer classFileTransformer : classFileTransformerList)
            //            {
            //                bytes = classFileTransformer.transform(classLoader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            //            }

            ClassNodeWrapper patchedClassNodeWrapper = classNodeWrapper;

            for (LightClassFileTransformer classFileTransformer : classFileTransformerList)
            {
                patchedClassNodeWrapper = classFileTransformer.transform(classLoader, classBeingRedefined, classfileBuffer, patchedClassNodeWrapper);
            }

            long dur = System.currentTimeMillis();

            StringBuilder sb = new StringBuilder();
            sb.append("LOAD:[");

            if (classLoader == null)
            {
                sb.append("BootstrapClassLoader");
                sb.append("] [");
            }
            else
            {
                sb.append(Integer.toHexString(classLoader.hashCode()));
                sb.append("] [");
                sb.append(classLoader.getClass().getName());

                sb.append("] ");
            }

            sb.append(classNodeWrapper.getClassName());
            sb.append(" ");
            sb.append(classfileBuffer.length);
            sb.append(" bytes ");
            sb.append((dur - now));
            sb.append(" ms");

            //            logger.info(sb.toString());

            //            if (bytes != null)
            //            {
            //                return bytes;
            //            }

            byte[] hookedClassFileBuffer = ASMUtil.toBytes(patchedClassNodeWrapper);

            if (traceContext.getConfig().read(dump_mode, default_dump_mode))
            {
                ClassFileDumper.dump(classNodeWrapper.getClassName(), classfileBuffer, hookedClassFileBuffer);
            }

            return hookedClassFileBuffer;
        }
        catch (Throwable throwable)
        {
            logger.debug(throwable);
        }

        return classfileBuffer;
    }
}
