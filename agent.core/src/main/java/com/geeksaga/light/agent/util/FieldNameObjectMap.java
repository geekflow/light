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

import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author geeksaga
 */
public class FieldNameObjectMap
{
    private static final LightLogger logger = CommonLogger.getLogger(FieldNameObjectMap.class.getName());

    private final Map<String, Object> table = new HashMap<String, Object>();

    public Object get(Object key)
    {
        return table.get(key);
    }

    public Set<String> keys()
    {
        return table.keySet();
    }

    public Object put(String key, Object value)
    {
        table.put(key, value);

        return value;
    }

    public Object remove(Object key)
    {
        return table.remove(key);
    }

    public int size()
    {
        return table.size();
    }

    public Object getName(Object key)
    {
        return table.get(key);
    }

    public static FieldNameObjectMap toMap(Class<?> clazz)
    {
        return toMap(clazz, false, false);
    }

    public static FieldNameObjectMap toMap(Class<?> clazz, boolean toLower, boolean includeFirstSuperClass)
    {
        FieldNameObjectMap fieldNameObjectMap = new FieldNameObjectMap();

        if (clazz != null && includeFirstSuperClass)
        {
            toMapWithFirstSuperClass(clazz.getSuperclass(), toLower, fieldNameObjectMap);
        }

        if (clazz != null)
        {
            put(clazz.getDeclaredFields(), fieldNameObjectMap, toLower);
        }

        return fieldNameObjectMap;
    }

    private static void toMapWithFirstSuperClass(Class<?> clazz, boolean toLower, FieldNameObjectMap fieldNameObjectMap)
    {
        if (clazz != null && !"java.lang.Object".equals(clazz.getName()))
        {
            put(clazz.getDeclaredFields(), fieldNameObjectMap, toLower);
        }
    }

    private static void put(Field[] fields, FieldNameObjectMap fieldNameObjectMap, boolean toLower)
    {
        if (fields == null)
        {
            return;
        }

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

                if (toLower)
                {
                    fieldNameObjectMap.put(name.toLowerCase(), value);
                }
                else
                {
                    fieldNameObjectMap.put(name, value);
                }
            }
            catch (Exception exception)
            {
                logger.warn(exception);
            }
        }
    }
}