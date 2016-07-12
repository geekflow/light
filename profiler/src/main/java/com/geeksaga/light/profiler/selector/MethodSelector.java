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

import java.util.HashSet;
import java.util.Set;

/**
 * @author geeksaga
 */
public class MethodSelector
{
    private Set<Item> methodMatch = null;

    public MethodSelector() {}

    public int size()
    {
        if (methodMatch != null)
        {
            return methodMatch.size();
        }

        return 0;
    }

    public boolean isEmpty()
    {
        return size() == 0;
    }

    public void add(String method)
    {
        if (methodMatch == null)
        {
            methodMatch = new HashSet<Item>();
        }

        int indexOf = method.indexOf("(");
        if (indexOf < 0)
        {
            methodMatch.add(new Item(method, null));
        }
        else
        {
            methodMatch.add(new Item(method.substring(0, indexOf), method.substring(indexOf)));
        }
    }

    private class Item
    {
        private String method;
        private String description;

        public Item(String method, String signature)
        {
            this.method = method;
            this.description = signature;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }

            if (obj instanceof Item)
            {
                Item o = (Item) obj;

                if (description == null)
                {
                    return method.equals(o.method) && o.description == null;
                }
                else
                {
                    return method.equals(o.method) && description.equals(o.description);
                }
            }

            return false;
        }

        @Override
        public int hashCode()
        {
            int result = 17;
            result = 31 * result + method.hashCode();

            if (description != null)
            {
                result = 31 * result + description.hashCode();
            }

            return result;
        }

        @Override
        public String toString()
        {
            return String.format("%s %s", method, description);
        }
    }

    public boolean isSelected(String name, String description)
    {
        return isSelected(name, description, false);
    }

    public boolean isSelected(String name, String description, boolean isStrict)
    {
        if (isStrict)
        {
            return (methodMatch != null) && methodMatch.contains(new Item(name, description));
        }

        return methodMatch == null || methodMatch.contains(new Item(name, description));
    }
}