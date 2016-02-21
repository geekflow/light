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
package com.geeksaga.light.profiler.util;

import com.geeksaga.light.profiler.asm.ClassReaderWrapper;
import com.geeksaga.light.profiler.asm.ClassWrapper;
import com.geeksaga.light.profiler.asm.ClassWriterWrapper;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.JSRInlinerAdapter;

/**
 * @author geeksaga
 */
public class ASMUtil {
    private ASMUtil() {
    }

    private static ClassVisitor useJSRInlinerAdapter(ClassVisitor visitor) {
        return new ClassVisitor(Opcodes.ASM5, visitor) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                return new JSRInlinerAdapter(super.visitMethod(access, name, desc, signature, exceptions), access, name, desc, signature, exceptions);
            }
        };
    }

    public static ClassWrapper parse(byte[] classfileBuffer) {
        if (classfileBuffer == null) {
            return null;
        }

        return parse(classfileBuffer, 0);
    }

    public static ClassWrapper parse(byte[] classfileBuffer, int flags) {
        ClassWrapper classWrapper = new ClassWrapper();
        ClassReader reader = new ClassReaderWrapper(classfileBuffer);

        reader.accept(useJSRInlinerAdapter(classWrapper), new Attribute[0], flags);

        return classWrapper;
    }

    public static ClassWrapper parse(Object obj) {
        ClassWrapper classWrapper = new ClassWrapper();

        ClassReader reader = new ClassReaderWrapper(obj.getClass().getName());

        reader.accept(useJSRInlinerAdapter(classWrapper), new Attribute[0], 0);

        return classWrapper;
    }

    public static ClassWrapper parse(Class clazz) {
        ClassWrapper classWrapper = new ClassWrapper();

        ClassReader reader = new ClassReaderWrapper(clazz);
        reader.accept(useJSRInlinerAdapter(classWrapper), new Attribute[0], 0);

        return classWrapper;
    }

    public static byte[] toBytes(Class clazz) {
        return toBytes(parse(clazz));
    }

    public static byte[] toBytes(Object obj) {
        return toBytes(parse(obj));
    }

    public static byte[] toBytes(ClassWrapper clazz) {
        int flags = ClassWriter.COMPUTE_MAXS;

        if (clazz.version > Opcodes.V1_5) {
            flags |= ClassWriter.COMPUTE_FRAMES;
        }

        ClassWriter classWriter = new ClassWriterWrapper(flags);

        clazz.accept(useJSRInlinerAdapter(classWriter));

        return classWriter.toByteArray();
    }

    public static String convertForAgent(String fromAsm) {
        if (fromAsm != null) {
            return fromAsm.replace('/', '.');
        }

        return null;
    }

    public static String getInternalName(String name) {
        if (name != null) {
            return name.replace('.', '/');
        }

        return null;
    }

}
