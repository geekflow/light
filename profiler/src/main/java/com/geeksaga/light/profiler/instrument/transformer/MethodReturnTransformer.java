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
import com.geeksaga.light.profiler.asm.ClassNodeWrapper;
import com.geeksaga.light.profiler.asm.ClassReaderWrapper;
import com.geeksaga.light.profiler.filter.Filter;
import com.geeksaga.light.profiler.filter.LightFilter;
import com.geeksaga.light.profiler.util.ASMUtil;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

import static com.geeksaga.light.profiler.util.ASMUtil.getInternalName;

/**
 * @author geeksaga
 */
public class MethodReturnTransformer implements ClassFileTransformer {
    private static final Logger logger = Logger.getLogger(MethodTransformer.class.getName());

    private Filter filter = new LightFilter();

    @Override
    public byte[] transform(ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (filter.allow(classLoader, className)) {
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

                return ASMUtil.toBytes(classNodeWrapper);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return classfileBuffer;
    }

    class MethodReturnVisitor extends LocalVariablesSorter {
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
            if (returnType == null || returnType.equals(Type.VOID_TYPE)) {
                mv.visitInsn(Opcodes.ACONST_NULL);
            } else {
                int returnVariableIndex = newLocal(returnType);

                switch (returnType.getSort()) {
                    case Type.BOOLEAN: {
                        mv.visitVarInsn(Opcodes.ISTORE, returnVariableIndex);
                        mv.visitVarInsn(Opcodes.ILOAD, returnVariableIndex);
                        mv.visitVarInsn(Opcodes.ILOAD, returnVariableIndex);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);

                        break;
                    }
                    case Type.CHAR: {
                        mv.visitVarInsn(Opcodes.ISTORE, returnVariableIndex);
                        mv.visitVarInsn(Opcodes.ILOAD, returnVariableIndex);
                        mv.visitVarInsn(Opcodes.ILOAD, returnVariableIndex);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);

                        break;
                    }
                    case Type.BYTE: {
                        mv.visitVarInsn(Opcodes.ISTORE, returnVariableIndex);
                        mv.visitVarInsn(Opcodes.ILOAD, returnVariableIndex);
                        mv.visitVarInsn(Opcodes.ILOAD, returnVariableIndex);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);

                        break;
                    }
                    case Type.SHORT: {
                        mv.visitVarInsn(Opcodes.ISTORE, returnVariableIndex);
                        mv.visitVarInsn(Opcodes.ILOAD, returnVariableIndex);
                        mv.visitVarInsn(Opcodes.ILOAD, returnVariableIndex);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);

                        break;
                    }
                    case Type.INT: {
                        mv.visitVarInsn(Opcodes.ISTORE, returnVariableIndex);
                        mv.visitVarInsn(Opcodes.ILOAD, returnVariableIndex);
                        mv.visitVarInsn(Opcodes.ILOAD, returnVariableIndex);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);

                        break;
                    }
                    case Type.FLOAT: {
                        mv.visitVarInsn(Opcodes.FSTORE, returnVariableIndex);
                        mv.visitVarInsn(Opcodes.FLOAD, returnVariableIndex);
                        mv.visitVarInsn(Opcodes.FLOAD, returnVariableIndex);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);

                        break;
                    }
                    case Type.LONG: {
                        mv.visitVarInsn(Opcodes.LSTORE, returnVariableIndex);
                        mv.visitVarInsn(Opcodes.LLOAD, returnVariableIndex);
                        mv.visitVarInsn(Opcodes.LLOAD, returnVariableIndex);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);

                        break;
                    }
                    case Type.DOUBLE: {
                        mv.visitVarInsn(Opcodes.DSTORE, returnVariableIndex);
                        mv.visitVarInsn(Opcodes.DLOAD, returnVariableIndex);
                        mv.visitVarInsn(Opcodes.DLOAD, returnVariableIndex);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);

                        break;
                    }
                    case Type.ARRAY:
                    case Type.OBJECT: {
                        mv.visitVarInsn(Opcodes.ASTORE, returnVariableIndex);
                        mv.visitVarInsn(Opcodes.ALOAD, returnVariableIndex);
                        mv.visitVarInsn(Opcodes.ALOAD, returnVariableIndex);

                        break;
                    }
                    default: // don't be run
                        break;
                }

                mv.visitMethodInsn(Opcodes.INVOKESTATIC, getInternalName(DebugTrace.class.getName()), "traceReturn", "(L" + getInternalName(Object.class.getName()) + ";)V", false);
            }
        }
    }
}
