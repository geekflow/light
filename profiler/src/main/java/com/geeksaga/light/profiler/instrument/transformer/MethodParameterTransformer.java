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
import com.geeksaga.light.agent.trace.DebugTrace;
import com.geeksaga.light.agent.trace.EntryTrace;
import com.geeksaga.light.agent.trace.Parameter;
import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;
import com.geeksaga.light.profiler.asm.ClassNodeWrapper;
import com.geeksaga.light.profiler.asm.ClassReaderWrapper;
import com.geeksaga.light.profiler.filter.Filter;
import com.geeksaga.light.profiler.filter.LightFilter;
import com.geeksaga.light.profiler.util.ASMUtil;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import static com.geeksaga.light.profiler.util.ASMUtil.getInternalName;
import static org.objectweb.asm.Opcodes.*;

/**
 * The type Method parameter transformer.
 *
 * @author geeksaga
 */
public class MethodParameterTransformer implements ClassFileTransformer
{
    private LightLogger logger;

    private Filter filter;

    private TraceRegisterBinder traceRegisterBinder;
    private TraceContext traceContext;
    private int traceId;

    /**
     * The constant WINDOWS_OS.
     */
    public static final boolean WINDOWS_OS = getSystemProperty("os.name", "unix").contains("Window");

    /**
     * Instantiates a new Method parameter transformer.
     *
     * @param traceRegisterBinder the trace register binder
     * @param traceContext        the trace context
     */
    public MethodParameterTransformer(TraceRegisterBinder traceRegisterBinder, TraceContext traceContext)
    {
        this.logger = CommonLogger.getLogger(getClass().getName());
        this.filter = new LightFilter(traceContext);

        this.traceRegisterBinder = traceRegisterBinder;
        this.traceContext = traceContext;
        this.traceId = this.traceRegisterBinder.getTraceRegistryAdaptor().add(new EntryTrace(traceContext));
    }

    private static String getSystemProperty(String key, String def)
    {
        try
        {
            return System.getProperty(key, def);
        }
        catch (RuntimeException exception)
        {
            return def;
        }
    }

    @Override
    public byte[] transform(ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
    {
        if (filter.allow(classLoader, className))
        {
            logger.info("Transform => " + className);

            ClassNodeWrapper classNodeWrapper = new ClassNodeWrapper();
            ClassReader reader = new ClassReaderWrapper(classfileBuffer);
            reader.accept(new ClassVisitor(Opcodes.ASM5, classNodeWrapper)
            {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
                {
                    MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);

                    if (name.contains("<"))
                    {
                        return methodVisitor;
                    }

                    return new MethodParameterVisitor(access, desc, methodVisitor, ASMUtil.isStatic(access));
                }
            }, ClassReader.EXPAND_FRAMES);

            if (classNodeWrapper.isInterface())
            {
                return classfileBuffer;
            }

            byte[] bytes = ASMUtil.toBytes(classNodeWrapper);

            save(System.getProperty("user.dir") + File.separator + "Main.class", bytes);

            return bytes;
        }

        return classfileBuffer;
    }

    /**
     * Save.
     *
     * @param name the name
     * @param buff the buff
     */
    public void save(String name, byte[] buff)
    {
        try
        {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(replaceWindowsSeparator(name))));
            out.write(buff);
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Replace windows separator string.
     *
     * @param path the path
     * @return the string
     */
    public String replaceWindowsSeparator(String path)
    {
        if (WINDOWS_OS)
        {
            if (path != null)
            {
                return path.replace("\\", "\\\\");
            }
        }

        return path;
    }

    /**
     * The type Method parameter visitor.
     */
    class MethodParameterVisitor extends LocalVariablesSorter
    {
        /**
         * The Argument class internal name.
         */
        public final String ARGUMENT_CLASS_INTERNAL_NAME = getInternalName(Parameter.class.getName());

        private String desc;
        private boolean isStatic = false;
        private int[] parameterIndices;

        /**
         * Instantiates a new Method parameter visitor.
         *
         * @param access        the access
         * @param desc          the desc
         * @param methodVisitor the method visitor
         * @param isStatic      the is static
         */
        public MethodParameterVisitor(int access, String desc, MethodVisitor methodVisitor, boolean isStatic)
        {
            super(Opcodes.ASM5, access, desc, methodVisitor);

            this.desc = desc;
            this.isStatic = isStatic;
            this.parameterIndices = ASMUtil.getFixedArgumentIndices(desc, isStatic);
        }

        @Override
        public void visitCode()
        {
            int parameterVariableIndex = newLocal(Type.getType(ARGUMENT_CLASS_INTERNAL_NAME));
            Type[] argumentTypes = Type.getArgumentTypes(desc);

            mv.visitTypeInsn(NEW, ARGUMENT_CLASS_INTERNAL_NAME);
            mv.visitInsn(DUP);
            mv.visitIntInsn(BIPUSH, isStatic ? argumentTypes.length : argumentTypes.length + 1); // separate type
            mv.visitMethodInsn(INVOKESPECIAL, ARGUMENT_CLASS_INTERNAL_NAME, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, Type.INT_TYPE), false);
            mv.visitVarInsn(ASTORE, parameterVariableIndex);

            int parameterIndex = 0;
            if (!isStatic)
            {
                mv.visitVarInsn(ALOAD, parameterVariableIndex);
                mv.visitInsn(DUP);
                mv.visitIntInsn(BIPUSH, parameterIndex);
                mv.visitVarInsn(ALOAD, parameterIndices[parameterIndex]);
                mv.visitMethodInsn(INVOKEVIRTUAL, ARGUMENT_CLASS_INTERNAL_NAME, "set", "(ILjava/lang/Object;)V", false);

                parameterIndex++;
            }

            for (int i = 0, j = isStatic ? 0 : 1; i < argumentTypes.length; i++, j++)
            {
                Type type = argumentTypes[i];

                switch (type.getSort())
                {
                    case Type.BOOLEAN:
                    case Type.CHAR:
                    case Type.BYTE:
                    case Type.SHORT:
                    case Type.INT:
                        visitInstruction(ILOAD, j, parameterVariableIndex, parameterIndex);

                        String description = null;
                        switch (type.getSort())
                        {
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

        private void visitInstruction(int opcode, int index, int parameterVariableIndex, int parameterIndex)
        {
            mv.visitVarInsn(ALOAD, parameterVariableIndex);
            mv.visitLdcInsn(index);
            mv.visitVarInsn(opcode, parameterIndices[parameterIndex]);
        }

        public void visitMaxs(int maxStack, int maxLocals)
        {
            mv.visitMaxs(maxStack, maxLocals);
        }
    }
}