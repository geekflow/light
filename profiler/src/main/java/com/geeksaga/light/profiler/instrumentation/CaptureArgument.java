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
package com.geeksaga.light.profiler.instrumentation;

import com.geeksaga.light.profiler.util.ASMUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author geeksaga
 */
public class CaptureArgument {
    public final static String ARGUMENT_CLASS_NAME = "com.geeksaga.light.profiler.trace.Argument";

    public static void aloadArgument(InsnList result, MethodNode method, LocalVariableNode localVar) throws IllegalAccessError {
        Type[] argTypes = Type.getArgumentTypes(method.desc);
        if (argTypes.length == 0 && ASMUtil.isStatic(method)) {
            result.add(new InsnNode(Opcodes.ACONST_NULL));
        } else {
            result.add(ASMUtil.createNewNode(ARGUMENT_CLASS_NAME));
            result.add(new InsnNode(Opcodes.DUP)); // Use predefined constant
            result.add(ASMUtil.createPushNode(argTypes.length));
            result.add(ASMUtil.createMethodInsn(Opcodes.INVOKESPECIAL, ARGUMENT_CLASS_NAME, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, Type.INT_TYPE)));
            result.add(ASMUtil.createASTORE(localVar));

            int argIndex = 0;
            if (!ASMUtil.isStatic(method)) {
                result.add(ASMUtil.createALOAD(localVar));
                result.add(ASMUtil.createALOAD(ASMUtil.getArgumentIndex(method, argIndex)));
                result.add(ASMUtil.createINVOKEVIRTUAL(ARGUMENT_CLASS_NAME, "set", "(Ljava/lang/Object;)V"));
                argIndex++;
            }

            for (int i = 0; i < argTypes.length; i++) {
                Type type = argTypes[i];

                switch (type.getSort()) {
                    case Type.BOOLEAN:
                    case Type.CHAR:
                    case Type.BYTE:
                    case Type.SHORT:
                    case Type.INT:
                        result.add(ASMUtil.createALOAD(localVar));
                        result.add(ASMUtil.createPushNode(i));
                        result.add(ASMUtil.createILOAD(ASMUtil.getArgumentIndex(method, argIndex)));

                        String param_sig = null;
                        switch (type.getSort()) {
                            case Type.BOOLEAN:
                                param_sig = "(IZ)V";
                                break;
                            case Type.CHAR:
                                param_sig = "(IC)V";
                                break;
                            case Type.BYTE:
                                param_sig = "(IB)V";
                                break;
                            case Type.SHORT:
                                param_sig = "(IS)V";
                                break;
                            case Type.INT:
                                param_sig = "(II)V";
                                break;
                        }

                        result.add(ASMUtil.createINVOKEVIRTUAL(ARGUMENT_CLASS_NAME, "set", param_sig));
                        break;
                    case Type.FLOAT:
                        result.add(ASMUtil.createALOAD(localVar));
                        result.add(ASMUtil.createPushNode(i));
                        result.add(ASMUtil.createFLOAD(ASMUtil.getArgumentIndex(method, argIndex)));
                        result.add(ASMUtil.createINVOKEVIRTUAL(ARGUMENT_CLASS_NAME, "set", "(IF)V"));
                        break;
                    case Type.LONG:
                        result.add(ASMUtil.createALOAD(localVar));
                        result.add(ASMUtil.createPushNode(i));
                        result.add(ASMUtil.createLLOAD(ASMUtil.getArgumentIndex(method, argIndex)));
                        result.add(ASMUtil.createINVOKEVIRTUAL(ARGUMENT_CLASS_NAME, "set", "(IJ)V"));
                        break;
                    case Type.DOUBLE:
                        result.add(ASMUtil.createALOAD(localVar));
                        result.add(ASMUtil.createPushNode(i));
                        result.add(ASMUtil.createDLOAD(ASMUtil.getArgumentIndex(method, argIndex)));
                        result.add(ASMUtil.createINVOKEVIRTUAL(ARGUMENT_CLASS_NAME, "set", "(ID)V"));
                        break;
                    case Type.ARRAY:
                    case Type.OBJECT:
                        result.add(ASMUtil.createALOAD(localVar));
                        result.add(ASMUtil.createPushNode(i));
                        result.add(ASMUtil.createALOAD(ASMUtil.getArgumentIndex(method, argIndex)));
                        result.add(ASMUtil.createINVOKEVIRTUAL(ARGUMENT_CLASS_NAME, "set", "(ILjava/lang/Object;)V"));

                        break;
                    default:
                        throw new IllegalAccessError("Unknown type." + type);
                }

                argIndex++;
            }

            result.add(ASMUtil.createALOAD(localVar));
        }
    }
}
