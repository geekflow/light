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
package target;

/**
 * @author geeksaga
 */
public class TestMethods {
    public char doWithChar(String value1, char value2) {
        return 'A';
    }

    public byte doWithByte(byte value) {
        return 1;
    }

    public int doWithInt(int value) {
        return 0;
    }

    public float doWithFloat(float value) {
        return 0;
    }

    public double doWithDouble(double value) {
        return 0;
    }

    public boolean doWithBoolean(boolean value) {
        return false;
    }

    public short doWithShort(short value) {
        return 0;
    }

    public long doWithLong(long value) {
        return 0;
    }

    public int[] doWithArray(int[] value) {
        return new int[]{};
    }

    public String doWithObject(String value) {
        return "AA" + value;
    }

    public void doWithNothing() {
    }
}
