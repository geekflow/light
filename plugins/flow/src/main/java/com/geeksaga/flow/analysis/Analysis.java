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
package com.geeksaga.flow.analysis;

import com.geeksaga.flow.dao.ClassDao;
import com.geeksaga.flow.dao.orientdb.ClassDaoImpl;
import com.geeksaga.flow.entity.Classes;
import com.geeksaga.flow.store.RepositoryFactory;
import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;
import com.geeksaga.light.profiler.asm.ClassNodeWrapper;
import com.geeksaga.light.profiler.util.ASMUtil;
import com.geeksaga.light.profiler.util.Constant;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.ListIterator;

/**
 * @author geeksaga
 */
public class Analysis
{
    private static final LightLogger logger = CommonLogger.getLogger(Analysis.class.getName());

    private static RepositoryFactory repositoryFactory = RepositoryFactory.getInstance();
    private static Printer printer = new Textifier();
    private static TraceMethodVisitor mp = new TraceMethodVisitor(printer);

    public static void analysis(Class clazz)
    {
        analysis(ASMUtil.parse(clazz));
    }

    public static void analysis(byte[] bytes)
    {
        analysis(bytes, false);
    }

    public static void analysis(byte[] bytes, boolean showConsole)
    {
        analysis(ASMUtil.parse(bytes), showConsole);
    }

    private static void analysis(ClassNodeWrapper classNodeWrapper, boolean showConsole)
    {
        if (showConsole)
        {
            debug(classNodeWrapper);
        }

        //        repositoryFactory.store("Classes", "name", classNodeWrapper.name);

        Classes classes = new Classes();
        classes.setName(classNodeWrapper.name);
        classes.setByteCodes(ASMUtil.toBytes(classNodeWrapper));

        ClassDao classDao = new ClassDaoImpl();
        classDao.save(classes);

        if (showConsole)
        {
            logger.debug("Constant Pool :");
            for (Constant s : classNodeWrapper.constantPool.getKeySet())
            {
                if (s.type == 'C')
                {
                    logger.debug("{} {}", s.type, s.strVal1);
                }
            }

            logger.debug(classNodeWrapper.name);

            List<FieldNode> fields = classNodeWrapper.fields;

            for (FieldNode field : fields)
            {
                logger.debug("{} = {} => {}", field.name, field.value, field.desc);
            }
        }
    }

    private static void analysis(ClassNodeWrapper classNodeWrapper)
    {
        analysis(classNodeWrapper, true);
    }

    private static void debug(ClassNodeWrapper clazz)
    {
        List<MethodNode> methods = clazz.methods;

        for (MethodNode method : methods)
        {
            logger.debug("\t{} {} ", method.name, method.desc);

            if (method.name.equals("main"))
            {
                print(method);
            }
        }
    }


    private static String insnToString(AbstractInsnNode insn)
    {
        insn.accept(mp);

        StringWriter sw = new StringWriter();

        printer.print(new PrintWriter(sw));
        printer.getText().clear();

        return sw.toString();
    }

    private static void print(MethodNode method)
    {
        InsnList instructions = method.instructions;

        ListIterator<AbstractInsnNode> iterator = instructions.iterator();

        while (iterator.hasNext())
        {
            AbstractInsnNode n = iterator.next();

            if (n instanceof MethodInsnNode)
            {
                logger.debug("\t\t{}", insnToString(n));
            }

            if (n instanceof FieldInsnNode)
            {
                logger.debug("\t\t\t{}", insnToString(n));
            }
        }
    }
}
