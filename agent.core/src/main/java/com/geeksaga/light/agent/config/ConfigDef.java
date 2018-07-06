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
public class ConfigDef
{
    public static final String domain_id = "domain_id";
    public static final String instance_id = "instance_id";

    public static final String db_type = "db_type";
    public static final String db_path = "db_path";
    public static final String db_url = "db_url";
    public static final String db_user = "db_user";
    public static final String db_password = "db_password";
    public static final String enable_db_in_memory = "enable_db_in_memory";
    public static final String enable_orientdb = "enable_orientdb";

    public static final String entry_point = "entry_point";
    public static final String class_max_size = "class_max_size";
    public static final String method_min_size = "method_min_size";
    public static final String method_max_size = "method_max_size";

    public static final String entry_point_class = "entry_point_class";
    public static final String entry_point_super_class = "entry_point_super_class";
    public static final String entry_point_interface = "entry_point_interface";
    public static final String entry_point_class_pattern = "entry_point_class_pattern";

    public static final String entry_point_ignore_class = "entry_point_ignore_class";
    public static final String entry_point_ignore_super_class = "entry_point_ignore_super_class";
    public static final String entry_point_ignore_interface = "entry_point_ignore_interface";
    public static final String entry_point_ignore_class_pattern = "entry_point_ignore_class_pattern";

    public static final String method_point_class = "method_point_class";
    public static final String method_point_super_class = "method_point_super_class";
    public static final String method_point_interface = "method_point_interface";
    public static final String method_point_class_pattern = "method_point_class_pattern";

    public static final String method_point_ignore_class = "method_point_ignore_class";
    public static final String method_point_ignore_super_class = "method_point_ignore_super_class";
    public static final String method_point_ignore_interface = "method_point_ignore_interface";
    public static final String method_point_ignore_class_pattern = "method_point_ignore_class_pattern";

    public static final String ignore_bci_pattern = "ignore_bci_pattern";
    public static final String allow_bci_pattern = "allow_bci_pattern";

    public static final String dump_mode = "dump_mode";
    public static final String dump_class_all = "dump_class_all";

    public static FieldNameMap names = FieldNameMap.toMap(ConfigDef.class);
}
