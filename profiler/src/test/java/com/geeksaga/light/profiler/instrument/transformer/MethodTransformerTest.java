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
import com.geeksaga.light.profiler.asm.ClassNodeWrapper;
import com.geeksaga.light.profiler.config.ProfilerConfig;
import com.geeksaga.light.profiler.TestUtil;
import com.geeksaga.light.profiler.util.ASMUtil;
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
public class MethodTransformerTest
{
    @Test
    public void testTransform() throws Exception
    {
        String className = TestMethods.class.getName();

        byte[] original = TestUtil.load(className);
        ClassNodeWrapper classNodeWrapper = ASMUtil.parse(original);

        LightClassFileTransformer transformer = new MethodTransformer(new DefaultTraceRegisterBinder(), new AgentTraceContext(ProfilerConfig.load(getClass().getClassLoader(), "light.conf")));
        transformer.transform(getClass().getClassLoader(), null, original, classNodeWrapper);

        byte[] transform = ASMUtil.toBytes(classNodeWrapper);

        assertThat(original, not(transform));

        TestClassLoader classLoader = new TestClassLoader(getClass().getClassLoader());
        Class<?> clazz = classLoader.findClass(className, transform);

        assertThat(clazz, notNullValue());
        assertThat(clazz.getName(), is(className));

        Method method = clazz.getMethod("doWithObject", String.class);

        assertThat((String) method.invoke(clazz.newInstance(), "s"), is("s"));
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
