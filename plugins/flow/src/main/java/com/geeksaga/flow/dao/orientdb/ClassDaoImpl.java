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
package com.geeksaga.flow.dao.orientdb;

import com.geeksaga.flow.dao.ClassDao;
import com.geeksaga.flow.entity.Classes;
import com.geeksaga.flow.store.RepositoryFactory;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.List;

/**
 * @author geeksaga
 */
public class ClassDaoImpl implements ClassDao
{
    private RepositoryFactory repositoryFactory = RepositoryFactory.getInstance();

    @Override
    public boolean save(Classes classes)
    {
        ODatabaseDocumentTx documentTx = repositoryFactory.getDatabase();

        try
        {
            documentTx.begin();

            List<ODocument> result = documentTx.query(new OSQLSynchQuery<ODocument>("SELECT * FROM Classes where name = '" + classes.getName() + "'"));

//            for(ODocument document : result)
//            {
//                System.out.println(document);
//            }

            //            if(documentTx.query(new OSQLSynchQuery<ODocument>("SELECT * FROM " + className + " where ame = 'Luke' and name like 'L%'")))
            //            documentTx.getMetadata().getSchema().getClass();

            if(result.size() == 0)
            {
                ODocument document = new ODocument("Classes");
                document.field("name", classes.getName());
                document.field("byteCode", classes.getByteCodes());
                document.save();
            }
            else
            {
//                int recordsUpdated = documentTx.command(new OCommandSQL("UPDATE Classes SET byteCode = " + classes.getByteCodes() + " WHERE name = " + classes.getName())).execute();

                for(ODocument document : documentTx.browseClass("Classes"))
                {
                    document.field("byteCode", classes.getByteCodes());
                    document.save();
                }
//                documentTx.getMetadata().getSchema().reload();
            }

            documentTx.commit();
        }
        catch(Exception exception)
        {
            System.out.println(exception.getMessage());

            documentTx.rollback();

            return false;
        }
        finally
        {
            documentTx.close();
        }

        return true;
    }

    @Override
    public Classes find(Classes classes)
    {
        return null;
    }
}
