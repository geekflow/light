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
public class ClassPatternSelector extends ClassSelector
{
    private Map<String, PatternMatch> patterns = new ConcurrentHashMap<String, PatternMatch>();

    @Override
    public void add(String clazz, String fullDescriptor)
    {
        try
        {
            MethodSelector methodSelector = selectors.get(clazz);
            if (methodSelector == null)
            {
                methodSelector = new MethodSelector(true);

                selectors.put(clazz, methodSelector);

                patterns.put(clazz, new PatternMatch(clazz));
            }

            if (fullDescriptor != null && fullDescriptor.length() > 0)
            {
                methodSelector.add(fullDescriptor);
            }
        }
        catch (Exception exception)
        {
            logger.info(exception);
        }
    }

    public MethodSelector selectByPattern(String value)
    {
        for (int i = 0; i < patterns.size(); i++)
        {
            PatternMatch patternMatch = patterns.get(i);
            if (patternMatch.matches(value))
            {
                return selectors.get(i);
            }
        }

        return null;
    }

    public static ClassPatternSelector create(String[] list)
    {
        ClassPatternSelector classPatternSelector = new ClassPatternSelector();
        if (list != null)
        {
            for (String value : list)
            {
                classPatternSelector.add(value);
            }
        }

        return classPatternSelector;
    }

    public static ClassPatternSelector create(List<String> list)
    {
        ClassPatternSelector classPatternSelector = new ClassPatternSelector();

        for (String value : list)
        {
            classPatternSelector.add(value);
        }

        return classPatternSelector;
    }
}