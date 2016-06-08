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

import java.io.*;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author geeksaga
 */
public class SimpleProperties
{
    private static final String WHITE_SPACE = " \t\r\n\f";

    private Map<String, Object> properties = new Hashtable<String, Object>();

    public SimpleProperties(String name)
    {
        loadSystemResource(name);
    }

    public SimpleProperties(File file, String name)
    {
        loadFormFile(file, name);
    }

    public String getProperty(String key)
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

        return "";
    }

    public String[] getPropertyList(String key)
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

    private void loadSystemResource(String conf)
    {
        InputStream inputStream = null;

        try
        {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(conf);

            if (inputStream != null)
            {
                load(inputStream);
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        finally
        {
            close(inputStream);
        }
    }

    private void loadFormFile(File file, String name)
    {
        if (file != null && file.exists())
        {
            JarFile jarFile = null;
            InputStream inputStream = null;
            try
            {
                jarFile = new JarFile(file);
                ZipEntry entry = jarFile.getEntry(name);
                if (entry != null)
                {
                    inputStream = jarFile.getInputStream(entry);

                    load(inputStream);
                }
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
            finally
            {
                close(inputStream);

                try
                {
                    if (jarFile != null)
                    {
                        jarFile.close();
                    }
                }
                catch (IOException ioException)
                {
                    ioException.printStackTrace();
                }
            }
        }
    }

    private void close(InputStream inputStream)
    {
        try
        {
            if (inputStream != null)
            {
                inputStream.close();
            }
        }
        catch (IOException ioException)
        {
            ioException.printStackTrace();
        }
    }

    private synchronized void load(InputStream inStream) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
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

    private void parse(String line, int startLine, Set<String> keys)
    {
        if (!isComment(line, startLine))
        {
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
    }

    private void put(int indexOfKey, int indexOfValue, int startLine, String line, Set<String> keys)
    {
        String value = (indexOfKey < line.length()) ? line.substring(indexOfValue, line.length()).trim() : "";
        if (value.length() > 0)
        {
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