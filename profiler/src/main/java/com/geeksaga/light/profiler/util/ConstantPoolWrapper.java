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

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.optimizer.ConstantPool;

import java.util.HashMap;
import java.util.Set;

/**
 * @see org.objectweb.asm.optimizer.ConstantPool;
 *
 * @author geeksaga
 */
public class ConstantPoolWrapper extends ConstantPool
{
    private final HashMap<Constant, Constant> map = new HashMap<Constant, Constant>();

    private final Constant key1 = new Constant();

    private final Constant key2 = new Constant();

    private final Constant key3 = new Constant();

    private final Constant key4 = new Constant();

    private final Constant key5 = new Constant();

    public Set<Constant> getKeySet()
    {
        return map.keySet();
    }

    private Constant ensureConstant(Constant key)
    {
        Constant result = getConstant(key);

        return (result != null) ? result : putConstant(new Constant(key));
    }

    private Constant ensureConstant(Constant key, String value)
    {
        Constant result = getConstant(key);

        if(result == null)
        {
            newUTF8(value, true);

            return putConstant(new Constant(key));
        }

        return result;
    }

    public Constant newInteger(final int value, boolean isExtend)
    {
        if(isExtend)
        {
            super.newInteger(value);
        }

        key1.set(value);

        return ensureConstant(key1);
    }

    public Constant newFloat(final float value, boolean isExtend)
    {
        if(isExtend)
        {
            super.newFloat(value);
        }

        key1.set(value);

        return ensureConstant(key1);
    }

    public Constant newLong(final long value, boolean isExtend)
    {
        if(isExtend)
        {
            super.newLong(value);
        }

        key1.set(value);

        return ensureConstant(key1);
    }

    public Constant newDouble(final double value, boolean isExtend)
    {
        if(isExtend)
        {
            super.newDouble(value);
        }

        key1.set(value);

        return ensureConstant(key1);
    }

    public Constant newUTF8(final String value, boolean isExtend)
    {
        if(isExtend)
        {
            super.newUTF8(value);
        }

        key1.set('s', value, null, null);

        return ensureConstant(key1);
    }

    private Constant newString(final String value)
    {
        key2.set('S', value, null, null);

        return ensureConstant(key2, value);
    }

    public Constant newClass(final String value, boolean isExtend)
    {
        if(isExtend)
        {
            super.newClass(value);
        }

        key2.set('C', value, null, null);

        return ensureConstant(key2, value);
    }

    public Constant newMethodType(final String methodDescriptor, boolean isExtend)
    {
        if(isExtend)
        {
            super.newMethodType(methodDescriptor);
        }

        key2.set('t', methodDescriptor, null, null);

        return ensureConstant(key2, methodDescriptor);
    }

    public Constant newHandle(final int tag, final String owner, final String name, final String desc, final boolean itf, boolean isExtend)
    {
        if(isExtend)
        {
            super.newHandle(tag, owner, name, desc, itf);
        }

        key4.set((char) ('h' - 1 + tag), owner, name, desc);

        Constant result = getConstant(key4);
        if (result == null)
        {
            if (tag <= Opcodes.H_PUTSTATIC)
            {
                newField(owner, name, desc, isExtend);
            }
            else
            {
                newMethod(owner, name, desc, itf, isExtend);
            }

            return putConstant(new Constant(key4));
        }

        return result;
    }

    public Constant newConst(final Object cst, boolean isExtend)
    {
        if (cst instanceof Integer)
        {
            int val = (Integer) cst;
            return newInteger(val, isExtend);
        }
        else if (cst instanceof Float)
        {
            float val = (Float) cst;
            return newFloat(val, isExtend);
        }
        else if (cst instanceof Long)
        {
            long val = (Long) cst;
            return newLong(val, isExtend);
        }
        else if (cst instanceof Double)
        {
            double val = (Double) cst;
            return newDouble(val, isExtend);
        }
        else if (cst instanceof String)
        {
            return newString((String) cst);
        }
        else if (cst instanceof Type)
        {
            Type t = (Type) cst;
            int s = t.getSort();
            if (s == Type.OBJECT)
            {
                return newClass(t.getInternalName(), isExtend);
            }
            else if (s == Type.METHOD)
            {
                return newMethodType(t.getDescriptor(), isExtend);
            }
            else
            {
                return newClass(t.getDescriptor(), isExtend);
            }
        }
        else if (cst instanceof Handle)
        {
            Handle h = (Handle) cst;
            return newHandle(h.getTag(), h.getOwner(), h.getName(), h.getDesc(), h.isInterface(), isExtend);
        }
        else
        {
            throw new IllegalArgumentException("value " + cst);
        }
    }

    public Constant newField(final String owner, final String name, final String desc, boolean isExtend)
    {
        if(isExtend)
        {
            super.newField(owner, name, desc);
        }

        key3.set('G', owner, name, desc);

        Constant result = getConstant(key3);
        if (result == null)
        {
            newClass(owner, isExtend);
            newNameType(name, desc, isExtend);

            return putConstant(new Constant(key3));
        }

        return result;
    }

    public Constant newMethod(final String owner, final String name, final String desc, final boolean itf, boolean isExtend)
    {
        if(isExtend)
        {
            super.newMethod(owner, name, desc, itf);
        }

        key3.set(itf ? 'N' : 'M', owner, name, desc);

        Constant result = getConstant(key3);
        if (result == null)
        {
            newClass(owner, isExtend);
            newNameType(name, desc, isExtend);

            return putConstant(new Constant(key3));
        }

        return result;
    }

    public Constant newInvokeDynamic(String name, String desc, Handle bsm, boolean isExtend, Object... bsmArgs)
    {
        if(isExtend)
        {
            super.newInvokeDynamic(name, desc, bsm, bsmArgs);
        }

        key5.set(name, desc, bsm, bsmArgs);

        Constant result = getConstant(key5);
        if (result == null)
        {
            newNameType(name, desc, isExtend);
            newHandle(bsm.getTag(), bsm.getOwner(), bsm.getName(), bsm.getDesc(), isExtend);

            for (Object bsmArg : bsmArgs)
            {
                newConst(bsmArg, isExtend);
            }

            return putConstant(new Constant(key5));
        }

        return result;
    }

    public Constant newNameType(final String name, final String desc, boolean isExtend)
    {
        if(isExtend)
        {
            super.newNameType(name, desc);
        }

        key2.set('T', name, desc, null);

        Constant result = getConstant(key2);
        if (result == null)
        {
            newUTF8(name, isExtend);
            newUTF8(desc, isExtend);

            return putConstant(new Constant(key2));
        }

        return result;
    }

    private Constant getConstant(final Constant key)
    {
        return map.get(key);
    }

    private Constant putConstant(final Constant constant)
    {
        return map.put(constant, constant);
    }
}