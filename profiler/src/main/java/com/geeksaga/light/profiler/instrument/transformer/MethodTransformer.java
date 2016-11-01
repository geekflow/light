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
import com.geeksaga.light.agent.TraceRepository;
import com.geeksaga.light.agent.core.TraceRegisterBinder;
import com.geeksaga.light.agent.trace.MethodInfo;
import com.geeksaga.light.agent.trace.MethodTrace;
import com.geeksaga.light.agent.trace.Parameter;
import com.geeksaga.light.agent.trace.Profiler;
import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;
import com.geeksaga.light.profiler.asm.ClassNodeWrapper;
import com.geeksaga.light.profiler.asm.ClassReaderWrapper;
import com.geeksaga.light.profiler.filter.Filter;
import com.geeksaga.light.profiler.filter.LightFilter;
import com.geeksaga.light.profiler.selector.ClassSelector;
import com.geeksaga.light.profiler.selector.MethodSelector;
import com.geeksaga.light.profiler.util.ASMUtil;
import com.geeksaga.light.profiler.util.ClassFileDumper;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import static com.geeksaga.light.agent.config.ConfigDef.entry_point;
import static com.geeksaga.light.agent.config.ConfigDefaultValueDef.default_entry_point;
import static com.geeksaga.light.profiler.util.ASMUtil.getInternalName;
import static com.geeksaga.light.profiler.util.ASMUtil.isStatic;

/**
 * @author geeksaga
 */
public class MethodTransformer implements LightClassFileTransformer
{
    private LightLogger logger;
    private Filter filter;

    private TraceRegisterBinder traceRegisterBinder;
    private TraceContext traceContext;
    private TraceRepository traceRepository;

    private int traceId;

    private String ownerClassName;
    private String begin;
    private String beginDescriptor;
    private String end;
    private String endDescriptor;

    private ClassSelector classSelector;

    public MethodTransformer(TraceRegisterBinder traceRegisterBinder, TraceContext traceContext)
    {
        this.logger = CommonLogger.getLogger(this.getClass().getName());
        this.filter = new LightFilter(traceContext);

        this.traceRegisterBinder = traceRegisterBinder;
        this.traceContext = traceContext;

        this.traceId = createTrace();
    }

    public MethodTransformer(TraceRegisterBinder traceRegisterBinder, TraceContext traceContext, TraceRepository traceRepository)
    {
        this(traceRegisterBinder, traceContext, traceRepository, Profiler.INTERNAL_CLASS_NAME, Profiler.BEGIN, Profiler.BEGIN_DESCRIPTOR, Profiler.END, Profiler.END_DESCRIPTOR);
    }

    public MethodTransformer(TraceRegisterBinder traceRegisterBinder, TraceContext traceContext, TraceRepository traceRepository, String ownerClassName, String begin, String beginDescriptor, String end, String endDescriptor)
    {
        this.logger = CommonLogger.getLogger(this.getClass().getName());

        this.traceRegisterBinder = traceRegisterBinder;
        this.traceContext = traceContext;
        this.traceRepository = traceRepository;

        this.traceId = createTrace();

        this.ownerClassName = ownerClassName;
        this.begin = begin;
        this.beginDescriptor = beginDescriptor;
        this.end = end;
        this.endDescriptor = endDescriptor;

        this.classSelector = ClassSelector.create(traceContext.getConfig().read(entry_point, default_entry_point));

        this.filter = new LightFilter(traceContext);
    }

    private int createTrace()
    {
        return traceRegisterBinder.getTraceRegistryAdaptor().add(new MethodTrace(traceContext, traceRepository));
    }

    @Override
    public byte[] transform(ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
    {
        if (!filter.allow(classLoader, className))
        {
            return classfileBuffer;
        }

        ClassNodeWrapper classNodeWrapper = new ClassNodeWrapper();
        ClassReader reader = new ClassReaderWrapper(classfileBuffer);
        reader.accept(new ClassVisitor(Opcodes.ASM5, classNodeWrapper)
        {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
            {
                MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

                return new MethodAdapter(access, name, desc, mv, isStatic(access));
            }
        }, ClassReader.EXPAND_FRAMES);

        if (classNodeWrapper.isInterface())
        {
            return classfileBuffer;
        }

        return ASMUtil.toBytes(classNodeWrapper);
    }

    @Override
    public ClassNodeWrapper transform(ClassLoader classLoader, Class<?> classBeingRedefined, byte[] classfileBuffer, ClassNodeWrapper classNodeWrapper)
    {
        try
        {
            if (!filter.allow(classLoader, classNodeWrapper.getClassName()))
            {
                return classNodeWrapper;
            }

            return transform(classLoader, classfileBuffer, classNodeWrapper);
        }
        catch (Exception exception)
        {
            logger.info(exception);
        }

        return classNodeWrapper;
    }

    private ClassNodeWrapper transform(final ClassLoader classLoader, byte[] classfileBuffer, final ClassNodeWrapper classNodeWrapper)
    {
        final MethodSelector methodSelector = getMethodSelectorOrNull(classNodeWrapper.getClassName());
        if (methodSelector == null)
        {
            return classNodeWrapper;
        }

        ClassNodeWrapper newClassNodeWrapper = new ClassNodeWrapper();
        classNodeWrapper.accept(new ClassVisitor(Opcodes.ASM5, newClassNodeWrapper)
        {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
            {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                if (!name.startsWith("<") && methodSelector.isSelected(name, desc))
                {
                    logger.debug("Transform => {}.{}{}", classNodeWrapper.getClassName(), name, desc);

                    return new MethodTransformer.MethodAdapter(access, name, desc, mv, isStatic(access));
                }

                return mv;
            }
        });

        byte[] hookedClassFileBuffer = ASMUtil.toBytes(newClassNodeWrapper);

        ClassFileDumper.dump(classNodeWrapper.getClassName(), classfileBuffer, hookedClassFileBuffer);

        return newClassNodeWrapper;
    }

    private MethodSelector getMethodSelectorOrNull(String className)
    {
        if (classSelector != null && !classSelector.isEmpty())
        {
            return classSelector.selectByClass(className);
        }

        return null;
    }

    private class MethodAdapter extends AdviceAdapter
    {
        private final String PARAMETER_CLASS_INTERNAL_NAME = getInternalName(Parameter.class.getName());
        private final String METHOD_INFO_CLASS_INTERNAL_NAME = getInternalName(MethodInfo.class.getName());

        private String name;
        private String desc;
        private boolean isStatic = false;
        private int[] parameterIndices;
        private Type returnType;

        private Label startFinally = new Label();

        int methodInfoIndex;

        MethodAdapter(int access, String name, String desc, MethodVisitor methodVisitor, boolean isStatic)
        {
            super(Opcodes.ASM5, methodVisitor, access, name, desc);

            this.name = name;
            this.desc = desc;
            this.returnType = Type.getReturnType(desc);
            this.isStatic = isStatic;
            this.parameterIndices = ASMUtil.getFixedArgumentIndices(desc, isStatic);
        }

        @Override
        public void visitCode()
        {
            super.visitCode();
            mv.visitLabel(startFinally);
        }

        @Override
        protected void onMethodEnter()
        {
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
            if (!isStatic)
            {
                mv.visitVarInsn(ALOAD, parameterVariableIndex);
                mv.visitIntInsn(BIPUSH, parameterIndex);
                mv.visitVarInsn(ALOAD, parameterIndices[parameterIndex]);
                mv.visitMethodInsn(INVOKEVIRTUAL, PARAMETER_CLASS_INTERNAL_NAME, "set", "(ILjava/lang/Object;)V", false);

                parameterIndex++;
            }

            for (int i = 0, argumentIndex = isStatic ? 0 : 1; i < argumentTypes.length; i++, argumentIndex++)
            {
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

        private void visitInstruction(Type type, int index, int parameterVariableIndex, int parameterIndex)
        {
            switch (type.getSort())
            {
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

        private void visitInstruction(int opcode, int index, int parameterVariableIndex, int parameterIndex)
        {
            mv.visitVarInsn(ALOAD, parameterVariableIndex);
            mv.visitLdcInsn(index);
            mv.visitVarInsn(opcode, parameterIndices[parameterIndex]);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals)
        {
            Label endFinally = new Label();

            mv.visitTryCatchBlock(startFinally, endFinally, endFinally, null);
            mv.visitLabel(endFinally);
            mv.visitInsn(DUP);

            int throwableIndex = newLocal(Type.getType(Throwable.class));
            mv.visitVarInsn(ASTORE, throwableIndex);
            //            mv.visitVarInsn(ALOAD, returnObjectIndex);

            mv.visitIntInsn(BIPUSH, traceId);
            mv.visitVarInsn(ALOAD, methodInfoIndex);
            mv.visitVarInsn(ALOAD, throwableIndex);
            mv.visitMethodInsn(INVOKESTATIC, ownerClassName, end, endDescriptor, false);
            mv.visitInsn(ATHROW);
            mv.visitMaxs(maxStack, maxLocals);
        }

        @Override
        protected void onMethodExit(int opcode)
        {
            if (isReturn(opcode))
            {
                captureReturn();
            }

            if (opcode != ATHROW)
            {
                // FIXME finally
                // captureReturn();
            }
        }

        private boolean isReturn(int opcode)
        {
            return (opcode >= IRETURN && opcode <= RETURN);
        }

        /**
         * mv.visitVarInsn(LSTORE, returnTypeIndex);
         * mv.visitVarInsn(ALOAD, methodInfoIndex);
         * mv.visitVarInsn(LLOAD, returnTypeIndex);
         * mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
         * mv.visitMethodInsn(INVOKEVIRTUAL, METHOD_INFO_CLASS_INTERNAL_NAME, "setReturnValue", "(Ljava/lang/Object;)V", false);
         * mv.visitVarInsn(LLOAD, returnTypeIndex);
         * <p>
         * Capture return.
         */
        void captureReturn()
        {
            if (returnType != null && !returnType.equals(Type.VOID_TYPE))
            {
                int returnTypeIndex = newLocal(returnType);
                int returnObjectIndex = newLocal(Type.getType(Object.class));

                switch (returnType.getSort())
                {
                    case Type.BOOLEAN:
                    {
                        mv.visitVarInsn(ISTORE, returnTypeIndex);
                        mv.visitVarInsn(ILOAD, returnTypeIndex);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
                        mv.visitVarInsn(ASTORE, returnObjectIndex);
                        mv.visitVarInsn(ILOAD, returnTypeIndex);

                        break;
                    }
                    case Type.CHAR:
                    {
                        mv.visitVarInsn(ISTORE, returnTypeIndex);
                        mv.visitVarInsn(ILOAD, returnTypeIndex);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
                        mv.visitVarInsn(ASTORE, returnObjectIndex);
                        mv.visitVarInsn(ILOAD, returnTypeIndex);

                        break;
                    }
                    case Type.BYTE:
                    {
                        mv.visitVarInsn(ISTORE, returnTypeIndex);
                        mv.visitVarInsn(ILOAD, returnTypeIndex);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
                        mv.visitVarInsn(ASTORE, returnObjectIndex);
                        mv.visitVarInsn(ILOAD, returnTypeIndex);

                        break;
                    }
                    case Type.SHORT:
                    {
                        mv.visitVarInsn(ISTORE, returnTypeIndex);
                        mv.visitVarInsn(ILOAD, returnTypeIndex);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
                        mv.visitVarInsn(ASTORE, returnObjectIndex);
                        mv.visitVarInsn(ILOAD, returnTypeIndex);

                        break;
                    }
                    case Type.INT:
                    {
                        mv.visitVarInsn(ISTORE, returnTypeIndex);
                        mv.visitVarInsn(ILOAD, returnTypeIndex);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                        mv.visitVarInsn(ASTORE, returnObjectIndex);
                        mv.visitVarInsn(ILOAD, returnTypeIndex);

                        break;
                    }
                    case Type.FLOAT:
                    {
                        mv.visitVarInsn(FSTORE, returnTypeIndex);
                        mv.visitVarInsn(FLOAD, returnTypeIndex);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
                        mv.visitVarInsn(ASTORE, returnObjectIndex);
                        mv.visitVarInsn(FLOAD, returnTypeIndex);

                        break;
                    }
                    case Type.LONG:
                    {
                        mv.visitVarInsn(LSTORE, returnTypeIndex);
                        mv.visitVarInsn(LLOAD, returnTypeIndex);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                        mv.visitVarInsn(ASTORE, returnObjectIndex);
                        mv.visitVarInsn(LLOAD, returnTypeIndex);

                        break;
                    }
                    case Type.DOUBLE:
                    {
                        mv.visitVarInsn(DSTORE, returnTypeIndex);
                        mv.visitVarInsn(DLOAD, returnTypeIndex);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                        mv.visitVarInsn(ASTORE, returnObjectIndex);
                        mv.visitVarInsn(DLOAD, returnTypeIndex);

                        break;
                    }
                    case Type.ARRAY:
                    case Type.OBJECT:
                    {
                        mv.visitVarInsn(ASTORE, returnTypeIndex);
                        mv.visitVarInsn(ALOAD, returnTypeIndex);

                        mv.visitVarInsn(ASTORE, returnObjectIndex);
                        mv.visitVarInsn(ALOAD, returnTypeIndex);

                        break;
                    }
                    default: // don't be run
                        break;
                }

                mv.visitVarInsn(ALOAD, methodInfoIndex);
                mv.visitVarInsn(ALOAD, returnObjectIndex);
                mv.visitMethodInsn(INVOKEVIRTUAL, METHOD_INFO_CLASS_INTERNAL_NAME, "setReturnValue", "(Ljava/lang/Object;)V", false);
            }

            mv.visitIntInsn(BIPUSH, traceId);
            mv.visitVarInsn(ALOAD, methodInfoIndex);
            mv.visitInsn(ACONST_NULL);
            mv.visitMethodInsn(INVOKESTATIC, ownerClassName, end, endDescriptor, false);
        }
    }
}
