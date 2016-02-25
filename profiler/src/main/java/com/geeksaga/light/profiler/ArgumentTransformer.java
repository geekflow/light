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

import com.geeksaga.light.profiler.asm.ClassNodeWrapper;
import com.geeksaga.light.profiler.asm.ClassReaderWrapper;
import com.geeksaga.light.profiler.trace.Argument;
import com.geeksaga.light.profiler.trace.DebugTrace;
import com.geeksaga.light.profiler.util.ASMUtil;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.tree.InsnNode;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

import static com.geeksaga.light.profiler.util.ASMUtil.getInternalName;
import static org.objectweb.asm.Opcodes.*;

/**
 * @author geeksaga
 */
public class ArgumentTransformer implements ClassFileTransformer {
    private static final Logger logger = Logger.getLogger(MethodTransformer.class.getName());
    public static final boolean WINDOWS_OS = getSystemProperty("os.name", "unix").contains("Window");

    private static String getSystemProperty(String key, String def) {
        try {
            return System.getProperty(key, def);
        } catch (RuntimeException exception) {
            return def;
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (!className.startsWith("java") && !className.startsWith("sun") && !className.contains("profiler")) {
            logger.info("Transform => " + className);

            ClassNodeWrapper classNodeWrapper = new ClassNodeWrapper();
            ClassReader reader = new ClassReaderWrapper(classfileBuffer);
            reader.accept(new ClassVisitor(Opcodes.ASM5, classNodeWrapper) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);

                    if (name.contains("<")) {
                        return methodVisitor;
                    }

                    return new ParameterVisitor(access, desc, methodVisitor, ASMUtil.isStatic(access));
                }
            }, ClassReader.EXPAND_FRAMES);

            if (classNodeWrapper.isInterface()) {
                return classfileBuffer;
            }

            byte[] bytes = ASMUtil.toBytes(classNodeWrapper);

            save("D:" + File.separator + "Main.class", bytes);

            return bytes;
        }

        return classfileBuffer;
    }

    public void save(String name, byte[] buff) {
        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(replaceWindowsSeparator(name))));
            out.write(buff);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String replaceWindowsSeparator(String path) {
        if (WINDOWS_OS) {
            if (path != null) {
                return path.replace("\\", "\\\\");
            }
        }

        return path;
    }
}

class ParameterVisitor extends LocalVariablesSorter {
    public final static String ARGUMENT_CLASS_NAME = "com.geeksaga.light.profiler.trace.Argument";
    public final static String ARGUMENT_CLASS_INTERNAL_NAME = getInternalName(ARGUMENT_CLASS_NAME);

    private String desc;
    private boolean isStatic = false;
    private int[] argumentIndices;

    public ParameterVisitor(int access, String desc, MethodVisitor methodVisitor, boolean isStatic) {
        super(Opcodes.ASM5, access, desc, methodVisitor);

        this.desc = desc;
        this.isStatic = isStatic;
        this.argumentIndices = ASMUtil.getFixedArgumentIndices(desc, isStatic);
    }

    @Override
    public void visitCode() {
        System.out.println(ARGUMENT_CLASS_INTERNAL_NAME);

        int argumentClassIndex = newLocal(Type.getType(ARGUMENT_CLASS_INTERNAL_NAME));
        Type[] argumentTypes = Type.getArgumentTypes(desc);

        mv.visitTypeInsn(NEW, ARGUMENT_CLASS_INTERNAL_NAME);
        mv.visitInsn(DUP);
        mv.visitIntInsn(BIPUSH, isStatic ? argumentTypes.length : argumentTypes.length + 1); // separate type
        mv.visitMethodInsn(INVOKESPECIAL, ARGUMENT_CLASS_INTERNAL_NAME, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, Type.INT_TYPE), false);
        mv.visitVarInsn(ASTORE, argumentClassIndex);

        int argumentIndex = 0;
        if (!isStatic) {
            mv.visitVarInsn(ALOAD, argumentClassIndex);
            mv.visitInsn(DUP);
            mv.visitIntInsn(BIPUSH, argumentIndex);
            mv.visitVarInsn(ALOAD, argumentIndices[argumentIndex]);
            mv.visitMethodInsn(INVOKEVIRTUAL, ARGUMENT_CLASS_INTERNAL_NAME, "set", "(ILjava/lang/Object;)V", false);

            argumentIndex++;
        }

        System.out.println("visitCode => " + desc + ", type length =" + argumentTypes.length + ", argumentIndex =" + argumentIndex);

        for (int i = 0, j = isStatic ? 0 : 1; i < argumentTypes.length; i++, j++) {
            Type type = argumentTypes[i];

            switch (type.getSort()) {
                case Type.BOOLEAN:
                case Type.CHAR:
                case Type.BYTE:
                case Type.SHORT:
                case Type.INT:
                    mv.visitVarInsn(ALOAD, argumentClassIndex);
                    mv.visitLdcInsn(j);
                    mv.visitVarInsn(ILOAD, argumentIndices[argumentIndex]);

                    String description = null;
                    switch (type.getSort()) {
                        case Type.BOOLEAN:
                            description = "(IZ)V";
                            break;
                        case Type.CHAR:
                            description = "(IC)V";
                            break;
                        case Type.BYTE:
                            description = "(IB)V";
                            break;
                        case Type.SHORT:
                            description = "(IS)V";
                            break;
                        case Type.INT:
                            description = "(II)V";
                            break;
                    }

                    mv.visitMethodInsn(INVOKEVIRTUAL, ARGUMENT_CLASS_INTERNAL_NAME, "set", description, false);

                    break;
                case Type.ARRAY:
                case Type.OBJECT:
                    mv.visitVarInsn(ALOAD, argumentClassIndex);
                    mv.visitLdcInsn(j);
                    mv.visitVarInsn(ALOAD, argumentIndices[argumentIndex]);

                    mv.visitMethodInsn(INVOKEVIRTUAL, ARGUMENT_CLASS_INTERNAL_NAME, "set", "(ILjava/lang/Object;)V", false);
            }

            argumentIndex++;
        }

        mv.visitVarInsn(ALOAD, argumentClassIndex);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, getInternalName(DebugTrace.class.getName()), "captureArgument", "(L" + getInternalName(Argument.class.getName()) + ";)V", false);
        mv.visitCode();
    }

    public void visitMaxs(int maxStack, int maxLocals) {
        mv.visitMaxs(maxStack, maxLocals);
    }
}