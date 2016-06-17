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

public class TestClass
{
    public static void a()
    {
        b();
        c();
        d();
        e();
    }
    
    public static void b()
    {
        c();
        d();
        e();
    }
    
    public static void c()
    {
        d();
        e();
    }
    
    public static void d()
    {
        e();
    }
    
    public static void e()
    {}

    public static void main(String[] args)
    {
        System.out.println(String.format("%s -------------------- Start", TestClass.class.getName()));

        a();
        b();
        c();
        d();
        e();
        
        System.out.println(String.format("%s -------------------- End", TestClass.class.getName()));
    }
}
