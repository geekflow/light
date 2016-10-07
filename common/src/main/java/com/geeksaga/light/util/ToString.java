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
package com.geeksaga.light.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author geeksaga
 */
public class ToString
{
    public static String toObjectString(Object obj)
    {
        List<String> fields = new ArrayList<String>();

        for (Field field : obj.getClass().getDeclaredFields())
        {
            fields.add(getValue(obj, field));
        }

        return ImmutableListToString.toString(ImmutableListUsingList.wrap(fields));
    }

    public static String toString(Object... args)
    {
        return ImmutableListToString.toString(ImmutableListUsingArray.wrap(args));
    }

    private static String getValue(Object obj, Field field)
    {
        Object value;

        try
        {
            field.setAccessible(true);

            value = field.get(obj);
        }
        catch (IllegalAccessException illegalAccessException)
        {
            throw new RuntimeException(illegalAccessException);
        }

        if (value == null)
        {
            return "null";
        }

        if (value.getClass().isArray())
        {
            return toArrayString(value);
        }

        return String.valueOf(value);
    }

    static String toArrayString(Object obj)
    {
        List<Object> list = new ArrayList<Object>();

        for (int i = 0, size = Array.getLength(obj); i < size; i++)
        {
            list.add(Array.get(obj, i));
        }

        return ImmutableListToString.toString(ImmutableListUsingList.wrap(list));
    }
}
