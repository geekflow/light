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
package com.geeksaga.flow.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author geeksaga
 */
@Entity
public class Classes implements Serializable
{
    @Id
    @Column(name = "name", nullable = false, updatable = false)
    private String name;

    @Column(name = "byteCodes")
    private byte[] byteCodes;

    @Transient
    private List<Classes> relation = new ArrayList<>();

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public byte[] getByteCodes()
    {
        return byteCodes;
    }

    public void setByteCodes(byte[] byteCodes)
    {
        this.byteCodes = byteCodes;
    }

    public List<Classes> getRelation()
    {
        return relation;
    }

    public void setRelation(List<Classes> relation)
    {
        this.relation = relation;
    }
}
