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
package com.geeksaga.light.agent;

import com.geeksaga.light.Product;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author geeksaga
 */
public class JavaAgent
{
    private static final Logger logger = Logger.getLogger(JavaAgent.class.getName());

    private static final boolean STATUS_NONE = false;
    private static final boolean STATUS_LOADED = true;

    protected static final AtomicBoolean STATUS = new AtomicBoolean(STATUS_NONE);

    private static Instrumentation instrumentation;

    public static void premain(String options, Instrumentation instrumentation)
    {
        init(options, instrumentation, false);
    }

    public static void agentmain(String options, Instrumentation instrumentation)
    {
        init(options, instrumentation, true);
    }

    private static void init(String options, Instrumentation instrumentation, boolean attach)
    {
        if (updateStatusAndCheckDuplicate())
        {
            failInitialize();
            return;
        }

        JavaAgent.instrumentation = instrumentation;

        Bootstrap bootstrap = new Bootstrap(options, instrumentation);
        bootstrap.initialize(attach);
    }

    private static boolean updateStatusAndCheckDuplicate()
    {
        if (STATUS.compareAndSet(STATUS_NONE, STATUS_LOADED))
        {
            return false;
        }

        logger.warning(Product.NAME + " Agent already initialized. Skip agent loading.");

        return true;
    }

    public static long sizeOf(Object o)
    {
        assert getInstrumentation() != null;
        return getInstrumentation().getObjectSize(o);
    }

    public static Instrumentation getInstrumentation()
    {
        return instrumentation;
    }

    public static Class<?> findClass(String className)
    {
        return findClass(getAllLoadedClasses(), className);
    }

    public static Class<?> findClass(String className, final ClassLoader classLoader)
    {
        return findClass(getClassLoaderInitiatedClasses(classLoader), className);
    }

    public static Class<?> findClass(Class<?>[] classes, String className)
    {
        if (classes == null || className == null || className.length() == 0)
        {
            return null;
        }

        for (Class<?> aClass : classes)
        {
            if (className.equals(aClass.getName()))
            {
                return aClass;
            }
        }

        return null;
    }

    public static List<Class<?>> findClassList(String className)
    {
        return findClassList(getAllLoadedClasses(), className);
    }

    public static List<Class<?>> findClassList(Class<?>[] classes, String className)
    {
        List<Class<?>> list = new CopyOnWriteArrayList<Class<?>>();

        if (classes != null && className != null && className.length() > 0)
        {
            for (Class<?> aClass : classes)
            {
                if (className.equals(aClass.getName()))
                {
                    list.add(aClass);
                }
            }
        }

        return list;
    }

    public static Class<?>[] getAllLoadedClasses()
    {
        if (getInstrumentation() != null)
        {
            return getInstrumentation().getAllLoadedClasses();
        }

        return new Class[0];
    }

    public static Class<?>[] getSystemClassLoaderInitiatedClasses()
    {
        return getClassLoaderInitiatedClasses(ClassLoader.getSystemClassLoader());
    }

    public static Class<?>[] getClassLoaderInitiatedClasses(final ClassLoader classLoader)
    {
        return getInstrumentation().getInitiatedClasses(classLoader);
    }

    public static boolean redefineClasses(ClassDefinition[] classDefinitions)
    {
        try
        {
            if (isRedefineClassesSupported())
            {
                getInstrumentation().redefineClasses(classDefinitions);

                return true;
            }
        }
        catch (Throwable throwable)
        {
            if (classDefinitions != null)
            {
                for (ClassDefinition classDefinition : classDefinitions)
                {
                    logger.info(classDefinition.getDefinitionClass().toString());
                }
            }

            logger.log(Level.INFO, throwable.getMessage(), throwable);
        }

        return false;
    }

    public static boolean redefineClasses(Class<?> clazz, byte[] buff)
    {
        return isRedefineClassesSupported() && redefineClasses(new ClassDefinition[] { new ClassDefinition(clazz, buff) });
    }

    public static boolean isRedefineClassesSupported()
    {
        return getInstrumentation() != null && getInstrumentation().isRedefineClassesSupported();
    }

    private static void failInitialize()
    {
        logger.log(Level.INFO, "***********************************************************");
        logger.log(Level.INFO, "* " + Product.NAME + " Agent Initialize failure");
        logger.log(Level.INFO, "***********************************************************");
    }
}
