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
import com.geeksaga.light.profiler.util.ASMUtil;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

/**
 * @author geeksaga
 */
public class LightClassFileTransformer implements ClassFileTransformer
{
    private static final Logger logger = Logger.getLogger(LightClassFileTransformer.class.getName());

    private TraceRegisterBinder traceRegisterBinder;
    private TraceContext traceContext;

    public LightClassFileTransformer(TraceRegisterBinder traceRegisterBinder, TraceContext traceContext)
    {
        this.traceRegisterBinder = traceRegisterBinder;
        this.traceContext = traceContext;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
    {
        if (!className.startsWith("java") && !className.startsWith("sun"))
        {
            logger.info("Transform => " + className);

            ClassNodeWrapper clazz = ASMUtil.parse(classfileBuffer);

            if (clazz.isInterface())
            {
                return classfileBuffer;
            }

            ClassFileTransformer methodTransformer = new MethodTransformer();

            return methodTransformer.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
        }

        return classfileBuffer;
    }
}