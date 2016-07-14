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
import com.geeksaga.light.agent.config.ConfigValueDef;
import com.geeksaga.light.profiler.selector.ClassSelector;
import com.geeksaga.light.profiler.selector.MethodSelector;
import com.geeksaga.light.profiler.util.ASMUtil;

import java.util.List;

import static com.geeksaga.light.agent.config.ConfigDef.entry_point;

/**
 * @author geeksaga
 */
public class EntryFilter extends AbstractFilter implements Filter
{
    private ClassSelector classSelector;

    public EntryFilter(TraceContext traceContext)
    {
        this(traceContext, ClassSelector.create(traceContext.getConfig().read(entry_point)));
    }

    public EntryFilter(TraceContext traceContext, ClassSelector classSelector)
    {
        super(traceContext);

        this.classSelector = classSelector;

        refresh();
    }

    public boolean refresh()
    {
        createIgnore("", "", "", "");

        return true;
    }

    @Override
    public boolean allow(ClassLoader classLoader, String className)
    {
        //        MethodSelector selector = getSelector(null);

        MethodSelector methodSelector = classSelector.selectByClass(className);

        if (methodSelector != null)
        {
            return true;
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
}
