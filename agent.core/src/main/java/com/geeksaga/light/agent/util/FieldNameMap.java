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
package com.geeksaga.light.agent.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * @author geeksaga
 */
public class FieldNameMap
{
    private final Map<String, String> table = new Hashtable<String, String>();

    public boolean hasName(String key)
    {
        return table.containsKey(key);
    }

    public String get(Object index)
    {
        return table.get(index);
    }

    public String put(String value)
    {
        return table.put(value, value);
    }

    public int size()
    {
        return table.size();
    }

    public Set<String> keySet()
    {
        return table.keySet();
    }

    public Collection<String> values()
    {
        return table.values();
    }

    public static FieldNameMap toMap(Class<?> clazz)
    {
        return toMap(clazz, false, false);
    }

    public static FieldNameMap toMap(Class<?> clazz, boolean toLower, boolean includeFirstSuperClass)
    {
        FieldNameMap fieldNameMap = new FieldNameMap();

        if (clazz != null)
        {
            put(clazz.getDeclaredFields(), fieldNameMap, toLower);
        }

        if (clazz != null && includeFirstSuperClass)
        {
            toMapWithFirstSuperClass(clazz.getSuperclass(), toLower, fieldNameMap);
        }

        return fieldNameMap;
    }

    private static void toMapWithFirstSuperClass(Class<?> clazz, boolean toLower, FieldNameMap fieldNameMap)
    {
        if (clazz != null && !"java.lang.Object".equals(clazz.getName()))
        {
            put(clazz.getDeclaredFields(), fieldNameMap, toLower);
        }
    }

    private static void put(Field[] fields, FieldNameMap fieldNameMap, boolean toLower)
    {
        if (fields != null)
        {
            for (Field field : fields)
            {
                int access_flags = field.getModifiers();

                if ((access_flags & Modifier.PUBLIC) == 0 || (access_flags & Modifier.STATIC) == 0 || (access_flags & Modifier.FINAL) == 0)
                {
                    continue;
                }

                try
                {
                    Object value = field.get(null);
                    String name = field.getName();

                    if (value instanceof String)
                    {
                        if (toLower)
                        {
                            fieldNameMap.put(name.toLowerCase());
                        }
                        else
                        {
                            fieldNameMap.put(name);
                        }
                    }
                }
                catch (Exception exception)
                {
                    exception.printStackTrace();
                }
            }
        }
    }
}