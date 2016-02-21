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
package com.geeksaga.light.profiler.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

/**
 * @author geeksaga
 */
public class ClassWriterWrapper extends ClassWriter {
    private final TypeHierarchyUtil typeHierarchyUtil = new TypeHierarchyUtil();

    public ClassWriterWrapper(int flags) {
        super(flags);
    }

    public ClassWriterWrapper(ClassReader classReader, int flags) {
        super(classReader, flags);
    }

    @Override
    protected String getCommonSuperClass(final String type1, final String type2) {
        return commonSuperClass(type1, type2);
    }

    private String commonSuperClass(final String type1, final String type2) {
        Class<?> c = null, d = null;

        try {
//            c = findClass(type1.replace('/', '.'));
//            d = findClass(type2.replace('/', '.'));

            if (c == null || d == null) {
                return typeHierarchyUtil.getCommonSuperClass(type1, type2);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if ((c != null && d != null) && c.isAssignableFrom(d)) {
            return type1;
        }

        if ((d != null && c != null) && d.isAssignableFrom(c)) {
            return type2;
        }

        if (c == null || d == null || c.isInterface() || d.isInterface()) {
            return "java/lang/Object";
        } else {
            do {
                c = c.getSuperclass();
            }
            while (!c.isAssignableFrom(d));

            return c.getName().replace('.', '/');
        }
    }
}