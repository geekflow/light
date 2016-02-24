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
package com.geeksaga.light.profiler.trace;

/**
 * @author geeksaga
 */
public class Argument {
    // first this object
    private Object[] arguments;

    public Argument(int size) {
        if (size <= 0) {
            return;
        }

        arguments = new Object[size];
    }

    public int size() {
        return (arguments == null ? 0 : arguments.length);
    }

    public void set(int index, Object value) {
        arguments[index] = value;
    }

    public void set(int index, boolean value) {
        arguments[index] = value;
    }

    public void set(int index, char value) {
        arguments[index] = value;
    }

    public void set(int index, short value) {
        arguments[index] = value;
    }

    public void set(int index, byte value) {
        arguments[index] = value;
    }

    public void set(int index, int value) {
        arguments[index] = value;
    }

    public void set(int index, long value) {
        arguments[index] = value;
    }

    public void set(int index, float value) {
        arguments[index] = value;
    }

    public void set(int index, double value) {
        arguments[index] = value;
    }
}
