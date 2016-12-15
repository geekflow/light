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
package com.geeksaga.light.repository.entity;

import com.geeksaga.light.agent.profile.ProfileMethod;
import com.geeksaga.light.util.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author geeksaga
 */
//@Entity
public class ProfileData implements Serializable
{
    @Id
    private Object id;

    @Column(nullable = false, updatable = false)
    private long tid;

    @Column
    private byte type;

    @Column
    private int sequence;

    @Column
    private int level;

    @Column
    private int hash;

    @Column
    private int startTime;

    @Column
    private int startCpuTime;

    @Column
    private int elapsedTime;

    @Column
    private int elapsedCpuTime;

    public ProfileData() {}

    public ProfileData(long tid)
    {
        this.tid = tid;
    }

    public ProfileData(long tid, ProfileMethod profileMethod)
    {
        this.tid = tid;

        this.type = profileMethod.getType();
        this.sequence = profileMethod.getSequence();
        this.level = profileMethod.getLevel();
        this.hash = profileMethod.getHash();
        this.startTime = profileMethod.getStartTime();
        this.startCpuTime = profileMethod.getStartCpuTime();
        this.elapsedTime = profileMethod.getElapsedTime();
        this.elapsedCpuTime = profileMethod.getElapsedCpuTime();
    }

    public Object getId()
    {
        return id;
    }

    public long getTid()
    {
        return tid;
    }

    public void setTid(long tid)
    {
        this.tid = tid;
    }

    public byte getType()
    {
        return type;
    }

    public void setType(byte type)
    {
        this.type = type;
    }

    public int getSequence()
    {
        return sequence;
    }

    public void setSequence(int sequence)
    {
        this.sequence = sequence;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public int getHash()
    {
        return hash;
    }

    public void setHash(int hash)
    {
        this.hash = hash;
    }

    public int getStartTime()
    {
        return startTime;
    }

    public void setStartTime(int startTime)
    {
        this.startTime = startTime;
    }

    public int getStartCpuTime()
    {
        return startCpuTime;
    }

    public void setStartCpuTime(int startCpuTime)
    {
        this.startCpuTime = startCpuTime;
    }

    public int getElapsedTime()
    {
        return elapsedTime;
    }

    public void setElapsedTime(int elapsedTime)
    {
        this.elapsedTime = elapsedTime;
    }

    public int getElapsedCpuTime()
    {
        return elapsedCpuTime;
    }

    public void setElapsedCpuTime(int elapsedCpuTime)
    {
        this.elapsedCpuTime = elapsedCpuTime;
    }

    @Override
    public String toString()
    {
        return ToString.toString(getTid(), getType(), getHash(), getElapsedTime());
    }
}
