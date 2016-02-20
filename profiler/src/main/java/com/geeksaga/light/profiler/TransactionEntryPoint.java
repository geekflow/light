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
package com.geeksaga.light.profiler;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @author geeksaga
 */
public class TransactionEntryPoint implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        int flags = ClassWriter.COMPUTE_MAXS;

//        if (clazz.version > Opcodes.V1_5)
//        {
        flags |= ClassWriter.COMPUTE_FRAMES;
//        }

        ClassWriter classWriter = new ClassWriter(flags);


        return classfileBuffer;
    }
}

class TransactionEntryPointClassVistor extends ClassVisitor implements Opcodes {

    public TransactionEntryPointClassVistor(ClassVisitor classVisitor) {
        super(ASM5, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        System.out.println("access = [" + access + "], name = [" + name + "], desc = [" + desc + "], signature = [" + signature + "], exceptions = [" + exceptions + "]");

        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);

        return new TransactionEntryPointMethodVisitor(access, desc, methodVisitor);
    }
}

class TransactionEntryPointMethodVisitor extends LocalVariablesSorter implements Opcodes {
    public TransactionEntryPointMethodVisitor(int access, String desc, MethodVisitor methodVisitor) {
        super(ASM5, access, desc, methodVisitor);
    }
}
