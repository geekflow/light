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
package com.geeksaga.light.profiler.util;

import com.geeksaga.light.profiler.asm.ClassNodeWrapper;
import com.geeksaga.light.profiler.asm.ClassReaderWrapper;
import com.geeksaga.light.profiler.asm.ClassWriterWrapper;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.objectweb.asm.tree.*;

import java.util.*;

/**
 * @author geeksaga
 */
public class ASMUtil
{
    private static final String START_HTTP_SIGNATURE = "(Ljavax/servlet/ServletRequest;Ljavax/servlet/ServletResponse;";

    private ASMUtil() {}

    public static boolean isStatic(ClassNodeWrapper clazz)
    {
        return containsAccess(clazz, Opcodes.ACC_STATIC);
    }

    public static boolean isStatic(MethodNode methodNode)
    {
        return containsAccess(methodNode, Opcodes.ACC_STATIC);
    }

    public static boolean isStatic(FieldNode field)
    {
        return containsAccess(field, Opcodes.ACC_STATIC);
    }

    public static boolean isStatic(int access)
    {
        return containsAccess(access, Opcodes.ACC_STATIC);
    }

    public static boolean containsAccess(InnerClassNode innerClass, int accessCode)
    {
        return (innerClass.access & accessCode) != 0;
    }

    public static boolean containsAccess(MethodNode method, int accessCode)
    {
        return (method.access & accessCode) != 0;
    }

    public static boolean containsAccess(FieldNode field, int accessCode)
    {
        return (field.access & accessCode) != 0;
    }

    public static boolean containsAccess(int access, int opcode)
    {
        return (access & opcode) != 0;
    }

    public static boolean containsAccess(ClassNodeWrapper classNode, int access)
    {
        return (classNode.access & access) != 0;
    }

    public static TypeInsnNode createNewNode(String className)
    {
        // TODO use to Type.getInternalName
        return new TypeInsnNode(Opcodes.NEW, getInternalName(className));
    }

    public static AbstractInsnNode createPushNode(int value)
    {
        if (value == -1)
        {
            return new InsnNode(Opcodes.ICONST_M1);
        }
        else if (value == 0)
        {
            return new InsnNode(Opcodes.ICONST_0);
        }
        else if (value == 1)
        {
            return new InsnNode(Opcodes.ICONST_1);
        }
        else if (value == 2)
        {
            return new InsnNode(Opcodes.ICONST_2);
        }
        else if (value == 3)
        {
            return new InsnNode(Opcodes.ICONST_3);
        }
        else if (value == 4)
        {
            return new InsnNode(Opcodes.ICONST_4);
        }
        else if (value == 5)
        {
            return new InsnNode(Opcodes.ICONST_5);
        }
        else if ((value >= -128) && (value <= 127))
        {
            return new IntInsnNode(Opcodes.BIPUSH, value);
        }
        else if ((value >= -32768) && (value <= 32767))
        {
            return new IntInsnNode(Opcodes.SIPUSH, value);
        }
        else
        {
            return new LdcInsnNode(value);
        }
    }

    public static AbstractInsnNode createPushNode(Object value)
    {
        AbstractInsnNode insnNode;

        if (value == null)
        {
            insnNode = new InsnNode(Opcodes.ACONST_NULL);
        }
        else
        {
            insnNode = new LdcInsnNode(value);
        }

        return insnNode;
    }

    public static MethodInsnNode createMethodInsn(int opcode, String className, String methodName, String desc)
    {
        return new MethodInsnNode(opcode, getInternalName(className), methodName, desc, opcode == Opcodes.INVOKEINTERFACE);
    }

    public static VarInsnNode createASTORE(LocalVariableNode localVariableNode)
    {
        return createASTORE(localVariableNode.index);
    }

    public static VarInsnNode createASTORE(int index)
    {
        return new VarInsnNode(Opcodes.ASTORE, index);
    }

    public static VarInsnNode createALOAD(LocalVariableNode localVariableNode)
    {
        return createALOAD(localVariableNode.index);
    }

    public static VarInsnNode createALOAD(int index)
    {
        return new VarInsnNode(Opcodes.ALOAD, index);
    }

    public static VarInsnNode createILOAD(LocalVariableNode localVariableNode)
    {
        return createILOAD(localVariableNode.index);
    }

    public static VarInsnNode createILOAD(int index)
    {
        return new VarInsnNode(Opcodes.ILOAD, index);
    }

    public static VarInsnNode createFLOAD(LocalVariableNode localVariableNode)
    {
        return createFLOAD(localVariableNode.index);
    }

    public static VarInsnNode createFLOAD(int index)
    {
        return new VarInsnNode(Opcodes.FLOAD, index);
    }

    public static VarInsnNode createDLOAD(LocalVariableNode localVariableNode)
    {
        return createDLOAD(localVariableNode.index);
    }

    public static VarInsnNode createDLOAD(int index)
    {
        return new VarInsnNode(Opcodes.DLOAD, index);
    }

    public static VarInsnNode createLLOAD(LocalVariableNode localVariableNode)
    {
        return createLLOAD(localVariableNode.index);
    }

    public static VarInsnNode createLLOAD(int index)
    {
        return new VarInsnNode(Opcodes.LLOAD, index);
    }

    public static VarInsnNode createISTORE(LocalVariableNode localVariableNode)
    {
        return new VarInsnNode(Opcodes.ISTORE, localVariableNode.index);
    }

    public static VarInsnNode createFSTORE(LocalVariableNode localVariableNode)
    {
        return new VarInsnNode(Opcodes.FSTORE, localVariableNode.index);
    }

    public static VarInsnNode createDSTORE(LocalVariableNode localVariableNode)
    {
        return new VarInsnNode(Opcodes.DSTORE, localVariableNode.index);
    }

    public static VarInsnNode createLSTORE(LocalVariableNode localVariableNode)
    {
        return new VarInsnNode(Opcodes.LSTORE, localVariableNode.index);
    }

    public static InsnNode createACONST_NULL()
    {
        return new InsnNode(Opcodes.ACONST_NULL);
    }

    public static int getArgumentIndex(MethodNode methodNode, int index)
    {
        return getFixedArgumentIndices(methodNode)[index];
    }

    public static int[] getFixedArgumentIndices(MethodNode methodNode)
    {
        return getFixedArgumentIndices(methodNode.desc, isStatic(methodNode));
    }

    public static int[] getFixedArgumentIndices(String desc, boolean isStatic)
    {
        Type[] args = Type.getArgumentTypes(desc);
        int[] r;
        if (!isStatic)
        {
            r = new int[args.length + 1];
            r[0] = 0;
            int size = 1;
            for (int i = 0; i < args.length; i++)
            {
                r[1 + i] = size;
                size += args[i].getSize();
            }
        }
        else
        {
            r = new int[args.length];
            int size = 0;
            for (int i = 0; i < args.length; i++)
            {
                r[i] = size;
                size += args[i].getSize();
            }
        }

        return r;
    }

    public static LocalVariableNode addNewLocalVariable(MethodNode methodNode, String name, Type type, LabelNode start, LabelNode end)
    {
        LocalVariableNode localVariableNode = new LocalVariableNode(name, type.getDescriptor(), null, start, end, methodNode.maxLocals);
        methodNode.maxLocals += type.getSize();
        methodNode.localVariables.add(localVariableNode);

        return localVariableNode;
    }

    public static String toInternalDescription(String className)
    {
        return "L" + getInternalName(className) + ";";
    }

    public static MethodInsnNode createINVOKESTATIC(String className, String methodName, String methodDesc)
    {
        return createMethodInsn(Opcodes.INVOKESTATIC, className, methodName, methodDesc);
    }

    public static MethodInsnNode createINVOKEVIRTUAL(String className, String methodName, String methodDesc)
    {
        return createMethodInsn(Opcodes.INVOKEVIRTUAL, className, methodName, methodDesc);
    }

    private static ClassVisitor useJSRInlinerAdapter(ClassVisitor visitor)
    {
        return new ClassVisitor(Opcodes.ASM5, visitor)
        {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
            {
                return new JSRInlinerAdapter(super.visitMethod(access, name, desc, signature, exceptions), access, name, desc, signature, exceptions);
            }
        };
    }

    public static ClassNodeWrapper parse(byte[] classfileBuffer)
    {
        if (classfileBuffer == null)
        {
            return null;
        }

        return parse(classfileBuffer, 0);
    }

    public static ClassNodeWrapper parse(byte[] classfileBuffer, int flags)
    {
        ClassNodeWrapper classNodeWrapper = new ClassNodeWrapper();
        ClassReader reader = new ClassReaderWrapper(classfileBuffer);

        reader.accept(useJSRInlinerAdapter(classNodeWrapper), new Attribute[0], flags);

        return classNodeWrapper;
    }

    public static ClassNodeWrapper parse(Object obj)
    {
        ClassNodeWrapper classNodeWrapper = new ClassNodeWrapper();

        ClassReader reader = new ClassReaderWrapper(obj.getClass().getName());

        reader.accept(useJSRInlinerAdapter(classNodeWrapper), new Attribute[0], 0);

        return classNodeWrapper;
    }

    public static ClassNodeWrapper parse(Class clazz)
    {
        ClassNodeWrapper classNodeWrapper = new ClassNodeWrapper();

        ClassReader reader = new ClassReaderWrapper(clazz);
        reader.accept(useJSRInlinerAdapter(classNodeWrapper), new Attribute[0], 0);

        return classNodeWrapper;
    }

    public static byte[] toBytes(Class clazz)
    {
        return toBytes(parse(clazz));
    }

    public static byte[] toBytes(Object obj)
    {
        return toBytes(parse(obj));
    }

    public static byte[] toBytes(ClassNodeWrapper clazz)
    {
        int flags = ClassWriter.COMPUTE_MAXS;

        if (clazz.version > Opcodes.V1_5)
        {
            flags |= ClassWriter.COMPUTE_FRAMES;
        }

        ClassWriter classWriter = new ClassWriterWrapper(flags);

        clazz.accept(useJSRInlinerAdapter(classWriter));

        return classWriter.toByteArray();
    }

    public static String convertForAgent(String name)
    {
        if (name != null)
        {
            return name.replace('/', '.');
        }

        return null;
    }

    public static String getInternalName(String name)
    {
        if (name != null)
        {
            return name.replace('.', '/');
        }

        return null;
    }

    public static String[] getAllSuperClassesNames(ClassNodeWrapper clazz)
    {
        LinkedList<ClassNodeWrapper> queue = new LinkedList<ClassNodeWrapper>();
        Set<String> allSuperClassesNames = new TreeSet<String>();
        queue.addLast(clazz);

        while (!queue.isEmpty())
        {
            ClassNodeWrapper classInQueue = queue.removeFirst();
            String superClassName = classInQueue.getSuperClassName();

            if (superClassName == null)
            {
                break;
            }

            ClassNodeWrapper superClassesOfClassInQueue = getSuperClassTypeOfClassWrapper(classInQueue);

            if (superClassesOfClassInQueue != null && !superClassesOfClassInQueue.getClassName().equals("java/lang/Object"))
            {
                allSuperClassesNames.add(superClassesOfClassInQueue.getClassName());

                queue.addLast(superClassesOfClassInQueue);
            }
        }

        return allSuperClassesNames.toArray(new String[allSuperClassesNames.size()]);
    }

    private static ClassNodeWrapper getSuperClassTypeOfClassWrapper(ClassNodeWrapper clazz)
    {
        String superClassName = clazz.getSuperClassName();

        ClassNodeWrapper classWrapper = getClassWrapperUsingClassName(superClassName);

        if (classWrapper != null)
        {
            return classWrapper;
        }

        return null;
    }

    private static ClassNodeWrapper getClassWrapperUsingClassName(String className)
    {
        if (ClassReaderWrapper.isValid(className))
        {
            ClassNodeWrapper classNode = new ClassNodeWrapper();
            ClassReader cr = new ClassReaderWrapper(className);
            cr.accept(classNode, new Attribute[0], 0);

            return classNode;
        }

        return null;
    }

    public static String[] getInterfaceNames(ClassNodeWrapper clazz)
    {
        String[] r = (String[]) clazz.interfaces.toArray(new String[clazz.interfaces.size()]);
        for (int i = 0; i < r.length; i++)
        {
            r[i] = convertForAgent(r[i]);
        }

        return r;
    }

    public static String[] getAllInterfaceNames(ClassNodeWrapper clazz)
    {
        LinkedList<ClassNodeWrapper> queue = new LinkedList<ClassNodeWrapper>();
        Set<String> allInterfaceNames = new TreeSet<String>();
        queue.addLast(clazz);

        while (!queue.isEmpty())
        {
            ClassNodeWrapper classInQueue = queue.removeFirst();
            String superClassName = classInQueue.getSuperClassName();
            List<ClassNodeWrapper> interfacesOfClassInQueue = getInterfacesTypeOfClassWrapper(classInQueue);

            if (classInQueue.isInterface())
            {
                allInterfaceNames.add(classInQueue.getClassName());
            }
            else
            {
                if (superClassName != null && !superClassName.equals("java/lang/Object"))
                {
                    ClassNodeWrapper classNode = getClassWrapperUsingClassName(superClassName);

                    if (classNode != null)
                    {
                        queue.addLast(classNode);
                    }
                }
            }

            for (ClassNodeWrapper classWrapperOfInterface : interfacesOfClassInQueue)
            {
                queue.addLast(classWrapperOfInterface);
            }
        }

        return allInterfaceNames.toArray(new String[allInterfaceNames.size()]);
    }

    private static List<ClassNodeWrapper> getInterfacesTypeOfClassWrapper(ClassNodeWrapper clazz)
    {
        String[] interfaceNames = clazz.getInterfaceNames();
        List<ClassNodeWrapper> interfaces = new ArrayList<ClassNodeWrapper>();

        for (String interfaceName : interfaceNames)
        {
            ClassNodeWrapper classWrapper = getClassWrapperUsingClassName(interfaceName);

            if (classWrapper != null)
            {
                interfaces.add(classWrapper);
            }
        }

        return interfaces;
    }

    public static boolean isHttp(String signature)
    {
        return signature.startsWith(START_HTTP_SIGNATURE);
    }
}
