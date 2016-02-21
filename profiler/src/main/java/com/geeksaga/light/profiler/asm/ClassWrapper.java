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

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author geeksaga
 */
public class ClassWrapper extends ClassNode {
    public List<MethodWrapper> methodWrappers;

    public ClassWrapper() {
        this(Opcodes.ASM5);
    }

    public ClassWrapper(final int api) {
        super(api);
        methodWrappers = new ArrayList<MethodWrapper>();
    }

    public boolean isInterface() {
        return ((access & Opcodes.ACC_INTERFACE) != 0);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodWrapper methodWrapper = new MethodWrapper(super.visitMethod(access, name, desc, signature, exceptions));
        methodWrappers.add(methodWrapper);

        return methodWrapper;
    }
}