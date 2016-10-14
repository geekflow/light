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

import com.geeksaga.light.agent.util.FieldNameObjectMap;

/**
 * @author geeksaga
 */
public class ConfigDefaultValueDef
{
    public static final short default_domain_id = -1;
    public static final short default_instance_id = -1;

    public static final String default_db_type = "orientdb";
    public static final String default_db_path = "./databases";
    public static final String default_db_url = "plocal:./databases/";
    public static final String default_db_user = "admin";
    public static final String default_db_password = "admin";
    public static final boolean default_enable_db_in_memory = false;

    public static final String[] default_entry_point = new String[] {};
    public static final int default_class_max_size = 1024 * 1024;
    public static final int default_method_min_size = 0;
    public static final int default_method_max_size = 48000;

    public static final String[] entry_point_class = new String[] {};
    public static final String[] entry_point_super_class = new String[] {};
    public static final String[] entry_point_interface = new String[] {};
    public static final String[] entry_point_class_pattern = new String[] {};

    public static final String[] entry_point_ignore_class = new String[] {};
    public static final String[] entry_point_ignore_super_class = new String[] {};
    public static final String[] entry_point_ignore_interface = new String[] {};
    public static final String[] entry_point_ignore_class_pattern = new String[] {};

    public static final String[] default_ignore_bci_pattern = new String[] {};
    public static final String[] default_allow_bci_pattern = new String[] {};

    public static FieldNameObjectMap names = FieldNameObjectMap.toMap(ConfigDefaultValueDef.class);
}
