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

import com.geeksaga.light.agent.TraceContext;
import com.geeksaga.light.agent.core.TraceRegisterBinder;
import com.geeksaga.light.agent.trace.EntryTrace;
import com.geeksaga.light.agent.trace.MethodInfo;
import com.geeksaga.light.agent.trace.Parameter;
import com.geeksaga.light.agent.trace.Profiler;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.geeksaga.light.profiler.util.ASMUtil.getInternalName;

/**
 * @author geeksaga
 */
public class EntryPointTransformer implements ClassFileTransformer {
    private static final Logger logger = Logger.getLogger(EntryPointTransformer.class.getName());

    private Filter filter = new LightFilter();

    private TraceRegisterBinder traceRegisterBinder;
    private TraceContext traceContext;
    private int traceId;
    private String ownerClassName;
    private String begin;
    private String beginDescriptor;
    private String end;
    private String endDescriptor;

    public EntryPointTransformer(TraceRegisterBinder traceRegisterBinder, TraceContext traceContext) {
        this(traceRegisterBinder, traceContext, Profiler.INTERNAL_CLASS_NAME, Profiler.BEGIN, Profiler.BEGIN_DESCRIPTOR, Profiler.END, Profiler.END_DESCRIPTOR);
    }

    public EntryPointTransformer(TraceRegisterBinder traceRegisterBinder, TraceContext traceContext, String ownerClassName, String begin, String beginDescriptor, String end, String endDescriptor) {
        this.traceRegisterBinder = traceRegisterBinder;
        this.traceContext = traceContext;
        this.traceId = this.traceRegisterBinder.getTraceRegistryAdaptor().add(new EntryTrace(traceContext));
        this.ownerClassName = ownerClassName;
        this.begin = begin;
        this.beginDescriptor = beginDescriptor;
        this.end = end;
        this.endDescriptor = endDescriptor;
    }

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
                        if (name.equals("doWithObject") || name.equals("doWithNothing") || name.equals("main")) {
                            return new EntryPointAdapter(access, name, desc, mv, ASMUtil.isStatic(access));
                        }

                        return mv;
                    }
                }, ClassReader.EXPAND_FRAMES);

                if (classNodeWrapper.isInterface()) {
                    return classfileBuffer;
                }

                // return ASMUtil.toBytes(classNodeWrapper);
                byte[] bytes = ASMUtil.toBytes(classNodeWrapper);

                new MethodParameterTransformer().save(System.getProperty("user.dir") + File.separator + ".." + File.separator + "build" + File.separator + "Main.class", bytes);

                return bytes;
            }
        } catch (Throwable throwable) {
            logger.log(Level.WARNING, throwable.getMessage(), throwable);
        }

        return classfileBuffer;
    }

    class EntryPointAdapter extends AdviceAdapter {
        private final String PARAMETER_CLASS_INTERNAL_NAME = getInternalName(Parameter.class.getName());
        private final String METHOD_INFO_CLASS_INTERNAL_NAME = getInternalName(MethodInfo.class.getName());

        private String name;

        private String desc;
        private boolean isStatic = false;
        private int[] parameterIndices;
        private Type returnType;

        private Label startFinally = new Label();
        int methodInfoIndex;

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
            mv.visitLabel(startFinally);
        }

        @Override
        protected void onMethodEnter() {
            methodInfoIndex = newLocal(Type.getType(METHOD_INFO_CLASS_INTERNAL_NAME));
            int parameterVariableIndex = newLocal(Type.getType(PARAMETER_CLASS_INTERNAL_NAME));
            Type[] argumentTypes = Type.getArgumentTypes(desc);

            mv.visitTypeInsn(NEW, METHOD_INFO_CLASS_INTERNAL_NAME);
            mv.visitInsn(DUP);
            mv.visitLdcInsn(name);
            mv.visitLdcInsn(desc);
            mv.visitMethodInsn(INVOKESPECIAL, METHOD_INFO_CLASS_INTERNAL_NAME, "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", false);
            mv.visitVarInsn(ASTORE, methodInfoIndex);

            mv.visitTypeInsn(NEW, PARAMETER_CLASS_INTERNAL_NAME);
            mv.visitInsn(DUP);
            mv.visitIntInsn(BIPUSH, isStatic ? argumentTypes.length : argumentTypes.length + 1); // separate type
            mv.visitMethodInsn(INVOKESPECIAL, PARAMETER_CLASS_INTERNAL_NAME, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, Type.INT_TYPE), false);
            mv.visitVarInsn(ASTORE, parameterVariableIndex);

            int parameterIndex = 0;
            if (!isStatic) {
                mv.visitVarInsn(ALOAD, parameterVariableIndex);
                mv.visitInsn(DUP);
                mv.visitIntInsn(BIPUSH, parameterIndex);
                mv.visitVarInsn(ALOAD, parameterIndices[parameterIndex]);
                mv.visitMethodInsn(INVOKEVIRTUAL, PARAMETER_CLASS_INTERNAL_NAME, "set", "(ILjava/lang/Object;)V", false);

                parameterIndex++;
            }

            for (int i = 0, argumentIndex = isStatic ? 0 : 1; i < argumentTypes.length; i++, argumentIndex++) {
                visitInstruction(argumentTypes[i], argumentIndex, parameterVariableIndex, parameterIndex);

                parameterIndex++;
            }

            mv.visitVarInsn(ALOAD, methodInfoIndex);
            mv.visitVarInsn(ALOAD, parameterVariableIndex);
            mv.visitMethodInsn(INVOKEVIRTUAL, METHOD_INFO_CLASS_INTERNAL_NAME, "setParameter", "(L" + getInternalName(Parameter.class.getName()) + ";)V", false);

            mv.visitIntInsn(BIPUSH, traceId);
            mv.visitVarInsn(ALOAD, methodInfoIndex);
            mv.visitMethodInsn(INVOKESTATIC, ownerClassName, begin, beginDescriptor, false);
            mv.visitCode();
        }

        private void visitInstruction(Type type, int index, int parameterVariableIndex, int parameterIndex) {
            switch (type.getSort()) {
                case Type.BOOLEAN:
                    visitInstruction(ILOAD, index, parameterVariableIndex, parameterIndex);

                    mv.visitMethodInsn(INVOKEVIRTUAL, PARAMETER_CLASS_INTERNAL_NAME, "set", "(IZ)V", false);

                    break;
                case Type.CHAR:
                    visitInstruction(ILOAD, index, parameterVariableIndex, parameterIndex);

                    mv.visitMethodInsn(INVOKEVIRTUAL, PARAMETER_CLASS_INTERNAL_NAME, "set", "(IC)V", false);

                    break;
                case Type.BYTE:
                    visitInstruction(ILOAD, index, parameterVariableIndex, parameterIndex);

                    mv.visitMethodInsn(INVOKEVIRTUAL, PARAMETER_CLASS_INTERNAL_NAME, "set", "(IB)V", false);

                    break;
                case Type.SHORT:
                    visitInstruction(ILOAD, index, parameterVariableIndex, parameterIndex);

                    mv.visitMethodInsn(INVOKEVIRTUAL, PARAMETER_CLASS_INTERNAL_NAME, "set", "(IS)V", false);
                    break;
                case Type.INT:
                    visitInstruction(ILOAD, index, parameterVariableIndex, parameterIndex);

                    mv.visitMethodInsn(INVOKEVIRTUAL, PARAMETER_CLASS_INTERNAL_NAME, "set", "(II)V", false);

                    break;
                case Type.FLOAT:
                    visitInstruction(FLOAD, index, parameterVariableIndex, parameterIndex);

                    mv.visitMethodInsn(INVOKEVIRTUAL, PARAMETER_CLASS_INTERNAL_NAME, "set", "(IF)V", false);

                    break;
                case Type.LONG:
                    visitInstruction(LLOAD, index, parameterVariableIndex, parameterIndex);

                    mv.visitMethodInsn(INVOKEVIRTUAL, PARAMETER_CLASS_INTERNAL_NAME, "set", "(IJ)V", false);

                    break;
                case Type.DOUBLE:
                    visitInstruction(DLOAD, index, parameterVariableIndex, parameterIndex);

                    mv.visitMethodInsn(INVOKEVIRTUAL, PARAMETER_CLASS_INTERNAL_NAME, "set", "(ID)V", false);

                    break;
                case Type.ARRAY:
                case Type.OBJECT:
                    visitInstruction(ALOAD, index, parameterVariableIndex, parameterIndex);

                    mv.visitMethodInsn(INVOKEVIRTUAL, PARAMETER_CLASS_INTERNAL_NAME, "set", "(ILjava/lang/Object;)V", false);

                    break;
                default:
                    throw new IllegalAccessError("Unknown type. " + type);
            }
        }

        private void visitInstruction(int opcode, int index, int parameterVariableIndex, int parameterIndex) {
            mv.visitVarInsn(ALOAD, parameterVariableIndex);
            mv.visitLdcInsn(index);
            mv.visitVarInsn(opcode, parameterIndices[parameterIndex]);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            Label endFinally = new Label();
            int throwableIndex = newLocal(Type.getType(Throwable.class));

            mv.visitTryCatchBlock(startFinally, endFinally, endFinally, null);
            mv.visitLabel(endFinally);
            mv.visitInsn(DUP);
            mv.visitVarInsn(ASTORE, throwableIndex);
            mv.visitVarInsn(ALOAD, throwableIndex);

            mv.visitIntInsn(BIPUSH, traceId);
            mv.visitVarInsn(ALOAD, methodInfoIndex);
            mv.visitVarInsn(ALOAD, throwableIndex);
            mv.visitMethodInsn(INVOKESTATIC, ownerClassName, end, endDescriptor, false);
            mv.visitInsn(ATHROW);
            mv.visitMaxs(maxStack, maxLocals);
        }

        @Override
        protected void onMethodExit(int opcode) {
            if ((opcode >= IRETURN && opcode <= RETURN)) {
                captureReturn();
            }

            if (opcode != ATHROW) {
                // FIXME finally
            }
        }

        public void captureReturn() {
            if (returnType == null || returnType.equals(Type.VOID_TYPE)) {
//                mv.visitInsn(ACONST_NULL);
            } else {
                int returnVariableIndex = newLocal(returnType);

                switch (returnType.getSort()) {
                    case Type.BOOLEAN: {
                        mv.visitVarInsn(ISTORE, returnVariableIndex);
                        mv.visitVarInsn(ILOAD, returnVariableIndex);
                        mv.visitVarInsn(ILOAD, returnVariableIndex);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);

                        break;
                    }
                    case Type.CHAR: {
                        mv.visitVarInsn(ISTORE, returnVariableIndex);
                        mv.visitVarInsn(ILOAD, returnVariableIndex);
                        mv.visitVarInsn(ILOAD, returnVariableIndex);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);

                        break;
                    }
                    case Type.BYTE: {
                        mv.visitVarInsn(ISTORE, returnVariableIndex);
                        mv.visitVarInsn(ILOAD, returnVariableIndex);
                        mv.visitVarInsn(ILOAD, returnVariableIndex);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);

                        break;
                    }
                    case Type.SHORT: {
                        mv.visitVarInsn(ISTORE, returnVariableIndex);
                        mv.visitVarInsn(ILOAD, returnVariableIndex);
                        mv.visitVarInsn(ILOAD, returnVariableIndex);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);

                        break;
                    }
                    case Type.INT: {
                        mv.visitVarInsn(ISTORE, returnVariableIndex);
                        mv.visitVarInsn(ILOAD, returnVariableIndex);
                        mv.visitVarInsn(ILOAD, returnVariableIndex);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);

                        break;
                    }
                    case Type.FLOAT: {
                        mv.visitVarInsn(FSTORE, returnVariableIndex);
                        mv.visitVarInsn(FLOAD, returnVariableIndex);
                        mv.visitVarInsn(FLOAD, returnVariableIndex);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);

                        break;
                    }
                    case Type.LONG: {
                        mv.visitVarInsn(LSTORE, returnVariableIndex);
                        mv.visitVarInsn(LLOAD, returnVariableIndex);
                        mv.visitVarInsn(LLOAD, returnVariableIndex);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);

                        break;
                    }
                    case Type.DOUBLE: {
                        mv.visitVarInsn(DSTORE, returnVariableIndex);
                        mv.visitVarInsn(DLOAD, returnVariableIndex);
                        mv.visitVarInsn(DLOAD, returnVariableIndex);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);

                        break;
                    }
                    case Type.ARRAY:
                    case Type.OBJECT: {
                        mv.visitVarInsn(ASTORE, returnVariableIndex);
                        mv.visitVarInsn(ALOAD, returnVariableIndex);

                        break;
                    }
                    default: // don't be run
                        break;
                }

                mv.visitVarInsn(ALOAD, methodInfoIndex);
                mv.visitVarInsn(ALOAD, returnVariableIndex);
                mv.visitMethodInsn(INVOKEVIRTUAL, METHOD_INFO_CLASS_INTERNAL_NAME, "setReturnValue", "(Ljava/lang/Object;)V", false);
            }

            mv.visitIntInsn(BIPUSH, traceId);
            mv.visitVarInsn(ALOAD, methodInfoIndex);
            mv.visitInsn(ACONST_NULL);
            mv.visitMethodInsn(INVOKESTATIC, ownerClassName, end, endDescriptor, false);
        }
    }
}
