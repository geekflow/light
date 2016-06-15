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
package org.objectweb.asm.optimizer;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingMethodAdapter;

/**
 * A {@link MethodVisitor} that renames fields and methods, and removes debug
 * info.
 * 
 * @author Eugene Kuleshov
 */
public class MethodOptimizer extends RemappingMethodAdapter implements Opcodes {

    private final ClassOptimizer classOptimizer;

    public MethodOptimizer(ClassOptimizer classOptimizer, int access,
                           String desc, MethodVisitor mv, Remapper remapper) {
        super(Opcodes.ASM5, access, desc, mv, remapper);
        this.classOptimizer = classOptimizer;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public void visitParameter(String name, int access) {
        // remove parameter info
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        // remove annotations
        return null;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        // remove annotations
        return null;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef,
                                                 TypePath typePath, String desc, boolean visible) {
        return null;
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(final int parameter,
                                                      final String desc, final boolean visible) {
        // remove annotations
        return null;
    }

    @Override
    public void visitLocalVariable(final String name, final String desc,
                                   final String signature, final Label start, final Label end,
                                   final int index) {
        // remove debug info
    }

    @Override
    public void visitLineNumber(final int line, final Label start) {
        // remove debug info
    }

    @Override
    public void visitFrame(int type, int local, Object[] local2, int stack,
                           Object[] stack2) {
        // remove frame info
    }

    @Override
    public void visitAttribute(Attribute attr) {
        // remove non standard attributes
    }

    @Override
    public void visitLdcInsn(Object cst) {
        if (!(cst instanceof Type)) {
            super.visitLdcInsn(cst);
            return;
        }

        // transform Foo.class for 1.2 compatibility
        String ldcName = ((Type) cst).getInternalName();
        String fieldName = "class$" + ldcName.replace('/', '$');
        if (!classOptimizer.syntheticClassFields.contains(ldcName)) {
            classOptimizer.syntheticClassFields.add(ldcName);
            FieldVisitor fv = classOptimizer.syntheticFieldVisitor(ACC_STATIC
                    | ACC_SYNTHETIC, fieldName, "Ljava/lang/Class;");
            fv.visitEnd();
        }

        String clsName = classOptimizer.clsName;
        mv.visitFieldInsn(GETSTATIC, clsName, fieldName, "Ljava/lang/Class;");
    }
}
