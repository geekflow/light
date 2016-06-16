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

import com.geeksaga.light.profiler.util.ASMUtil;
import com.geeksaga.light.profiler.util.ConstantPoolWrapper;
import org.objectweb.asm.*;
import org.objectweb.asm.optimizer.AnnotationConstantsCollector;
import org.objectweb.asm.optimizer.FieldConstantsCollector;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author geeksaga
 */
public class ClassNodeWrapper extends ClassNode
{
    public List<MethodWrapper> methodVisitWrappers;
    public ConstantPoolWrapper constantPool;

    private boolean isExtend = true;

    private String packageName;
    private String className;
    private String superClassName;

    public ClassNodeWrapper()
    {
        this(Opcodes.ASM5);
    }

    public ClassNodeWrapper(final int api)
    {
        super(api);
        methodVisitWrappers = new ArrayList<MethodWrapper>();
        constantPool = new ConstantPoolWrapper();
    }

    public String getClassName()
    {
        if (className == null)
        {
            className = ASMUtil.convertForAgent(name);
        }

        return className;
    }

    public String getSuperClassName()
    {
        if (superName != null)
        {
            superClassName = ASMUtil.convertForAgent(superName);
        }

        return superClassName;
    }

    public String getPackageName()
    {
        if (packageName == null)
        {
            int index = getClassName().lastIndexOf('.');
            if (index > -1)
            {
                packageName = getClassName().substring(0, index);
            }
        }

        return packageName;
    }

    public String[] getAllSuperClassNames()
    {
        return ASMUtil.getAllSuperClassesNames(this);
    }

    public String[] getInterfaceNames()
    {
        return ASMUtil.getInterfaceNames(this);
    }

    public String[] getAllInterfaceNames()
    {
        return ASMUtil.getAllInterfaceNames(this);
    }

    public boolean isInterface()
    {
        return ((access & Opcodes.ACC_INTERFACE) != 0);
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces)
    {
        if ((access & Opcodes.ACC_DEPRECATED) != 0)
        {
            constantPool.newUTF8("Deprecated", isExtend);
        }
        if ((access & Opcodes.ACC_SYNTHETIC) != 0)
        {
            constantPool.newUTF8("Synthetic", isExtend);
        }

        constantPool.newClass(name, isExtend);

        if (signature != null)
        {
            constantPool.newUTF8("Signature", isExtend);
            constantPool.newUTF8(signature, isExtend);
        }

        if (superName != null)
        {
            constantPool.newClass(superName, isExtend);
        }

        if (interfaces != null)
        {
            for (String anInterface : interfaces)
            {
                constantPool.newClass(anInterface, isExtend);
            }
        }

        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitSource(final String source, final String debug)
    {
        if (source != null)
        {
            constantPool.newUTF8("SourceFile", isExtend);
            constantPool.newUTF8(source, isExtend);
        }

        if (debug != null)
        {
            constantPool.newUTF8("SourceDebugExtension", isExtend);
        }

        super.visitSource(source, debug);
    }

    @Override
    public void visitOuterClass(final String owner, final String name, final String desc)
    {
        constantPool.newUTF8("EnclosingMethod", isExtend);
        constantPool.newClass(owner, isExtend);

        if (name != null && desc != null)
        {
            constantPool.newNameType(name, desc, isExtend);
        }

        super.visitOuterClass(owner, name, desc);
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

        return new AnnotationConstantsCollector(super.visitAnnotation(desc, visible), constantPool);
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

        return new AnnotationConstantsCollector(super.visitAnnotation(desc, visible), constantPool);
    }

    @Override
    public void visitAttribute(final Attribute attr)
    {
        super.visitAttribute(attr);
    }

    @Override
    public void visitInnerClass(final String name, final String outerName, final String innerName, final int access)
    {
        constantPool.newUTF8("InnerClasses", isExtend);

        if (name != null)
        {
            constantPool.newClass(name, isExtend);
        }

        if (outerName != null)
        {
            constantPool.newClass(outerName, isExtend);
        }

        if (innerName != null)
        {
            constantPool.newUTF8(innerName, isExtend);
        }

        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value)
    {
        if ((access & Opcodes.ACC_SYNTHETIC) != 0)
        {
            constantPool.newUTF8("Synthetic", isExtend);
        }
        if ((access & Opcodes.ACC_DEPRECATED) != 0)
        {
            constantPool.newUTF8("Deprecated", isExtend);
        }
        constantPool.newUTF8(name, isExtend);
        constantPool.newUTF8(desc, isExtend);

        if (signature != null)
        {
            constantPool.newUTF8("Signature", isExtend);
            constantPool.newUTF8(signature, isExtend);
        }

        if (value != null)
        {
            constantPool.newConst(value, isExtend);
        }

        return new FieldConstantsCollector(super.visitField(access, name, desc, signature, value), constantPool);
    }

//    @Override
//    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
//    {
//        CodeSizeEvaluatorWrapper methodVisitWrapper = new CodeSizeEvaluatorWrapper(super.visitMethod(access, name, desc, signature, exceptions));
//        methodVisitWrappers.add(methodVisitWrapper);
//
//        return methodVisitWrapper;
//    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions)
    {
        if ((access & Opcodes.ACC_SYNTHETIC) != 0)
        {
            constantPool.newUTF8("Synthetic", isExtend);
        }

        if ((access & Opcodes.ACC_DEPRECATED) != 0)
        {
            constantPool.newUTF8("Deprecated", isExtend);
        }

        constantPool.newUTF8(name, isExtend);
        constantPool.newUTF8(desc, isExtend);

        if (signature != null)
        {
            constantPool.newUTF8("Signature", isExtend);
            constantPool.newUTF8(signature, isExtend);
        }

        if (exceptions != null)
        {
            constantPool.newUTF8("Exceptions", isExtend);
            for (String exception : exceptions)
            {
                constantPool.newClass(exception, isExtend);
            }
        }

        MethodWrapper methodWrapper = new MethodWrapper(super.visitMethod(access, name, desc, signature, exceptions), constantPool);
        methodVisitWrappers.add(methodWrapper);

        return methodWrapper;
    }
}