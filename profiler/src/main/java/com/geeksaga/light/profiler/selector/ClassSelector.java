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
package com.geeksaga.light.profiler.selector;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author geeksaga
 */
public class ClassSelector
{
    Map<String, MethodSelector> selectors = new ConcurrentHashMap<String, MethodSelector>();

    public void add(String fullDescription)
    {
        if (isNotNone(fullDescription))
        {
            String[] classMethod = divideClassMethod(fullDescription.trim());
            if (!"java.lang.Object".equals(classMethod[0]) && isNotNone(classMethod[0]))
            {
                add(classMethod[0], classMethod[1]);
            }
        }
    }

    public void add(String clazz, String fullDescription)
    {
        MethodSelector methodSelector = selectors.get(clazz);
        if (methodSelector == null)
        {
            methodSelector = new MethodSelector();

            selectors.put(clazz, methodSelector);
        }

        if (isNotNone(fullDescription))
        {
            methodSelector.add(fullDescription);
        }
    }

    private String[] divideClassMethod(String fullDescriptor)
    {
        String[] classMethod = new String[2];

        int indexOf = fullDescriptor.indexOf(" ");
        if (indexOf <= 0)
        {
            classMethod[0] = fullDescriptor.trim();
            classMethod[1] = null;
        }
        else
        {
            classMethod[0] = fullDescriptor.substring(0, indexOf).trim();
            classMethod[1] = fullDescriptor.substring(indexOf + 1).trim();
        }

        return classMethod;
    }

    private boolean isNotNone(String value)
    {
        return value != null && value.length() > 0;
    }

    private MethodSelector selectBySuperOrInterface(String[] superOrInterfaces)
    {
        if (superOrInterfaces != null)
        {
            for (String superOrInterface : superOrInterfaces)
            {
                Object o = selectors.get(superOrInterface);
                if (o != null)
                {
                    return (MethodSelector) o;
                }
            }
        }

        return null;
    }

    public MethodSelector selectByClass(String name)
    {
        return selectors.get(name);
    }

    public MethodSelector selectByInterface(String[] interfaces)
    {
        return selectBySuperOrInterface(interfaces);
    }

    public MethodSelector selectBySuper(String name)
    {
        return selectors.get(name);
    }

    public MethodSelector selectBySuper(String[] superClasses)
    {
        return selectBySuperOrInterface(superClasses);
    }

    public MethodSelector selectByPattern(String name)
    {
        return selectors.get(name);
    }

    public boolean isEmpty()
    {
        return selectors.isEmpty();
    }

    public boolean containsKey(Object key)
    {
        return selectors.containsKey(key);
    }

    public static ClassSelector create(String[] list)
    {
        ClassSelector classSelector = new ClassSelector();

        if (list != null)
        {
            for (String value : list)
            {
                classSelector.add(value);
            }
        }

        return classSelector;
    }

    public static ClassSelector create(List<String> list)
    {
        ClassSelector classSelector = new ClassSelector();

        for (String value : list)
        {
            //            classSelector.add(value.replace(".", "/"));
            classSelector.add(value);

            System.out.println(value);
        }

        return classSelector;
    }
}