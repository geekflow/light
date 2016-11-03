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
package com.geeksaga.light.agent.profile;

import com.geeksaga.light.util.ToString;

/**
 * @author geeksaga
 */
public class ProfileMethod extends ProfileData
{
    private final int hash;
    private final int startTime;
    private final int startCpuTime;
    private int elapsedTime;
    private int elapsedCpuTime;

    public ProfileMethod()
    {
        this(-1, -1, -1);
    }

    public ProfileMethod(int hash, int startTime, int startCpuTime)
    {
        this((byte) 0, 0, 0, hash, startTime, startCpuTime);
    }

    public ProfileMethod(byte type, int index, int parentIndex, int hash, int startTime, int startCpuTime)
    {
        super(type, index, parentIndex);
        this.hash = hash;
        this.startTime = startTime;
        this.startCpuTime = startCpuTime;
    }

    public void setElapsedTime(int elapsedTime)
    {
        this.elapsedTime = elapsedTime;
    }

    public void setElapsedCpuTime(int elapsedCpuTime)
    {
        this.elapsedCpuTime = elapsedCpuTime;
    }

    @Override
    public String toString()
    {
        return ToString.toString(super.toString(), hash, startTime, startCpuTime, elapsedTime, elapsedCpuTime);
    }
}