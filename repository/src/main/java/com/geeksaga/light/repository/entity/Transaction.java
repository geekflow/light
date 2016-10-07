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

import com.geeksaga.light.util.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author geeksaga
 */
//@Entity
public class Transaction implements Serializable
{
    @Id
    //    @Column(nullable = false, updatable = false)
    private Object id;

    //    @Id
    @Column(nullable = false, updatable = false)
    private long tid;

    @Column
    private int oid;

    @Column
    private byte[] guid;

    @Column
    private long endTimeMillis;

    @Column
    private int elapsedTime;

    @Column
    private int cpuTime;

    @Column
    private int sqlCount;

    @Column
    private int sqlTime;

    @Column
    private int fetchCount;

    @Column
    private int fetchTime;

    @Column
    private byte[] ipAddress;

    @Column
    private String transactionName;

    @Column
    private int transactionHash;

    @Column
    private int browserHash;

    @Column
    private int userHash;

    public Transaction() {}

    public Transaction(long tid)
    {
        this.tid = tid;
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

    public int getOid()
    {
        return oid;
    }

    public void setOid(int oid)
    {
        this.oid = oid;
    }

    public byte[] getGuid()
    {
        return guid;
    }

    public void setGuid(byte[] guid)
    {
        this.guid = guid;
    }

    public long getEndTimeMillis()
    {
        return endTimeMillis;
    }

    public void setEndTimeMillis(long endTimeMillis)
    {
        this.endTimeMillis = endTimeMillis;
    }

    public int getElapsedTime()
    {
        return elapsedTime;
    }

    public void setElapsedTime(int elapsedTime)
    {
        this.elapsedTime = elapsedTime;
    }

    public int getCpuTime()
    {
        return cpuTime;
    }

    public void setCpuTime(int cpuTime)
    {
        this.cpuTime = cpuTime;
    }

    public int getSqlCount()
    {
        return sqlCount;
    }

    public void setSqlCount(int sqlCount)
    {
        this.sqlCount = sqlCount;
    }

    public int getSqlTime()
    {
        return sqlTime;
    }

    public void setSqlTime(int sqlTime)
    {
        this.sqlTime = sqlTime;
    }

    public int getFetchCount()
    {
        return fetchCount;
    }

    public void setFetchCount(int fetchCount)
    {
        this.fetchCount = fetchCount;
    }

    public int getFetchTime()
    {
        return fetchTime;
    }

    public void setFetchTime(int fetchTime)
    {
        this.fetchTime = fetchTime;
    }

    public byte[] getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(byte[] ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public void setTransactionName(String transactionName)
    {
        this.transactionName = transactionName;
    }

    public String getTransactionName()
    {
        return transactionName;
    }

    public int getTransactionHash()
    {
        return transactionHash;
    }

    public void setTransactionHash(int transactionHash)
    {
        this.transactionHash = transactionHash;
    }

    public int getBrowserHash()
    {
        return browserHash;
    }

    public void setBrowserHash(int browserHash)
    {
        this.browserHash = browserHash;
    }

    public int getUserHash()
    {
        return userHash;
    }

    public void setUserHash(int userHash)
    {
        this.userHash = userHash;
    }

    @Override
    public String toString()
    {
        return ToString.toString(getTid(), getTransactionName(), getElapsedTime());
    }
}
