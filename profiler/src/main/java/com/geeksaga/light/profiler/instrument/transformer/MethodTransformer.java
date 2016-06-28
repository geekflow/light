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
import com.geeksaga.light.agent.trace.MethodTrace;
import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;
import com.geeksaga.light.profiler.asm.ClassNodeWrapper;
import com.geeksaga.light.profiler.asm.ClassReaderWrapper;
import com.geeksaga.light.profiler.filter.Filter;
import com.geeksaga.light.profiler.filter.LightFilter;
import com.geeksaga.light.profiler.util.ASMUtil;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @author geeksaga
 */
public class MethodTransformer implements ClassFileTransformer
{
    private LightLogger logger;
    private Filter filter;

    private TraceRegisterBinder traceRegisterBinder;
    private TraceContext traceContext;
    private int traceId;

    public MethodTransformer(TraceRegisterBinder traceRegisterBinder, TraceContext traceContext)
    {
        this.logger = CommonLogger.getLogger(this.getClass().getName());
        this.filter = new LightFilter();

        this.traceRegisterBinder = traceRegisterBinder;
        this.traceContext = traceContext;
        this.traceId = this.traceRegisterBinder.getTraceRegistryAdaptor().add(new MethodTrace(traceContext));
    }

    @Override
    public byte[] transform(ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
    {
        if (filter.allow(classLoader, className))
        {
            ClassNodeWrapper classNodeWrapper = new ClassNodeWrapper();
            ClassReader reader = new ClassReaderWrapper(classfileBuffer);
            reader.accept(new ClassVisitor(Opcodes.ASM5, classNodeWrapper)
            {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
                {
                    MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
                    return new MethodAdapter(access, name, desc, mv);
                }
            }, ClassReader.EXPAND_FRAMES);

            if (classNodeWrapper.isInterface())
            {
                return classfileBuffer;
            }

            return ASMUtil.toBytes(classNodeWrapper);
        }

        return classfileBuffer;
    }

    private class MethodAdapter extends AdviceAdapter
    {
        private String name;
        private Label timeStart = new Label();
        private Label timeEnd = new Label();

        MethodAdapter(int access, String name, String desc, MethodVisitor methodVisitor)
        {
            super(Opcodes.ASM5, methodVisitor, access, name, desc);

            this.name = name;
        }

        @Override
        protected void onMethodEnter()
        {
            mv.visitLabel(timeStart);
            int time = newLocal(Type.getType("J"));
            visitLocalVariable("time", "J", null, timeStart, timeEnd, time);

            //            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            //            mv.visitLdcInsn("Enter " + name);
            //            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals)
        {
            visitLabel(timeEnd);

            super.visitMaxs(maxStack, maxLocals);
        }

        @Override
        protected void onMethodExit(int opcode)
        {
            if (opcode != ATHROW)
            {
                // FIXME finally
            }
        }
    }
}
