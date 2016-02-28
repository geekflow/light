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

import com.geeksaga.light.agent.trace.DebugTrace;
import com.geeksaga.light.agent.trace.Parameter;
import com.geeksaga.light.profiler.asm.ClassNodeWrapper;
import com.geeksaga.light.profiler.asm.ClassReaderWrapper;
import com.geeksaga.light.profiler.util.ASMUtil;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

import static com.geeksaga.light.profiler.util.ASMUtil.getInternalName;
import static org.objectweb.asm.Opcodes.ALOAD;

/**
 * @author geeksaga
 */
public class MethodReturnTransformer implements ClassFileTransformer {
    private static final Logger logger = Logger.getLogger(MethodTransformer.class.getName());

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (!className.startsWith("java") && !className.startsWith("sun") && !className.contains("profiler")) {
            logger.info("Transform => " + className);

            try {
                ClassNodeWrapper classNodeWrapper = new ClassNodeWrapper();
                ClassReader reader = new ClassReaderWrapper(classfileBuffer);
                reader.accept(new ClassVisitor(Opcodes.ASM5, classNodeWrapper) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);

                        if (name.contains("<")) {
                            return methodVisitor;
                        }

                        return new MethodReturnVisitor(access, desc, methodVisitor, ASMUtil.isStatic(access));
                    }
                }, ClassReader.EXPAND_FRAMES);

                if (classNodeWrapper.isInterface()) {
                    return classfileBuffer;
                }

                byte[] bytes = ASMUtil.toBytes(classNodeWrapper);

                new MethodParameterTransformer().save(System.getProperty("user.dir") + File.separator + "Main.class", bytes);

                return bytes;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return classfileBuffer;
    }
}

class MethodReturnVisitor extends LocalVariablesSorter {
    public final static String ARGUMENT_CLASS_INTERNAL_NAME = getInternalName(Parameter.class.getName());

    private Type returnType;
    private boolean isStatic = false;

    public MethodReturnVisitor(int access, String desc, MethodVisitor methodVisitor, boolean isStatic) {
        super(Opcodes.ASM5, access, desc, methodVisitor);

        this.returnType = Type.getReturnType(desc);
        this.isStatic = isStatic;
    }

    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
            captureReturn();
        }
        mv.visitInsn(opcode);
    }

    public void captureReturn() {
        System.out.println("call?? " + returnType);

        if(returnType == null || returnType.equals(Type.VOID_TYPE))
        {
            mv.visitInsn(Opcodes.ACONST_NULL);
        }
        else {

            int returnVariableIndex = newLocal(returnType);

            switch (returnType.getSort()) {
                case Type.BOOLEAN: {
                    mv.visitVarInsn(Opcodes.ISTORE, returnVariableIndex);
                    mv.visitVarInsn(Opcodes.ILOAD, returnVariableIndex);
                    mv.visitVarInsn(Opcodes.ILOAD, returnVariableIndex);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);

                    break;
                }
                case Type.LONG: {
                    mv.visitVarInsn(Opcodes.LSTORE, returnVariableIndex);
                    mv.visitVarInsn(Opcodes.LLOAD, returnVariableIndex);
                    mv.visitVarInsn(Opcodes.LLOAD, returnVariableIndex);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);

                    break;
                }
                default: // for void
                    mv.visitInsn(Opcodes.ACONST_NULL);

                    break;
            }

//        mv.visitVarInsn(ALOAD, returnVariableIndex);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, getInternalName(DebugTrace.class.getName()), "traceReturn", "(L" + getInternalName(Object.class.getName()) + ";)V", false);
        }
    }
}
