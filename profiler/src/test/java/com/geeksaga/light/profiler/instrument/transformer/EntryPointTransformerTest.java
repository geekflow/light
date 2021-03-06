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
package com.geeksaga.light.profiler.instrument.transformer;

import com.geeksaga.light.agent.core.AgentTraceContext;
import com.geeksaga.light.agent.core.DefaultTraceRegisterBinder;
import com.geeksaga.light.profiler.config.ProfilerConfig;
import com.geeksaga.light.profiler.TestUtil;
import com.geeksaga.light.profiler.asm.ClassNodeWrapper;
import com.geeksaga.light.profiler.util.ASMUtil;
import com.geeksaga.light.util.SystemProperty;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import target.TestMethods;

import java.io.File;
import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author geeksaga
 */
public class EntryPointTransformerTest
{
    private static final String LOGFILE_PATH = "/src/test/resources/log4j2.xml";

    @BeforeClass
    public static void init()
    {
        System.setProperty(XmlConfigurationFactory.CONFIGURATION_FILE_PROPERTY, System.getProperty("user.dir") + replaceWindowsSeparator(LOGFILE_PATH));
    }

    private static String replaceWindowsSeparator(String path)
    {
        if (SystemProperty.WINDOWS_OS && path != null)
        {
            return path.replace("\\", File.separator);
        }

        return path;
    }

    @Test
    public void testTransform() throws Exception
    {
        String className = TestMethods.class.getName();

//        ClassFileTransformer transformer = new EntryPointTransformer(new DefaultTraceRegisterBinder(), new AgentTraceContext(ProfilerConfig.load(getClass().getClassLoader(), "light.conf")));
        LightClassFileTransformer transformer = new EntryPointTransformer(new DefaultTraceRegisterBinder(), new AgentTraceContext(ProfilerConfig.load(getClass().getClassLoader(), "light.conf")));

        byte[] original = TestUtil.load(className);
//        byte[] transform = transformer.transform(getClass().getClassLoader(), className, null, null, original);
//        byte[] transform = transformer.transform(getClass().getClassLoader(), null, original, ASMUtil.parse(original));

        ClassNodeWrapper classNodeWrapper = ASMUtil.parse(original);

        transformer.transform(getClass().getClassLoader(), null, original, classNodeWrapper);

        byte[] transform = ASMUtil.toBytes(classNodeWrapper);

        assertThat(original, not(transform));

        TestClassLoader classLoader = new TestClassLoader(getClass().getClassLoader());
        Class<?> clazz = classLoader.findClass(className, transform);

        assertThat(clazz, notNullValue());
        assertThat(clazz.getName(), is(className));

        Object instance = clazz.newInstance();

        Method method = clazz.getDeclaredMethod("doWithObject", String.class);

        assertThat((String) method.invoke(instance, "A"), is("A"));
        assertThat((String) method.invoke(instance, "B"), is("B"));

        method = clazz.getDeclaredMethod("doWithNothing");
        assertThat(method.invoke(instance), nullValue());
    }

    private class TestClassLoader extends ClassLoader
    {
        TestClassLoader(ClassLoader parent)
        {
            super(parent);
        }

        Class<?> findClass(String name, byte[] bytes)
        {
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
}
