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

package org.apache.hertzbeat.common.support.valid;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.hertzbeat.common.script.ScriptExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Script validator class.
 * This code references dromara/liteflow, thanks to the author Ge_Zuao.
 */
@Slf4j
@Component
public class ScriptValidator {

    private final Map<String, ScriptExecutor> scriptExecutors = new ConcurrentHashMap<>();

    @Autowired
    public ScriptValidator(List<ScriptExecutor> scriptExecutorList) {
        scriptExecutorList.forEach(scriptExecutor ->
                scriptExecutors.put(scriptExecutor.scriptType(), scriptExecutor)
        );
    }

    /**
     * Validate script
     *
     * @param script     script
     * @param scriptType scriptType
     * @return boolean
     */
    private boolean validateScript(String script, String scriptType) {

        if (scriptExecutors.isEmpty()) {
            log.error("No script modules loaded.");
            return false;
        }

        if (scriptType != null && !scriptExecutors.containsKey(scriptType)) {
            log.error("Specified script language {} not found.", scriptType);
            return false;
        }

        if (scriptExecutors.size() > 1 && scriptType == null) {
            log.error("Multiple script modules loaded. Please specify the script language.");
            return false;
        }

        ScriptExecutor scriptExecutor = (scriptType != null)
                ? scriptExecutors.get(scriptType) : scriptExecutors.values().iterator().next();

        try {
            scriptExecutor.compile(script);
        } catch (Exception e) {
            log.error("Script validation failed for {}: {}", scriptExecutor.scriptType(), e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * @param script script
     * @return boolean
     */
    public boolean validate(String script) {
        return validateScript(script, null);
    }

    /**
     * @param script script
     * @param scriptType scriptType
     * @return boolean
     */
    public boolean validate(String script, String scriptType) {
        return validateScript(script, scriptType);
    }

    /**
     * @param scripts scripts
     * @return boolean
     */
    public boolean validate(Map<String, String> scripts) {
        for (Map.Entry<String, String> script : scripts.entrySet()) {
            if (!validate(script.getValue(), script.getKey())) {
                return false;
            }
        }
        return true;
    }
}
