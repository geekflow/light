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
package com.geeksaga.light.profiler.filter;

import com.geeksaga.light.agent.TraceContext;
import com.geeksaga.light.agent.config.ConfigDef;
import com.geeksaga.light.profiler.asm.ClassNodeWrapper;
import com.geeksaga.light.profiler.util.ASMUtil;

import java.util.List;

/**
 * @author geeksaga
 */
public class LightFilter implements Filter
{
    private TraceContext traceContext;

    public LightFilter(TraceContext traceContext)
    {
        this.traceContext = traceContext;
    }

    @Override
    public boolean allow(ClassLoader classLoader, String className)
    {
        if (ignorePattern(className, true) || (classLoader != null && classLoader.getClass().getName().startsWith("com.geeksaga.light")))
        {
            return false;
        }

        return true;
    }

    @Override
    public boolean allow(ClassLoader classLoader, String className, byte[] classfileBuffer)
    {
        if (classfileBuffer == null) // || classfileBuffer.length) FIXME is max size
        {
            return false;
        }

        return allow(classLoader, className);
    }

    @Override
    public boolean allow(ClassLoader classLoader, ClassNodeWrapper classNodeWrapper)
    {
        return allow(classLoader, classNodeWrapper.getClassName());
    }

    @Override
    public boolean allow(ClassLoader classLoader, ClassNodeWrapper classNodeWrapper, byte[] classfileBuffer)
    {
        return allow(classLoader, classNodeWrapper.getClassName(), classfileBuffer);
    }

    private boolean ignorePattern(final String className, boolean useConvertName)
    {
        return ignorePattern(useConvertName ? ASMUtil.convertForAgent(className) : className);
    }

    private boolean ignorePattern(String className)
    {
        List<String> values = traceContext.getConfig().read(ConfigDef.ignore_bci_pattern);

        for (String value : values)
        {
            if (className.startsWith(value))
            {
                return true;
            }
        }

        return false;
    }
}
