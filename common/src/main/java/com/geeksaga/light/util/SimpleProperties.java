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
public class SimpleProperties {
    private static final String WHITE_SPACE = " \t\r\n\f";

    private Map<String, Object> properties = new Hashtable<String, Object>();

    public SimpleProperties(String name) {
        loadSystemResource(name);
    }

    public SimpleProperties(File file, String name) {
        loadJarFile(file, name);
    }

    public String getProperty(String key)
    {
        Object oval = properties.get(key);
        if (oval instanceof String)
        {
            return (String) oval;
        }

        if (oval instanceof String[])
        {
            String[] v = (String[]) oval;
            if (v.length > 0)
            {
                return v[0];
            }
        }

        return null;
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

        return null;
    }

    private void loadSystemResource(String conf) {
        InputStream fin = null;

        try {
            fin = Thread.currentThread().getContextClassLoader().getResourceAsStream(conf);

            if (fin != null) {
                load(fin);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private void loadJarFile(File file, String name) {
        if (file != null && file.exists()) {
            JarFile jarFile = null;
            try {
                jarFile = new JarFile(file);
                ZipEntry ent = jarFile.getEntry(name);
                if (ent != null) {
                    InputStream fin = jarFile.getInputStream(ent);

                    try {
                        load(fin);
                    } finally {
                        fin.close();
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                try {
                    if (jarFile != null) {
                        jarFile.close();
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    private synchronized void load(InputStream inStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
        Set<String> lstKeys = new HashSet<String>();

        while (true) {
            String line = in.readLine();
            if (line == null) {
                break;
            }

            line = line.trim();
            if (line.length() == 0) {
                continue;
            }

            int len = line.length();
            int keyStart;
            for (keyStart = 0; keyStart < len; keyStart++) {
                if (WHITE_SPACE.indexOf(line.charAt(keyStart)) == -1) {
                    break;
                }
            }

            if (keyStart == len) {
                continue;
            }

            char firstChar = line.charAt(keyStart);
            if ((firstChar != '#') && (firstChar != '!')) {
                while (continueLine(line)) {
                    String nextLine = in.readLine();
                    if (nextLine == null) {
                        nextLine = "";
                    }

                    String loppedLine = line.substring(0, len - 1);
                    int startIndex;
                    for (startIndex = 0; startIndex < nextLine.length(); startIndex++) {
                        if (WHITE_SPACE.indexOf(nextLine.charAt(startIndex)) == -1) {
                            break;
                        }
                    }

                    nextLine = nextLine.substring(startIndex, nextLine.length());
                    line = loppedLine + nextLine;
                    len = line.length();
                }

                int separatorIndex;
                for (separatorIndex = keyStart; separatorIndex < len; separatorIndex++) {
                    char currentChar = line.charAt(separatorIndex);
                    if (currentChar == '\\') {
                        separatorIndex++;
                    } else if (WHITE_SPACE.indexOf(currentChar) != -1) {
                        break;
                    }
                }

                int valueIndex;
                for (valueIndex = separatorIndex; valueIndex < len; valueIndex++) {
                    if (WHITE_SPACE.indexOf(line.charAt(valueIndex)) == -1) {
                        break;
                    }
                }

                if (valueIndex < len) {
                    if ("=".indexOf(line.charAt(valueIndex)) != -1) {
                        valueIndex++;
                    }
                }

                while (valueIndex < len) {
                    if (WHITE_SPACE.indexOf(line.charAt(valueIndex)) == -1) {
                        break;
                    }

                    valueIndex++;
                }

                String key = line.substring(keyStart, separatorIndex);
                String value = (separatorIndex < len) ? line.substring(valueIndex, len) : "";

                key = loadConvert(key);
                value = loadConvert(value).trim();

                if (value.length() == 0) {
                    continue;
                }

                Object old = properties.get(key);
                if (old instanceof String) {
                    List<Object> list = new ArrayList<Object>();
                    list.add(old);
                    list.add(value);

                    properties.put(key, list);

                    lstKeys.add(key);
                } else if (old instanceof List) {
                    List list = (List) old;
                    list.add(value);
                } else {
                    properties.put(key, value);
                }
            }
        }

        for (String key : lstKeys) {
            Object value = properties.get(key);
            if (value instanceof List) {
                List ol = (List) value;
                String[] newValue = new String[ol.size()];

                for (int z = 0; z < ol.size(); z++) {
                    newValue[z] = (String) ol.get(z);
                }

                properties.put(key, newValue);
            }
        }
    }

    private boolean continueLine(String line) {
        int slashCount = 0;
        int index = line.length() - 1;
        while ((index >= 0) && (line.charAt(index--) == '\\')) {
            slashCount++;
        }

        return (slashCount % 2 != 0);
    }

    private String loadConvert(String theString) {
        char aChar;
        int len = theString.length();
        StringBuilder outBuffer = new StringBuilder(len);

        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    }

                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }

        return outBuffer.toString();
    }
}
