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

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

/**
 * @author geeksaga
 */
public class ToStringTest
{
    private static class Target
    {
        private int field1 = 100;
        private String field2 = "B";
        private int[] field3 = { 1, 2, 3 };
        private String field4 = null;
        private int[] field5 = null;

        @Override
        public String toString()
        {
            return String.format("%d,%s,%s,%s,%s", field1, field2, String.format("(%d,%d,%d)", field3[0], field3[1], field3[2]), field4, field5);
        }
    }

    @Test
    public void testToString()
    {
        assertThat("(100,toString)", is(ToString.toString(100, "toString")));

        assertThat("(100)", is(ToString.toString(100)));

        assertThat("(100,B,(1,2,3),null,null)", is(ToString.toString(new Target())));

        // using Gradle Build add to Field $jacocoData
        assertThat(ToString.toObjectString(new Target()), startsWith("(100,B,(1,2,3),null,null"));
    }

    @Test
    public void testToArrayString()
    {
        assertThat("(A,B)", is(ToString.toArrayString(new String[] { "A", "B" })));
    }
}
