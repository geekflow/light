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
package com.geeksaga.light.profiler.selector;

import java.util.regex.Pattern;

public class PatternMatch
{
    private final Pattern compliedPattern;

    public PatternMatch(String pattern)
    {
        compliedPattern = Pattern.compile(pattern(pattern));
    }

    private String pattern(String pattern)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("^");
        for (char c : pattern.toCharArray())
        {
            sb.append(c == '*' ? ".*" : Pattern.quote(String.valueOf(c)));
        }
        sb.append("$");

        return sb.toString();
    }

    public boolean matches(String value)
    {
        return compliedPattern.matcher(value).matches();
    }
}