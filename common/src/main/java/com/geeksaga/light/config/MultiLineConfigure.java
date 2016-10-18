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
package com.geeksaga.light.config;

import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;
import com.geeksaga.light.util.SystemProperty;

import java.io.*;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author geeksaga
 */
public class MultiLineConfigure
{
    private static final LightLogger logger = CommonLogger.getLogger(MultiLineConfigure.class.getName());

    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String WHITE_SPACE = " \t\r\n\f";

    private Map<String, Object> properties = new Hashtable<String, Object>();

    public MultiLineConfigure()
    {
        loadSystemResource(null, SystemProperty.LIGHT_CONFIG);
    }

    public MultiLineConfigure(String name)
    {
        loadSystemResource(name);
    }

    public MultiLineConfigure(ClassLoader classLoader, String name)
    {
        loadSystemResource(classLoader, name);
    }

    public MultiLineConfigure(String path, String name)
    {
        loadFormFile(path, name);
    }

    public MultiLineConfigure(File file, String name)
    {
        loadFormFile(file, name);
    }

    public int size()
    {
        return properties.size();
    }

    public Set<String> keys()
    {
        return properties.keySet();
    }

    String getValueOrNull(String key)
    {
        Object value = properties.get(key);
        if (value instanceof String)
        {
            return (String) value;
        }

        if (value instanceof String[])
        {
            String[] values = (String[]) value;
            if (values.length > 0)
            {
                return values[0];
            }
        }

        return null;
    }

    public Object get(String key)
    {
        return properties.get(key);
    }

    public String getValueOrNull(String key, String defaultValue)
    {
        String val = getValueOrNull(key);
        return (val == null) ? defaultValue : val;
    }

    public String[] getValues(String key)
    {
        Object value = properties.get(key);
        if (value instanceof String)
        {
            return new String[] { (String) value };
        }

        if (value instanceof String[])
        {
            return (String[]) value;
        }

        return new String[] {};
    }

    public String[] getValues(String key, String[] defaultValue)
    {
        String[] values = getValues(key);

        return values.length > 0 ? values : defaultValue;
    }

    private void loadSystemResource(String name)
    {
        loadSystemResource(Thread.currentThread().getContextClassLoader(), name);
    }

    private void loadSystemResource(ClassLoader classLoader, String name)
    {
        try
        {
            load(classLoader, name);
        }
        catch (Exception exception)
        {
            logger.info(exception);
        }
    }

    private void loadFormFile(String path, String name)
    {
        if(path == null)
        {
            return;
        }

        loadFormFile(new File(path), name);
    }

    private void loadFormFile(File file, String name)
    {
        if (file == null || !file.exists())
        {
            return;
        }

        JarFile jarFile = null;
        InputStream inputStream = null;
        try
        {
            jarFile = new JarFile(file);
            ZipEntry entry = jarFile.getEntry(name);
            if (entry == null)
            {
                return;
            }

            inputStream = jarFile.getInputStream(entry);

            load(inputStream);
        }
        catch (Exception exception)
        {
            logger.info(exception);
        }
        finally
        {
            close(inputStream);
            close(jarFile);
        }
    }

    private void close(Closeable closeable)
    {
        if (closeable == null)
        {
            return;
        }

        try
        {
            closeable.close();
        }
        catch (IOException ioException)
        {
            logger.info(ioException);
        }
    }

    public void load(String name) throws IOException
    {
        load(name, DEFAULT_ENCODING);
    }

    public void load(String name, String encoding) throws IOException
    {
        load(new FileInputStream(name), encoding);
    }

    public void load(ClassLoader classLoader, String name) throws IOException
    {
        if (classLoader == null)
        {
            load(name, DEFAULT_ENCODING);

            return;
        }

        InputStream inputStream = classLoader.getResourceAsStream(name);

        if (inputStream != null)
        {
            load(inputStream, DEFAULT_ENCODING);
        }
    }

    private void load(InputStream inputStream) throws IOException
    {
        load(inputStream, DEFAULT_ENCODING);
    }

    private void load(InputStream inputStream, String encoding)
    {
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new InputStreamReader(inputStream, encoding));
            Set<String> keys = new HashSet<String>();
            String line;

            while ((line = reader.readLine()) != null)
            {
                line = line.trim();
                if (line.length() == 0)
                {
                    continue;
                }

                int startLine = indexOf(line);
                if (startLine == line.length())
                {
                    continue;
                }

                parse(line, startLine, keys);
            }

            listToArray(keys);
        }
        catch (Exception e)
        {
            logger.warn(e);
        }
        finally
        {
            close(reader);
        }
    }

    public void addAll(MultiLineConfigure multiLineConfigure)
    {
        if (multiLineConfigure == null || multiLineConfigure.size() == 0)
        {
            return;
        }

        for (String key : multiLineConfigure.keys())
        {
            Object newValue = multiLineConfigure.get(key);
            Object oldValue = this.get(key);
            if (oldValue instanceof String)
            {
                if (newValue instanceof String)
                {
                    newValue = merge((String) oldValue, (String) newValue);
                }
                else if (newValue instanceof String[])
                {
                    newValue = merge((String) oldValue, (String[]) newValue);
                }
            }
            else if (oldValue instanceof String[])
            {
                if (newValue instanceof String)
                {
                    newValue = merge((String[]) oldValue, (String) newValue);
                }
                else if (newValue instanceof String[])
                {
                    newValue = merge((String[]) oldValue, (String[]) newValue);
                }
            }

            properties.put(key, newValue);
        }
    }

    private Object merge(String a, String b)
    {
        return uniqueArrayValues(new String[] { a, b });
    }

    private Object merge(String a, String[] b)
    {
        return uniqueArrayValues(merge0(new String[] { a }, (b != null ? b : new String[] {})));
    }

    private Object merge(String[] a, String b)
    {
        return uniqueArrayValues(merge0((a != null ? a : new String[] {}), new String[] { b }));
    }

    private Object merge(String[] a, String[] b)
    {
        return merge0((a != null ? a : new String[] {}), (b != null ? b : new String[] {}));
    }

    private String[] merge0(String[] a, String[] b)
    {
        String[] result = new String[a.length + b.length];

        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);

        return result;
    }

    private Object uniqueArrayValues(String[] values)
    {
        return removeNullValue(new HashSet<String>(Arrays.asList(values)).toArray(new String[0]));
    }

    private Object removeNullValue(String[] values)
    {
        List<String> list = new ArrayList<String>();

        for (String value : values)
        {
            if (value != null && value.length() > 0)
            {
                list.add(value);
            }
        }

        return list.toArray(new String[list.size()]);
    }

    private void parse(String line, int startLine, Set<String> keys)
    {
        if (isComment(line, startLine))
        {
            return;
        }

        int length = line.length();

        int indexOfKey;
        for (indexOfKey = startLine; indexOfKey < length; indexOfKey++)
        {
            if (WHITE_SPACE.indexOf(line.charAt(indexOfKey)) != -1)
            {
                break;
            }
        }

        int indexOfValue;
        for (indexOfValue = indexOfKey; indexOfValue < length; indexOfValue++)
        {
            if (WHITE_SPACE.indexOf(line.charAt(indexOfValue)) == -1)
            {
                break;
            }
        }

        if (indexOfValue < length && "=".indexOf(line.charAt(indexOfValue)) != -1)
        {
            indexOfValue++;
        }

        while (indexOfValue < length)
        {
            if (WHITE_SPACE.indexOf(line.charAt(indexOfValue)) == -1)
            {
                break;
            }

            indexOfValue++;
        }

        put(indexOfKey, indexOfValue, startLine, line, keys);
    }

    private void put(int indexOfKey, int indexOfValue, int startLine, String line, Set<String> keys)
    {
        String value = (indexOfKey < line.length()) ? line.substring(indexOfValue, line.length()).trim() : "";
        if (value.length() == 0)
        {
            return;
        }

        String key = line.substring(startLine, indexOfKey);
        Object beforeValue = properties.get(key);
        if (beforeValue instanceof String)
        {
            properties.put(key, createList(beforeValue, value));

            keys.add(key);
        }
        else if (beforeValue instanceof List)
        {
            List list = (List) beforeValue;
            list.add(value);
        }
        else
        {
            properties.put(key, value);
        }
    }

    private List<Object> createList(Object beforeValue, Object value)
    {
        List<Object> list = new ArrayList<Object>();
        list.add(beforeValue);
        list.add(value);

        return list;
    }

    private boolean isComment(String line, int startIndexOfKey)
    {
        char firstChar = line.charAt(startIndexOfKey);

        return !((firstChar != '#') && (firstChar != '!'));
    }

    private void listToArray(Set<String> keys)
    {
        for (String key : keys)
        {
            Object value = properties.get(key);
            if (value instanceof List)
            {
                List list = (List) value;

                properties.put(key, list.toArray(new String[list.size()]));
            }
        }
    }

    private int indexOf(String line)
    {
        int index;
        int length = line.length();

        for (index = 0; index < length; index++)
        {
            if (WHITE_SPACE.indexOf(line.charAt(index)) == -1)
            {
                break;
            }
        }

        return index;
    }
}