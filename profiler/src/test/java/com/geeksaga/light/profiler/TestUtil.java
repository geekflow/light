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
package com.geeksaga.light.profiler;

import com.geeksaga.light.profiler.asm.ClassNodeWrapper;
import com.geeksaga.light.profiler.util.ASMUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author geeksaga
 */
public class TestUtil {
    public static byte[] load(String name) throws Exception {
        return load(TestUtil.class.getResource("/" + convert(name)));
    }

    public static byte[] load(URL url) throws Exception
    {
        return load(new FileInputStream(new File(url.toURI())));
    }

    public static byte[] load(InputStream inputStream) throws Exception
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        while (true)
        {
            byte[] buffer = new byte[1024];
            int read = inputStream.read(buffer);
            if (read == -1)
            {
                break;
            }

            byteArrayOutputStream.write(buffer, 0, read);
        }

        inputStream.close();

        return byteArrayOutputStream.toByteArray();
    }

    public static ClassNodeWrapper loadClass(String fileName) throws Exception {
        return ASMUtil.parse(load(fileName));
    }

    private static String convert(String value) {
        Pattern pattern = Pattern.compile("[(\\w+).)]+");
        Matcher matcher = pattern.matcher(value);

        if(matcher.find())
        {
            return matcher.group(0).replace('.', '/') + ".class";
        }

        return value.replace('.', '/');
    }
}
