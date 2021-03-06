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
import org.junit.Test;
import target.TestMethods;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author geeksaga
 */
public class MethodParameterTransformerTest
{
    @Test
    public void testTransform() throws Exception
    {
        String className = TestMethods.class.getName();

        ClassFileTransformer transformer = new MethodParameterTransformer(new DefaultTraceRegisterBinder(), new AgentTraceContext(ProfilerConfig.load(getClass().getClassLoader(), "light.conf")));

        byte[] original = TestUtil.load(className);
        byte[] transform = transformer.transform(getClass().getClassLoader(), className, null, null, original);

        assertThat(original, not(transform));

        TestClassLoader classLoader = new TestClassLoader(getClass().getClassLoader());
        Class<?> clazz = classLoader.findClass(className, transform);

        assertThat(clazz, notNullValue());
        assertThat(clazz.getName(), is(className));

        Method method = clazz.getMethod("doWithBoolean", boolean.class);

        Object instance = clazz.newInstance();

        assertThat((Boolean) method.invoke(instance, true), is(true));
        assertThat((Boolean) method.invoke(instance, false), is(false));

        method = clazz.getMethod("doWithInt", int.class);
        assertThat((Integer) method.invoke(instance, 13), is(13));
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
