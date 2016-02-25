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
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author geeksaga
 */
public class CaptureReturn {
    public final static String RETURN_CLASS_NAME = "com.geeksaga.light.profiler.trace.Return";

    public static void aloadReturn(InsnList result, MethodNode method, Type type, String[] args, String tempVarName, LabelNode start, LabelNode end) {
        switch (type.getSort()) {
            case Type.BOOLEAN: {
                LocalVariableNode r = ASMUtil.addNewLocalVariable(method, tempVarName, Type.BOOLEAN_TYPE, start, end);
                result.add(ASMUtil.createISTORE(r));
                result.add(ASMUtil.createILOAD(r));
                for (int k = 0; args != null && k < args.length; k++) {
                    result.add(ASMUtil.createPushNode(args[k]));
                }
                result.add(ASMUtil.createILOAD(r));
                result.add(ASMUtil.createMethodInsn(Opcodes.INVOKESTATIC, RETURN_CLASS_NAME, "toObject", "(Z)Ljava/lang/Object;"));
                break;
            }
            case Type.CHAR: {
                LocalVariableNode r = ASMUtil.addNewLocalVariable(method, tempVarName, Type.CHAR_TYPE, start, end);
                result.add(ASMUtil.createISTORE(r));
                result.add(ASMUtil.createILOAD(r));
                for (int k = 0; args != null && k < args.length; k++) {
                    result.add(ASMUtil.createPushNode(args[k]));
                }
                result.add(ASMUtil.createILOAD(r));
                result.add(ASMUtil.createINVOKESTATIC(RETURN_CLASS_NAME, "toObject", "(C)Ljava/lang/Object;"));
                break;
            }
            case Type.BYTE: {
                LocalVariableNode r = ASMUtil.addNewLocalVariable(method, tempVarName, Type.BYTE_TYPE, start, end);
                result.add(ASMUtil.createISTORE(r));
                result.add(ASMUtil.createILOAD(r));
                for (int k = 0; args != null && k < args.length; k++) {
                    result.add(ASMUtil.createPushNode(args[k]));
                }
                result.add(ASMUtil.createILOAD(r));
                result.add(ASMUtil.createINVOKESTATIC(RETURN_CLASS_NAME, "toObject", "(B)Ljava/lang/Object;"));
                break;
            }
            case Type.INT: {
                LocalVariableNode r = ASMUtil.addNewLocalVariable(method, tempVarName, Type.INT_TYPE, start, end);
                result.add(ASMUtil.createISTORE(r));
                result.add(ASMUtil.createILOAD(r));
                for (int k = 0; args != null && k < args.length; k++) {
                    result.add(ASMUtil.createPushNode(args[k]));
                }
                result.add(ASMUtil.createILOAD(r));
                result.add(ASMUtil.createINVOKESTATIC(RETURN_CLASS_NAME, "toObject", "(I)Ljava/lang/Object;"));
                break;
            }
            case Type.SHORT: {
                LocalVariableNode r = ASMUtil.addNewLocalVariable(method, tempVarName, Type.SHORT_TYPE, start, end);
                result.add(ASMUtil.createISTORE(r));
                result.add(ASMUtil.createILOAD(r));
                for (int k = 0; args != null && k < args.length; k++) {
                    result.add(ASMUtil.createPushNode(args[k]));
                }
                result.add(ASMUtil.createILOAD(r));
                result.add(ASMUtil.createINVOKESTATIC(RETURN_CLASS_NAME, "toObject", "(S)Ljava/lang/Object;"));
                break;
            }
            case Type.FLOAT: {
                LocalVariableNode r = ASMUtil.addNewLocalVariable(method, tempVarName, Type.FLOAT_TYPE, start, end);
                result.add(ASMUtil.createFSTORE(r));
                result.add(ASMUtil.createFLOAD(r));
                for (int k = 0; args != null && k < args.length; k++) {
                    result.add(ASMUtil.createPushNode(args[k]));
                }
                result.add(ASMUtil.createFLOAD(r));
                result.add(ASMUtil.createINVOKESTATIC(RETURN_CLASS_NAME, "toObject", "(F)Ljava/lang/Object;"));
                break;
            }
            case Type.DOUBLE: {
                LocalVariableNode r = ASMUtil.addNewLocalVariable(method, tempVarName, Type.DOUBLE_TYPE, start, end);
                result.add(ASMUtil.createDSTORE(r));
                result.add(ASMUtil.createDLOAD(r));
                for (int k = 0; args != null && k < args.length; k++) {
                    result.add(ASMUtil.createPushNode(args[k]));
                }
                result.add(ASMUtil.createDLOAD(r));
                result.add(ASMUtil.createINVOKESTATIC(RETURN_CLASS_NAME, "toObject", "(D)Ljava/lang/Object;"));
                break;
            }
            case Type.LONG: {
                LocalVariableNode r = ASMUtil.addNewLocalVariable(method, tempVarName, Type.LONG_TYPE, start, end);
                result.add(ASMUtil.createLSTORE(r));
                result.add(ASMUtil.createLLOAD(r));
                for (int k = 0; args != null && k < args.length; k++) {
                    result.add(ASMUtil.createPushNode(args[k]));
                }
                result.add(ASMUtil.createLLOAD(r));
                result.add(ASMUtil.createINVOKESTATIC(RETURN_CLASS_NAME, "toObject", "(J)Ljava/lang/Object;"));
                break;
            }
            case Type.ARRAY:
            case Type.OBJECT: {
                LocalVariableNode r = ASMUtil.addNewLocalVariable(method, tempVarName, Type.getType(Object.class), start, end);
                result.add(ASMUtil.createASTORE(r));
                result.add(ASMUtil.createALOAD(r));
                for (int k = 0; args != null && k < args.length; k++) {
                    result.add(ASMUtil.createPushNode(args[k]));
                }
                result.add(ASMUtil.createALOAD(r));
                break;
            }
            default: // for void
                for (int k = 0; args != null && k < args.length; k++) {
                    result.add(ASMUtil.createPushNode(args[k]));
                }
                result.add(ASMUtil.createACONST_NULL());
                break;
        }
    }
}
