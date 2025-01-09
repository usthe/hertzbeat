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

/**
 * script executor interface
 * This code references dromara/liteflow, thanks to the author Bryan.
 */
public abstract class ScriptExecutor {

    public Object execute(String scriptKey) throws Exception {
        return executeScript(scriptKey);
    }

    public abstract Object executeScript(String scriptKey) throws Exception;


    public abstract String scriptType();


    /**
     * compile script
     *
     * @param script script content
     * @return boolean
     * @throws Exception if compile failed
     */
    public abstract Object compile(String script) throws Exception;
}
