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

import com.geeksaga.light.profiler.TestClass;
import com.geeksaga.light.profiler.TestUtil;
import target.TestMethods;
import org.junit.Test;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author geeksaga
 */
public class MethodTransformerTest {
    @Test
    public void testTransform() throws Exception {
        String className = TestClass.CLASS_NAME;
        String classFileName = TestClass.CLASS_FILE_NAME;

        ClassFileTransformer transformer = new MethodTransformer();

        byte[] original = TestUtil.load(classFileName);
        byte[] transform = transformer.transform(getClass().getClassLoader(), className, null, null, original);

        assertThat(original, not(transform));

        TestClassLoader classLoader = new TestClassLoader(getClass().getClassLoader());
        Class<?> clazz = classLoader.findClass(className, transform);

        assertThat(clazz, notNullValue());
        assertThat(clazz.getName(), is(className));

        Method method = clazz.getMethod("doWithObject", String.class);

        assertThat((String) method.invoke(clazz.newInstance(), "s"), is("s"));
    }

    protected class TestClassLoader extends ClassLoader {
        public TestClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> findClass(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
}
