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
import com.geeksaga.light.profiler.asm.ClassNodeWrapper;
import com.geeksaga.light.profiler.selector.ClassPatternSelector;
import com.geeksaga.light.profiler.selector.ClassSelector;
import com.geeksaga.light.profiler.selector.MethodSelector;

/**
 * @author geeksaga
 */
class AbstractFilter
{
    private ClassSelector selectIgnoreClass = null;
    private ClassSelector selectIgnoreSuperClass = null;
    private ClassSelector selectIgnoreInterface = null;
    private ClassPatternSelector selectIgnoreClassPattern = null;

    private TraceContext traceContext;

    AbstractFilter(TraceContext traceContext)
    {
        this.traceContext = traceContext;
    }

    void createIgnore(String ignoreClassKey, String ignoreSuperClassKey, String ignoreInterfaceKey, String ignoreClassPatternKey)
    {
        selectIgnoreClass = ClassSelector.create(traceContext.getConfig().read(ignoreClassKey));
        selectIgnoreSuperClass = ClassSelector.create(traceContext.getConfig().read(ignoreSuperClassKey));
        selectIgnoreInterface = ClassSelector.create(traceContext.getConfig().read(ignoreInterfaceKey));
        selectIgnoreClassPattern = ClassPatternSelector.create(traceContext.getConfig().read(ignoreClassPatternKey));
    }

    MethodSelector getSelector(ClassNodeWrapper clazz)
    {
        MethodSelector methodSelector;

        if (selectIgnoreClassPattern != null && !selectIgnoreClassPattern.isEmpty())
        {
            methodSelector = selectIgnoreClassPattern.selectByPattern(clazz.getClassName());
            if (methodSelector != null)
            {
                return methodSelector;
            }
        }

        if (selectIgnoreClass != null && !selectIgnoreClass.isEmpty())
        {
            methodSelector = selectIgnoreClass.selectByClass(clazz.getClassName());
            if (methodSelector != null)
            {
                return methodSelector;
            }
        }

        if (selectIgnoreSuperClass != null && !selectIgnoreSuperClass.isEmpty())
        {
            methodSelector = selectIgnoreSuperClass.selectByInterface(clazz.getAllInterfaceNames());
            if (methodSelector != null)
            {
                return methodSelector;
            }
        }

        if (selectIgnoreInterface != null && !selectIgnoreInterface.isEmpty())
        {
            methodSelector = selectIgnoreInterface.selectBySuper(clazz.getAllSuperClassNames());
            if (methodSelector != null)
            {
                return methodSelector;
            }
        }

        return null;
    }
}