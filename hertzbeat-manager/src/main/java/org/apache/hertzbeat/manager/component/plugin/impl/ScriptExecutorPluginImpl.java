/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.hertzbeat.manager.component.plugin.impl;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.hertzbeat.common.entity.alerter.Alert;
import org.apache.hertzbeat.common.entity.plugin.PluginContext;
import org.apache.hertzbeat.common.script.Script;
import org.apache.hertzbeat.common.script.ScriptExecutor;
import org.apache.hertzbeat.common.support.SpringContextHolder;
import org.apache.hertzbeat.common.support.event.OfficialScriptPluginEvent;
import org.apache.hertzbeat.common.util.ScriptUtil;
import org.apache.hertzbeat.plugin.PostAlertPlugin;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScriptExecutorPluginImpl implements PostAlertPlugin {

    @Override
    public void execute(Alert alert, PluginContext pluginContext) {
        String type = pluginContext.param().getString("script-type", null);
        if (type == null) {
            log.warn("Script type is null");
            return;
        }

        ScriptExecutor scriptExecutor = getScriptExecutorByType(type);
        if (scriptExecutor == null) {
            log.warn("No executor found for script type: {}", type);
            return;
        }

        String scriptContent = pluginContext.param().getString("script-content", null);
        if (scriptContent == null) {
            log.warn("Script is null");
            return;
        }
        Script script = Script.builder().type(type).content(scriptContent).id(ScriptUtil.generateScriptKey(scriptContent)).build();
        ApplicationContext applicationContext = SpringContextHolder.getApplicationContext();
        applicationContext.publishEvent(new OfficialScriptPluginEvent(applicationContext, script, pluginContext.param().getString("collector", null)));
        log.info("Script has been sent to collector: {}", pluginContext.param().getString("collector", null));
    }

    public ScriptExecutor getScriptExecutorByType(String type) {
        return getScriptExecutors().get(type);
    }

    private Map<String, ScriptExecutor> getScriptExecutors() {
        ApplicationContext context = SpringContextHolder.getApplicationContext();
        Map<String, ScriptExecutor> beansOfType = context.getBeansOfType(ScriptExecutor.class);
        return beansOfType.values().stream()
                .collect(Collectors.toMap(ScriptExecutor::scriptType, Function.identity()));
    }

}

