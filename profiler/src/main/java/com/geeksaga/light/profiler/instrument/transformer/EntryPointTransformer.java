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
import com.geeksaga.light.agent.trace.MethodInfo;
import com.geeksaga.light.agent.trace.Parameter;
import com.geeksaga.light.profiler.asm.ClassNodeWrapper;
import com.geeksaga.light.profiler.asm.ClassReaderWrapper;
import com.geeksaga.light.profiler.filter.Filter;
import com.geeksaga.light.profiler.filter.LightFilter;
import com.geeksaga.light.profiler.util.ASMUtil;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

import static com.geeksaga.light.profiler.util.ASMUtil.getInternalName;

/**
 * @author geeksaga
 */
public class EntryPointTransformer implements ClassFileTransformer {
    private static final Logger logger = Logger.getLogger(EntryPointTransformer.class.getName());

    private Filter filter = new LightFilter();

    @Override
    public byte[] transform(ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            if (filter.allow(classLoader, className)) {
                logger.info("Transform => " + className);

                ClassNodeWrapper classNodeWrapper = new ClassNodeWrapper();
                ClassReader reader = new ClassReaderWrapper(classfileBuffer);
                reader.accept(new ClassVisitor(Opcodes.ASM5, classNodeWrapper) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
                        if (name.equals("doWithObject"))
                            return new EntryPointAdapter(access, name, desc, mv, ASMUtil.isStatic(access));

                        return mv;
                    }
                }, ClassReader.EXPAND_FRAMES);

                if (classNodeWrapper.isInterface()) {
                    return classfileBuffer;
                }

                // return ASMUtil.toBytes(classNodeWrapper);
                byte[] bytes = ASMUtil.toBytes(classNodeWrapper);

                new MethodParameterTransformer().save(System.getProperty("user.dir") + File.separator + "Main.class", bytes);

                return bytes;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return classfileBuffer;
    }

    class EntryPointAdapter extends AdviceAdapter {
        public final String ARGUMENT_CLASS_INTERNAL_NAME = getInternalName(Parameter.class.getName());

        private String name;

        private String desc;
        private boolean isStatic = false;
        private int[] parameterIndices;
        private Type returnType;

        private Label startFinally = new Label();

        public EntryPointAdapter(int access, String name, String desc, MethodVisitor methodVisitor, boolean isStatic) {
            super(Opcodes.ASM5, methodVisitor, access, name, desc);

            this.name = name;
            this.desc = desc;
            this.returnType = Type.getReturnType(desc);
            this.isStatic = isStatic;
            this.parameterIndices = ASMUtil.getFixedArgumentIndices(desc, isStatic);
        }

        @Override
        public void visitCode() {
            super.visitCode();
//            mv.visitLabel(startFinally);
        }

        @Override
        protected void onMethodEnter() {
            System.out.println("onMethodEnter");
            mv.visitLabel(startFinally);

            int parameterVariableIndex = newLocal(Type.getType(ARGUMENT_CLASS_INTERNAL_NAME));
            Type[] argumentTypes = Type.getArgumentTypes(desc);

            mv.visitTypeInsn(NEW, ARGUMENT_CLASS_INTERNAL_NAME);
            mv.visitInsn(DUP);
            mv.visitIntInsn(BIPUSH, isStatic ? argumentTypes.length : argumentTypes.length + 1); // separate type
            mv.visitMethodInsn(INVOKESPECIAL, ARGUMENT_CLASS_INTERNAL_NAME, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, Type.INT_TYPE), false);
            mv.visitVarInsn(ASTORE, parameterVariableIndex);

            int parameterIndex = 0;
            if (!isStatic) {
                mv.visitVarInsn(ALOAD, parameterVariableIndex);
                mv.visitInsn(DUP);
                mv.visitIntInsn(BIPUSH, parameterIndex);
                mv.visitVarInsn(ALOAD, parameterIndices[parameterIndex]);
                mv.visitMethodInsn(INVOKEVIRTUAL, ARGUMENT_CLASS_INTERNAL_NAME, "set", "(ILjava/lang/Object;)V", false);

                parameterIndex++;
            }

            for (int i = 0, j = isStatic ? 0 : 1; i < argumentTypes.length; i++, j++) {
                Type type = argumentTypes[i];

                switch (type.getSort()) {
                    case Type.BOOLEAN:
                    case Type.CHAR:
                    case Type.BYTE:
                    case Type.SHORT:
                    case Type.INT:
                        visitInstruction(ILOAD, j, parameterVariableIndex, parameterIndex);

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
                    case Type.FLOAT:
                        visitInstruction(FLOAD, j, parameterVariableIndex, parameterIndex);

                        mv.visitMethodInsn(INVOKEVIRTUAL, ARGUMENT_CLASS_INTERNAL_NAME, "set", "(IF)V", false);

                        break;
                    case Type.LONG:
                        visitInstruction(LLOAD, j, parameterVariableIndex, parameterIndex);

                        mv.visitMethodInsn(INVOKEVIRTUAL, ARGUMENT_CLASS_INTERNAL_NAME, "set", "(IJ)V", false);

                        break;
                    case Type.DOUBLE:
                        visitInstruction(DLOAD, j, parameterVariableIndex, parameterIndex);

                        mv.visitMethodInsn(INVOKEVIRTUAL, ARGUMENT_CLASS_INTERNAL_NAME, "set", "(ID)V", false);

                        break;
                    case Type.ARRAY:
                    case Type.OBJECT:
                        visitInstruction(ALOAD, j, parameterVariableIndex, parameterIndex);

                        mv.visitMethodInsn(INVOKEVIRTUAL, ARGUMENT_CLASS_INTERNAL_NAME, "set", "(ILjava/lang/Object;)V", false);

                        break;
                    default:
                        throw new IllegalAccessError("Unknown type. " + type);
                }

                parameterIndex++;
            }

            mv.visitVarInsn(ALOAD, parameterVariableIndex);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, getInternalName(DebugTrace.class.getName()), "traceParameter", "(L" + getInternalName(Parameter.class.getName()) + ";)V", false);
            mv.visitCode();
        }

        private void visitInstruction(int opcode, int index, int parameterVariableIndex, int parameterIndex) {
            mv.visitVarInsn(ALOAD, parameterVariableIndex);
            mv.visitLdcInsn(index);
            mv.visitVarInsn(opcode, parameterIndices[parameterIndex]);
        }

        @Override
        public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
            System.out.println(name + "call? visitTryCatchBlock");
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            Label endFinally = new Label();

            mv.visitTryCatchBlock(startFinally, endFinally, endFinally, null);
            mv.visitLabel(endFinally);

//            onFinally(ATHROW);

            mv.visitInsn(DUP);
            int errIdx = newLocal(Type.getType(Throwable.class));
            mv.visitVarInsn(Opcodes.ASTORE, errIdx);
            mv.visitVarInsn(Opcodes.ALOAD, errIdx);
            mv.visitInsn(Opcodes.ACONST_NULL);
            mv.visitVarInsn(Opcodes.ALOAD, errIdx);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, getInternalName(DebugTrace.class.getName()), "end", "(L" + getInternalName(MethodInfo.class.getName()) + ";L" + getInternalName(Throwable.class.getName()) + ";)V", false);

            mv.visitInsn(ATHROW);
            mv.visitMaxs(maxStack + 3, maxLocals + 3);
        }

        @Override
        protected void onMethodExit(int opcode) {
            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
                captureReturn();
            }

            if (opcode != ATHROW) {
                // FIXME finally
//                onFinally(opcode);
            }
        }

        private void onFinally(int opcode) {
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("Enter " + name);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }

        public void captureReturn() {
//            int methodInfoIndex = newLocal(Type.getType(MethodInfo.class));

//            mv.visitVarInsn(ALOAD, 0);
//            mv.visitTypeInsn(NEW, getInternalName(MethodInfo.class.getName()));
//            mv.visitInsn(DUP);
//            mv.visitLdcInsn(name);
//            mv.visitLdcInsn(desc);

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

                        break;
                    }
                    default: // don't be run
                        break;
                }

                mv.visitTypeInsn(NEW, getInternalName(MethodInfo.class.getName()));
                mv.visitInsn(DUP);
                mv.visitLdcInsn(name);
                mv.visitLdcInsn(desc);
                mv.visitVarInsn(Opcodes.ALOAD, returnVariableIndex);

                mv.visitMethodInsn(INVOKESPECIAL, getInternalName(MethodInfo.class.getName()), "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V", false);
                mv.visitInsn(Opcodes.ACONST_NULL);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, getInternalName(DebugTrace.class.getName()), "end", "(L" + getInternalName(MethodInfo.class.getName()) + ";L" + getInternalName(Throwable.class.getName()) + ";)V", false);
            }
        }
    }
}
