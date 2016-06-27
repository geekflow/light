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

import com.geeksaga.light.Product;

/**
 * @author geeksaga
 */
public class Main {
    public static void main(String[] args) {
        System.out.println(Product.NAME + " is simple APM (Application Performance Management).");
        System.out.println("Usage : execute java with -javaagent option");
        System.out.println("ex) java -javaagent:light.agent-x.x.x.jar -jar light.demo.jar");
    }
}
