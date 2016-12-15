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

import java.util.UUID;

public final class IdentifierUtils
{
    private static final char sBitChars[] = { //
            'G', '5', 'g', 'D', 'j', 'P', 'V', 'M', 'R', 'Z', 'm', 'Q', 's', 'N', 'z', '-', //
            'B', 'i', '4', '_', 'k', '1', 'r', 'U', 'o', 'W', '8', 'l', '9', 'c', 'F', 'O', //
            'n', 'x', 'L', 'A', 'd', '7', 'u', 'H', '3', '0', 'S', 'a', 'f', 'C', 'h', '6', //
            'b', 'E', 'T', 'X', 't', 'y', 'q', 'J', '2', 'e', 'p', 'I', 'Y', 'K', 'v', 'w'  //
    };

    private static final long[] charBits = new long['z' + 1];

    private static final int BITS_PER_BYTE = 8;
    private static final int BYTES_PER_LONG = 8;
    private static final int BITS_PER_BASE64 = 6;
    private static final int mask6 = (1 << BITS_PER_BASE64) - 1;

    private static final int MAX_ID_LENGTH = BITS_PER_BYTE * BYTES_PER_LONG / BITS_PER_BASE64 + 1;

    static
    {
        charBits['G'] = 0;
        charBits['5'] = 1;
        charBits['g'] = 2;
        charBits['D'] = 3;
        charBits['j'] = 4;
        charBits['P'] = 5;
        charBits['V'] = 6;
        charBits['M'] = 7;
        charBits['R'] = 8;
        charBits['Z'] = 9;
        charBits['m'] = 10;
        charBits['Q'] = 11;
        charBits['s'] = 12;
        charBits['N'] = 13;
        charBits['z'] = 14;
        charBits['-'] = 15;
        charBits['B'] = 16;
        charBits['i'] = 17;
        charBits['4'] = 18;
        charBits['_'] = 19;
        charBits['k'] = 20;
        charBits['1'] = 21;
        charBits['r'] = 22;
        charBits['U'] = 23;
        charBits['o'] = 24;
        charBits['W'] = 25;
        charBits['8'] = 26;
        charBits['l'] = 27;
        charBits['9'] = 28;
        charBits['c'] = 29;
        charBits['F'] = 30;
        charBits['O'] = 31;
        charBits['n'] = 32;
        charBits['x'] = 33;
        charBits['L'] = 34;
        charBits['A'] = 35;
        charBits['d'] = 36;
        charBits['7'] = 37;
        charBits['u'] = 38;
        charBits['H'] = 39;
        charBits['3'] = 40;
        charBits['0'] = 41;
        charBits['S'] = 42;
        charBits['a'] = 43;
        charBits['f'] = 44;
        charBits['C'] = 45;
        charBits['h'] = 46;
        charBits['6'] = 47;
        charBits['b'] = 48;
        charBits['E'] = 49;
        charBits['T'] = 50;
        charBits['X'] = 51;
        charBits['t'] = 52;
        charBits['y'] = 53;
        charBits['q'] = 54;
        charBits['J'] = 55;
        charBits['2'] = 56;
        charBits['e'] = 57;
        charBits['p'] = 58;
        charBits['I'] = 59;
        charBits['Y'] = 60;
        charBits['K'] = 61;
        charBits['v'] = 62;
        charBits['w'] = 63;
    }

    private IdentifierUtils() {}

    public static String bytesToBase64(byte[] bytes)
    {
        char base64[] = new char[bytes.length];
        for (int i = 0; i < bytes.length; i++)
        {
            base64[i] = sBitChars[bytes[i]];
        }
        return new String(base64);
    }

    public static String longToBase64(long value)
    {
        char base64[] = new char[MAX_ID_LENGTH];
        for (int i = 0; i < MAX_ID_LENGTH; i++)
        {
            base64[MAX_ID_LENGTH - i - 1] = sBitChars[(int) (value & mask6)];

            value = value >>> BITS_PER_BASE64;
        }
        return new String(base64);
    }

    public static long base64ToLong(String base64)
    {
        long base64ToLong = 0L;
        int length = (base64.length() > MAX_ID_LENGTH) ? MAX_ID_LENGTH : base64.length();
        for (int i = 0; i < length; i++)
        {
            base64ToLong += charBits[base64.charAt(i)] << ((length - i - 1) * BITS_PER_BASE64);
        }

        return base64ToLong;
    }

    public static String randomString()
    {
        return longToBase64(nextLong());
    }

    // ------------------------------
    private final static long multiplier = 0x5DEECE66DL;
    private final static long addend = 0xBL;
    private final static long mask48 = (1L << 48) - 1;
    private static long seed = (System.currentTimeMillis() ^ multiplier) & mask48;

    public static void seed(long value)
    {
        seed = (value ^ multiplier) & mask48;
    }

    private static synchronized int next(int bits)
    {
        long nextSeed = (seed * multiplier + addend) & mask48;
        seed = nextSeed;

        return (int) (nextSeed >>> (48 - bits));
    }

    public static long nextLong()
    {
        return ((long) (next(32)) << 32) + next(32);
    }

    public static long UUID()
    {
        return UUID.randomUUID().getMostSignificantBits();
    }
}