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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author geeksaga
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TestClass.class)
public class StaticClassTest
{
    @Test
    public void testStaticClass()
    {
        mockStatic(TestClass.class);
        when(TestClass.addInteger(1, 1)).thenReturn(0);
        when(TestClass.addInteger(2, 2)).thenReturn(1);

        assertThat(0, is(TestClass.addInteger(1, 1)));
        assertThat(1, is(TestClass.addInteger(2, 2)));
    }
}

class TestClass
{
    static int addInteger(int a, int b)
    {
        return a + b;
    }
}