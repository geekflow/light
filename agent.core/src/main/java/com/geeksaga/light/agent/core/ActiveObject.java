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
package com.geeksaga.light.agent.core;

import com.geeksaga.light.agent.profile.ProfileData;
import com.geeksaga.light.agent.profile.ProfileMethod;
import com.geeksaga.light.agent.profile.ProfileCallStack;
import com.geeksaga.light.agent.trace.MethodInfo;
import com.geeksaga.light.util.IdentifierUtils;

/**
 * @author geeksaga
 */
public class ActiveObject
{
    private Thread currentThread;
    private MethodInfo methodInfo;
    private long transactionId;
    private long startTimeMillis;
    private long startNanoTime;
    private String transactionName;
    private ProfileCallStack profileCallStack;

    public ActiveObject(Thread currentThread)
    {
        this(currentThread, null, new ProfileMethod((byte) 0, 0, 0));
    }

    public ActiveObject(Thread currentThread, MethodInfo methodInfo)
    {
        this(currentThread, methodInfo, new ProfileMethod((byte) 0, 0, 0));
    }

    public ActiveObject(Thread currentThread, MethodInfo methodInfo, ProfileData rootProfile)
    {
        this.currentThread = currentThread;
        this.methodInfo = methodInfo;

        this.transactionId = IdentifierUtils.nextLong();

        this.profileCallStack = new ProfileCallStack(rootProfile);
    }

    public ActiveObject(Thread currentThread, MethodInfo methodInfo, String transactionName)
    {
        this.currentThread = currentThread;
        this.methodInfo = methodInfo;
        this.transactionName = transactionName;
    }

    public void setProfileCallStack(ProfileCallStack profileCallStack)
    {
        this.profileCallStack = profileCallStack;
    }

    public ProfileCallStack getProfileCallStack()
    {
        return profileCallStack;
    }

    public Thread getCurrentThread()
    {
        return currentThread;
    }

    public MethodInfo getMethodInfo()
    {
        return methodInfo;
    }

    public void setTransactionId(long transactionId)
    {
        this.transactionId = transactionId;
    }

    public long getTransactionId()
    {
        return transactionId;
    }

    public void setStartTimeMillis(long startTimeMillis)
    {
        this.startTimeMillis = startTimeMillis;
    }

    public long getStartTimeMillis()
    {
        return startTimeMillis;
    }

    public void setStartNanoTime(long startNanoTime)
    {
        this.startNanoTime = startNanoTime;
    }

    public long getStartNanoTime()
    {
        return startNanoTime;
    }

    public void setTransactionName(String transactionName)
    {
        this.transactionName = transactionName;
    }

    public String getTransactionName()
    {
        return transactionName;
    }
}
