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
package com.geeksaga.light.agent.config;

import com.geeksaga.light.agent.util.FieldNameMap;

/**
 * @author geeksaga
 */
public class ConfigValueDef
{
    public static final short domain_id = -1;
    public static final short instance_id = -1;

    public static final String db_path = "./databases";
    public static final String db_url = "plocal:./databases/";
    public static final String db_user = "admin";
    public static final String db_password = "admin";
    public static final boolean enable_db_in_memory = false;

    public static final String[] entry_point = null;
    public static final int class_max_size = 1024 * 1024;
    public static final int method_min_size = 0;
    public static final int method_max_size = 48000;

    public static final String[] ignore_bci_pattern = null;

    public static FieldNameMap names = FieldNameMap.toMap(ConfigValueDef.class);
}
