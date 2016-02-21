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

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Type.getType;

/**
 * Discovers basic type information without loading classes.
 * <p/>
 * Each of the methods have semantics equivalent to those found on {@link Class}
 * <p/>
 * <p/>
 * This implementation performs zero caching. Either of the results of reading
 * from the underlying class files, or the results of computations. As a result
 * the implementation is likely to be significantly poorer than using equivalent
 * methods from {@link Class}. Users are expected to decorate or subclass with a
 * caching strategy which best suits your environment or application.
 *
 * @author Graham Allan
 * @author geeksaga
 */
public class TypeHierarchyUtil {
    public boolean isInterface(final Type type) {
        return hierarchyOf(type).isInterface();
    }

    public Type getSuperClass(final Type type) {
        return hierarchyOf(type).getSuperType();
    }

    public boolean isAssignableFrom(Type to, Type from) {
        return hierarchyOf(to).isAssignableFrom(hierarchyOf(from), this);
    }

    public String getCommonSuperClass(final String type1, final String type2) {
        Type c = Type.getObjectType(type1);
        Type d = Type.getObjectType(type2);

        if (isAssignableFrom(c, d)) {
            return type1;
        }

        if (isAssignableFrom(d, c)) {
            return type2;
        }

        if (isInterface(c) || isInterface(d)) {
            return "java/lang/Object";
        } else {
            do {
                c = getSuperClass(c);
            }
            while (!isAssignableFrom(c, d));

            return c.getInternalName();
        }
    }

    public TypeHierarchy hierarchyOf(Type type) {
        try {
            switch (type.getSort()) {
                case Type.BOOLEAN:
                    return TypeHierarchy.BOOLEAN_HIERARCHY;
                case Type.BYTE:
                    return TypeHierarchy.BYTE_HIERARCHY;
                case Type.CHAR:
                    return TypeHierarchy.CHAR_HIERARCHY;
                case Type.SHORT:
                    return TypeHierarchy.SHORT_HIERARCHY;
                case Type.INT:
                    return TypeHierarchy.INT_HIERARCHY;
                case Type.LONG:
                    return TypeHierarchy.LONG_HIERARCHY;
                case Type.FLOAT:
                    return TypeHierarchy.FLOAT_HIERARCHY;
                case Type.DOUBLE:
                    return TypeHierarchy.DOUBLE_HIERARCHY;
                case Type.VOID:
                    return TypeHierarchy.VOID_HIERARCHY;
                case Type.ARRAY:
                    return TypeHierarchy.hierarchyForArrayOfType(type);
                case Type.OBJECT:

                    if ("java/lang/Object".equals(type.getInternalName())) {
                        return TypeHierarchy.JAVA_LANG_OBJECT;
                    }

                    ClassReader reader = reader(type);

                    if (reader != null) {
                        return obtainHierarchyOf(reader);
                    }

                    return TypeHierarchy.JAVA_LANG_OBJECT;

                default:
                    throw new Error("Programmer error: received a type whose getSort() wasn't matched.");
            }
        } catch (IOException e) {
            return TypeHierarchy.JAVA_LANG_OBJECT;
        }
    }

    protected ClassReader reader(Type type) throws IOException {
        ClassReader reader = new ClassReaderWrapper(type.getInternalName());
        if (reader.b != null && reader.b.length > 0) {
            return reader;
        }

        return null;
    }

    protected TypeHierarchy obtainHierarchyOf(ClassReader reader) {
        return new TypeHierarchy(Type.getObjectType(reader.getClassName()), reader.getSuperName() == null ? null
                : Type.getObjectType(reader.getSuperName()), interfacesTypesFrom(reader.getInterfaces()),
                (reader.getAccess() & ACC_INTERFACE) != 0);
    }

    private List<Type> interfacesTypesFrom(String[] interfaces) {
        Type[] interfaceTypes = new Type[interfaces.length];

        for (int i = 0; i < interfaces.length; i++) {
            interfaceTypes[i] = Type.getObjectType(interfaces[i]);
        }
        return Arrays.asList(interfaceTypes);
    }

    public static class TypeHierarchy {
        private static final List<Type> IMPLEMENTS_NO_INTERFACES = Collections.emptyList();
        private static final List<Type> IMPLICIT_ARRAY_INTERFACES = unmodifiableList(asList(getType(Cloneable.class),
                getType(Serializable.class)));
        public static final TypeHierarchy JAVA_LANG_OBJECT = new TypeHierarchy(Type.getType(Object.class), null, IMPLEMENTS_NO_INTERFACES,
                false);

        public static final TypeHierarchy BOOLEAN_HIERARCHY = typeHierarchyForPrimitiveType(Type.BOOLEAN_TYPE);
        public static final TypeHierarchy BYTE_HIERARCHY = typeHierarchyForPrimitiveType(Type.BYTE_TYPE);
        public static final TypeHierarchy CHAR_HIERARCHY = typeHierarchyForPrimitiveType(Type.CHAR_TYPE);
        public static final TypeHierarchy SHORT_HIERARCHY = typeHierarchyForPrimitiveType(Type.SHORT_TYPE);
        public static final TypeHierarchy INT_HIERARCHY = typeHierarchyForPrimitiveType(Type.INT_TYPE);
        public static final TypeHierarchy LONG_HIERARCHY = typeHierarchyForPrimitiveType(Type.LONG_TYPE);
        public static final TypeHierarchy FLOAT_HIERARCHY = typeHierarchyForPrimitiveType(Type.FLOAT_TYPE);
        public static final TypeHierarchy DOUBLE_HIERARCHY = typeHierarchyForPrimitiveType(Type.DOUBLE_TYPE);
        public static final TypeHierarchy VOID_HIERARCHY = typeHierarchyForPrimitiveType(Type.VOID_TYPE);

        static TypeHierarchy hierarchyForArrayOfType(Type t) {
            return new TypeHierarchy(t, JAVA_LANG_OBJECT.type(), IMPLICIT_ARRAY_INTERFACES, false);
        }

        private static TypeHierarchy typeHierarchyForPrimitiveType(Type primitiveType) {
            return new TypeHierarchy(primitiveType, null, IMPLEMENTS_NO_INTERFACES, false);
        }

        private final Type thisType;
        private final Type superType;
        private final List<Type> interfaces;
        private final boolean isInterface;

        public TypeHierarchy(Type thisType, Type superType, List<Type> interfaces, boolean isInterface) {
            this.thisType = thisType;
            this.superType = superType;
            this.interfaces = interfaces;
            this.isInterface = isInterface;
        }

        public Type type() {
            return thisType;
        }

        public boolean representsType(Type t) {
            return t.equals(thisType);
        }

        public boolean isInterface() {
            return isInterface;
        }

        public Type getSuperType() {
            return superType;
        }

        public boolean isAssignableFrom(TypeHierarchy u, TypeHierarchyUtil typeHierarchyReader) {
            if (assigningToObject()) {
                return true;
            }

            if (this.isSameType(u)) {
                return true;
            } else if (this.isSuperTypeOf(u)) {
                return true;
            } else if (this.isInterfaceImplementedBy(u)) {
                return true;
            } else if (bothAreArrayTypes(u) && haveSameDimensionality(u)) {
                return JAVA_LANG_OBJECT.representsType(typeOfArray()) || arrayTypeIsAssignableFrom(u, typeHierarchyReader);
            } else if (bothAreArrayTypes(u) && isObjectArrayWithSmallerDimensionalityThan(u)) {
                return true;
            } else if (u.extendsObject() && !u.implementsAnyInterfaces()) {
                return false;
            }

            if (u.hasSuperType() && isAssignableFrom(u.getSuperType(), typeHierarchyReader)) {
                return true;
            } else if (u.implementsAnyInterfaces() && isAssignableFromAnyInterfaceImplementedBy(u, typeHierarchyReader)) {
                return true;
            }

            return false;
        }

        public boolean isAssignableFrom(Type type, TypeHierarchyUtil reader) {
            return isAssignableFrom(reader.hierarchyOf(type), reader);
        }

        private boolean isAssignableFromAnyInterfaceImplementedBy(TypeHierarchy u, TypeHierarchyUtil typeHierarchyReader) {
            for (Type ui : u.interfaces) {
                if (isAssignableFrom(ui, typeHierarchyReader)) {
                    return true;
                }
            }
            return false;
        }

        private boolean haveSameDimensionality(TypeHierarchy u) {
            return arrayDimensionality() == u.arrayDimensionality();
        }

        private boolean isObjectArrayWithSmallerDimensionalityThan(TypeHierarchy u) {
            return JAVA_LANG_OBJECT.representsType(typeOfArray()) && arrayDimensionality() <= u.arrayDimensionality();
        }

        private boolean arrayTypeIsAssignableFrom(TypeHierarchy u, TypeHierarchyUtil reader) {
            TypeHierarchy thisArrayType = reader.hierarchyOf(typeOfArray());
            return thisArrayType.isAssignableFrom(reader.hierarchyOf(u.typeOfArray()), reader);
        }

        private boolean bothAreArrayTypes(TypeHierarchy u) {
            return this.isArrayType() && u.isArrayType();
        }

        private Type typeOfArray() {
            return Type.getType(thisType.getInternalName().substring(thisType.getDimensions()));
        }

        private int arrayDimensionality() {
            return thisType.getDimensions();
        }

        private boolean isArrayType() {
            return thisType.getSort() == Type.ARRAY;
        }

        private boolean isInterfaceImplementedBy(TypeHierarchy u) {
            return u.interfaces.contains(type());
        }

        private boolean isSuperTypeOf(TypeHierarchy u) {
            return type().equals(u.getSuperType());
        }

        private boolean hasSuperType() {
            return getSuperType() != null && !JAVA_LANG_OBJECT.representsType(getSuperType());
        }

        private boolean implementsAnyInterfaces() {
            return !interfaces.isEmpty();
        }

        private boolean extendsObject() {
            return getSuperType() != null && JAVA_LANG_OBJECT.representsType(getSuperType());
        }

        private boolean isSameType(TypeHierarchy u) {
            return u.type().equals(type());
        }

        private boolean assigningToObject() {
            return JAVA_LANG_OBJECT.representsType(type());
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + thisType.hashCode();
            result = 31 * result + superType.hashCode();
            result = 31 * result + interfaces.hashCode();

            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj == null) {
                return false;
            } else if (getClass() != obj.getClass()) {
                return false;
            }

            TypeHierarchy other = (TypeHierarchy) obj;
            return thisType.equals(other.thisType);
        }

        @Override
        public String toString() {
            return String.format("%s [type=%s]", getClass().getSimpleName(), thisType.toString());
        }
    }
}