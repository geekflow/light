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
package com.geeksaga.light.profiler.asm;

import com.geeksaga.light.profiler.util.ConstantPoolWrapper;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.CodeSizeEvaluator;
import org.objectweb.asm.optimizer.AnnotationConstantsCollector;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author geeksaga
 */
public class MethodWrapper extends CodeSizeEvaluator
{
    public MethodVisitor mvWrapper = null;
    public MethodNode method = null;
    private int methodHash = -1;
    private final ConstantPoolWrapper constantPool;

    private boolean isExtend = true;

    public MethodWrapper(final MethodVisitor mv, ConstantPoolWrapper constantPool)
    {
        this(Opcodes.ASM5, mv, constantPool);
    }

    protected MethodWrapper(int api, MethodVisitor mv, ConstantPoolWrapper constantPool)
    {
        super(api, mv);

        this.mvWrapper = mv;
        this.method = (MethodNode) mv;
        this.constantPool = constantPool;
    }

    public int getMethodHash()
    {
        if (methodHash == -1)
        {
//            methodHash = ASMUtil.hash(method);
        }

        return methodHash;
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault()
    {
        constantPool.newUTF8("AnnotationDefault", isExtend);

        return new AnnotationConstantsCollector(mv.visitAnnotationDefault(), constantPool);
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible)
    {
        constantPool.newUTF8(desc, isExtend);

        if (visible)
        {
            constantPool.newUTF8("RuntimeVisibleAnnotations", isExtend);
        }
        else
        {
            constantPool.newUTF8("RuntimeInvisibleAnnotations", isExtend);
        }

        return new AnnotationConstantsCollector(mv.visitAnnotation(desc, visible), constantPool);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible)
    {
        constantPool.newUTF8(desc, isExtend);

        if (visible)
        {
            constantPool.newUTF8("RuntimeVisibleTypeAnnotations", isExtend);
        }
        else
        {
            constantPool.newUTF8("RuntimeInvisibleTypeAnnotations", isExtend);
        }

        return new AnnotationConstantsCollector(mv.visitAnnotation(desc, visible), constantPool);
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(final int parameter, final String desc, final boolean visible)
    {
        constantPool.newUTF8(desc, isExtend);

        if (visible)
        {
            constantPool.newUTF8("RuntimeVisibleParameterAnnotations", isExtend);
        }
        else
        {
            constantPool.newUTF8("RuntimeInvisibleParameterAnnotations", isExtend);
        }

        return new AnnotationConstantsCollector(mv.visitParameterAnnotation(parameter, desc, visible), constantPool);
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type)
    {
        constantPool.newClass(type, isExtend);

        mv.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc)
    {
        constantPool.newField(owner, name, desc, isExtend);

        mv.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf)
    {
        constantPool.newMethod(owner, name, desc, itf, isExtend);

        mv.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs)
    {
        constantPool.newInvokeDynamic(name, desc, bsm, isExtend, bsmArgs);

        mv.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    @Override
    public void visitLdcInsn(final Object cst)
    {
        constantPool.newConst(cst, isExtend);

        mv.visitLdcInsn(cst);
    }

    @Override
    public void visitMultiANewArrayInsn(final String desc, final int dims)
    {
        constantPool.newClass(desc, isExtend);

        mv.visitMultiANewArrayInsn(desc, dims);
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible)
    {
        constantPool.newUTF8(desc, isExtend);

        if (visible)
        {
            constantPool.newUTF8("RuntimeVisibleTypeAnnotations", isExtend);
        }
        else
        {
            constantPool.newUTF8("RuntimeInvisibleTypeAnnotations", isExtend);
        }

        return new AnnotationConstantsCollector(mv.visitInsnAnnotation(typeRef, typePath, desc, visible), constantPool);
    }

    @Override
    public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type)
    {
        if (type != null)
        {
            constantPool.newClass(type, isExtend);
        }

        mv.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible)
    {
        constantPool.newUTF8(desc, isExtend);

        if (visible)
        {
            constantPool.newUTF8("RuntimeVisibleTypeAnnotations", isExtend);
        }
        else
        {
            constantPool.newUTF8("RuntimeInvisibleTypeAnnotations", isExtend);
        }

        return new AnnotationConstantsCollector(mv.visitTryCatchAnnotation(typeRef, typePath, desc, visible), constantPool);
    }

    @Override
    public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index)
    {
        if (signature != null)
        {
            constantPool.newUTF8("LocalVariableTypeTable", isExtend);
            constantPool.newUTF8(name, isExtend);
            constantPool.newUTF8(signature, isExtend);
        }

        constantPool.newUTF8("LocalVariableTable", isExtend);
        constantPool.newUTF8(name, isExtend);
        constantPool.newUTF8(desc, isExtend);

        mv.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible)
    {
        constantPool.newUTF8(desc, isExtend);

        if (visible)
        {
            constantPool.newUTF8("RuntimeVisibleTypeAnnotations", isExtend);
        }
        else
        {
            constantPool.newUTF8("RuntimeInvisibleTypeAnnotations", isExtend);
        }

        return new AnnotationConstantsCollector(mv.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible), constantPool);
    }

    @Override
    public void visitLineNumber(final int line, final Label start)
    {
        constantPool.newUTF8("LineNumberTable", isExtend);

        mv.visitLineNumber(line, start);
    }

    @Override
    public void visitMaxs(final int maxStack, final int maxLocals)
    {
        constantPool.newUTF8("Code", isExtend);

        mv.visitMaxs(maxStack, maxLocals);
    }
}