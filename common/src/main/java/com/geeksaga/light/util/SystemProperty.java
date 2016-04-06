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

/**
 * @author geeksaga
 */
public class SystemProperty {
    public static final boolean IS_JAVA_16 = getSystemProperty("java.version", "1.6").startsWith("1.6");
    public static final boolean IS_JAVA_17 = getSystemProperty("java.version", "1.7").startsWith("1.7");
    public static final boolean IS_JAVA_18 = getSystemProperty("java.version", "1.8").startsWith("1.8");
    public static final boolean IS_JAVA_19 = getSystemProperty("java.version", "1.9").startsWith("1.9");
    public static final boolean IS_JAVA_16_PLUS = IS_JAVA_16 || IS_JAVA_17 || IS_JAVA_18;
    public static final boolean IS_JAVA_17_PLUS = IS_JAVA_17 || IS_JAVA_18;
    public static final boolean IS_JAVA_18_PLUS = IS_JAVA_18 || IS_JAVA_19;
    public static final boolean IS_JAVA_19_PLUS = IS_JAVA_19;

    public static final String JAVA_CLASS_PATH = getSystemProperty("java.class.path");
    public static final String JAVA_VERSION = getSystemProperty("java.version");
    public static final String JAVA_HOME = getSystemProperty("java.home");
    public static final String JAVA_VM_NAME = getSystemProperty("java.vm.name");
    public static final String JAVA_VM_VERSION = getSystemProperty("java.vm.version");
    public static final String JAVA_VENDOR = getSystemProperty("java.vendor");
    public static final String JAVA_VM_VENDOR = getSystemProperty("java.vm.vendor");
    public static final String JVM = getSystemProperty("jvm");
    public static final String OS_ARCH = getSystemProperty("os.arch");
    public static final String OS_NAME = getSystemProperty("os.name");
    public static final String OS_VERSION = getSystemProperty("os.version");
    public static final String USER_HOME = getSystemProperty("user.home");
    public static final String USER_TIMEZONE = getSystemProperty("user.timezone");
    public static final String FILE_ENCODING = getSystemProperty("file.encoding");

    public static final boolean WINDOWS_OS = getSystemProperty("os.name", "unix").contains("Window");
    public static final boolean IBM_VM = JAVA_VENDOR.toUpperCase().startsWith("IBM");

    public static final String LINE_SEPARATOR = getSystemProperty("line.separator");
    public static final String PATH_SEPARATOR = getSystemProperty("path.separator");

    public static final String LIGHT_CONFIG = getSystemProperty("light.config", "light.conf");

    private static String getSystemProperty(String key)
    {
        return getSystemProperty(key, "");
    }

    private static String getSystemProperty(String key, String def) {
        try {
            return System.getProperty(key, def);
        } catch (RuntimeException exception) {
            return def;
        }
    }
}
