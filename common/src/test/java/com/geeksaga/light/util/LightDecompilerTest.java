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
package com.geeksaga.light.util;

import org.jetbrains.java.decompiler.main.Fernflower;
import org.junit.BeforeClass;
import org.junit.Test;
import target.TestMethods;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * @author geeksaga
 */
public class LightDecompilerTest
{
    private static String EXPECT = "package target;\n" +
            "\n" +
            "public class TestMethods {\n" +
            "   public boolean doWithBoolean(boolean value) {\n" +
            "      return value;\n" +
            "   }\n" +
            "\n" +
            "   public char doWithChar(char value) {\n" +
            "      return value;\n" +
            "   }\n" +
            "\n" +
            "   public byte doWithByte(byte value) {\n" +
            "      return value;\n" +
            "   }\n" +
            "\n" +
            "   public short doWithShort(short value) {\n" +
            "      return value;\n" +
            "   }\n" +
            "\n" +
            "   public int doWithInt(int value) {\n" +
            "      return value;\n" +
            "   }\n" +
            "\n" +
            "   public float doWithFloat(float value) {\n" +
            "      return value;\n" +
            "   }\n" +
            "\n" +
            "   public double doWithDouble(double value) {\n" +
            "      return value;\n" +
            "   }\n" +
            "\n" +
            "   public long doWithLong(long value) {\n" +
            "      return value;\n" +
            "   }\n" +
            "\n" +
            "   public int[] doWithArray(int[] value) {\n" +
            "      return value;\n" +
            "   }\n" +
            "\n" +
            "   public String doWithObject(String value) {\n" +
            "      return value;\n" +
            "   }\n" +
            "\n" +
            "   public void doWithNothing() {\n" +
            "   }\n" +
            "}\n";

    private String absolutePath() throws URISyntaxException
    {
        return TestMethods.class.getProtectionDomain().getCodeSource().getLocation().toString().replace("file:", "") + TestMethods.class.getCanonicalName().replace(".", File.separator) + ".class";
    }

    @BeforeClass
    public static void init()
    {
        if (SystemProperty.WINDOWS_OS)
        {
            EXPECT = EXPECT.replace("\n", "\r\n");
        }
    }

    @Test
    public void testDecompile() throws Exception
    {
        FernFlowerDecompiler decompiler = new FernFlowerDecompiler();
        decompiler.setByteCode(TestUtil.load(TestMethods.class.getName()));

        Fernflower fernflower = new Fernflower(decompiler, decompiler, new HashMap<String, Object>(), new FernFlowerLogger());

        try
        {
            fernflower.getStructContext().addSpaceForLight("", absolutePath(), true);
            fernflower.decompileContext();

            if (SystemProperty.WINDOWS_OS)
            {
                assertThat(decompiler.getContent(), is(EXPECT));
            }
        }
        finally
        {
            fernflower.clearContext();
        }
    }

    @Test
    public void testDecompileUseAPI() throws Exception
    {
        FernFlowerDecompiler decompiler = new FernFlowerDecompiler();
        decompiler.setByteCode(TestUtil.load(TestMethods.class.getName()));

        Fernflower fernflower = new Fernflower(decompiler, decompiler, new HashMap<String, Object>(), new FernFlowerLogger());

        try
        {
            fernflower.getStructContext().addSpace(new File(absolutePath()), true);
            fernflower.decompileContext();

            assertThat(LightDecompiler.decompile(TestUtil.load(TestMethods.class.getName())), is(decompiler.getContent()));
            assertThat(LightDecompiler.decompile(TestMethods.class.getSimpleName(), TestUtil.load(TestMethods.class.getName())), is(decompiler.getContent()));
        }
        finally
        {
            fernflower.clearContext();
        }
    }

    @Test
    public void testDecompileUseAPIForArray() throws Exception
    {
        assertThat(LightDecompiler.decompile(TestUtil.load(TestMethods.class.getName())), is(EXPECT));
        assertThat(LightDecompiler.decompile(new ArrayList<byte[]>()), notNullValue());

        List<byte[]> sources = new ArrayList<byte[]>();
        sources.add(TestUtil.load(TestMethods.class.getName()));
        sources.add(TestUtil.load(TestMethods.class.getName()));
        sources.add(TestUtil.load(TestMethods.class.getName()));

        List<String> result = LightDecompiler.decompile(sources);

        assertThat(result.size(), is(sources.size()));

        for (String source : result)
        {
            assertThat(source, is(EXPECT));
        }
    }
}
