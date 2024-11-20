/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hertzbeat.common.script;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * The Script Bean Manager
 * This code references dromara/liteflow, thanks to the author Bryan.
 */
public class ScriptBeanManager {

    @Getter
    private static final Map<String, Object> scriptBeanMap = new HashMap<>();

    public static void addScriptBean(String key, Object bean) {
        scriptBeanMap.put(key, bean);
    }

}
