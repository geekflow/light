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

import com.geeksaga.light.profiler.asm.ClassReaderWrapper;
import com.geeksaga.light.profiler.asm.ClassWrapper;
import com.geeksaga.light.profiler.util.ASMUtil;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.JSRInlinerAdapter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

/**
 * @author geeksaga
 */
public class MethodTransformer implements ClassFileTransformer {
    private static final Logger logger = Logger.getLogger(MethodTransformer.class.getName());

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        logger.info("Transform => " + className);

//        ClassWrapper clazz = ASMUtil.parse(classfileBuffer);
//
//        if (clazz.isInterface()) {
//            return classfileBuffer;
//        }

        ClassWrapper classWrapper = new ClassWrapper();
        ClassReader reader = new ClassReaderWrapper(classfileBuffer);
        reader.accept(new ClassVisitor(Opcodes.ASM5, classWrapper) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
                return new MethodAdapter(access, name, desc, mv);
            }
        }, ClassReader.EXPAND_FRAMES);

        return ASMUtil.toBytes(classWrapper);

        // return classfileBuffer;
    }
}

class MethodAdapter extends AdviceAdapter {
    private String name;
    private int time;
    private Label timeStart = new Label();
    private Label timeEnd = new Label();

    public MethodAdapter(int access, String name, String desc, MethodVisitor methodVisitor) {
        super(Opcodes.ASM5, methodVisitor, access, name, desc);

        this.name = name;
    }

    @Override
    protected void onMethodEnter() {
        System.out.println("onMethodEnter " + name);

        mv.visitLabel(timeStart);
        int time = newLocal(Type.getType("J"));
        visitLocalVariable("time", "J", null, timeStart, timeEnd, time);

        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("Enter " + name);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    @Override
    public void visitMaxs(int stack, int locals) {
        visitLabel(timeEnd);

        super.visitMaxs(stack, locals);
    }

    @Override
    protected void onMethodExit(int opcode) {
        System.out.println("onMethodExit " + name);

        if (opcode != ATHROW) {
            mv.visitVarInsn(Opcodes.ALOAD, 42);
            // FIXME finally
        }
    }
}
