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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author geeksaga
 */
public class LightDecompiler {
    private static final FernFlowerDecompiler decompiler = new FernFlowerDecompiler();

    public static String decompile(byte[] source) {
        return decompile("Temp", source);
    }

    public static String decompile(String fileName, byte[] source) {
        decompiler.setByteCode(source);

        Fernflower fernflower = new Fernflower(decompiler, decompiler, new HashMap<String, Object>(), new FernFlowerLogger());

        try {
            fernflower.getStructContext().addSpaceForLight("", fileName + ".class", true);
            fernflower.decompileContext();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            fernflower.clearContext();
        }

        return decompiler.getContent();
    }

    public static List<String> decompile(List<byte[]> sources) {
        List<String> result = new ArrayList<String>();

        if (sources != null) {
            for (byte[] source : sources) {
                result.add(decompile(source));
            }
        }

        return result;
    }
}
