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

package org.apache.hertzbeat.manager.component.plugin.message;

import lombok.extern.slf4j.Slf4j;
import org.apache.hertzbeat.common.support.event.OfficialScriptPluginEvent;
import org.apache.hertzbeat.manager.scheduler.CollectorJobScheduler;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Send Server Message to collector by collector name
 */
@Component
@Slf4j
public class ServerMessageSender {

    private final CollectorJobScheduler collectorJobScheduler;

    public ServerMessageSender(CollectorJobScheduler collectorJobScheduler) {
        this.collectorJobScheduler = collectorJobScheduler;
    }

    @EventListener(OfficialScriptPluginEvent.class)
    public void onOfficialScriptPluginEvent(OfficialScriptPluginEvent event) {
        log.info("Received plugin event: {}", event);
        collectorJobScheduler.executeSyncScript(event.getScript(), event.getCollector());
    }
}
