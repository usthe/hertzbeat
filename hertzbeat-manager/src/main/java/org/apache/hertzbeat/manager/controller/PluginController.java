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

package org.apache.hertzbeat.manager.controller;

import static org.apache.hertzbeat.common.constants.CommonConstants.FAIL_CODE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hertzbeat.common.entity.dto.Message;
import org.apache.hertzbeat.common.entity.dto.CustomPlugin;
import org.apache.hertzbeat.common.entity.dto.OfficialPlugin;
import org.apache.hertzbeat.common.entity.message.CollectRep;
import org.apache.hertzbeat.common.entity.plugin.PluginMetadata;
import org.apache.hertzbeat.manager.pojo.dto.PluginParam;
import org.apache.hertzbeat.manager.pojo.dto.PluginParametersVO;
import org.apache.hertzbeat.manager.service.PluginService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * plugin management API
 */
@Tag(name = "Plugin Manage API")
@RestController
@RequestMapping(path = "/api/plugin", produces = {APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Slf4j
public class PluginController {

    private final PluginService pluginService;

    @PostMapping("/custom")
    @Operation(summary = "upload custom plugin", description = "upload custom plugin")
    public ResponseEntity<Message<Void>> uploadCustomPlugin(@Valid CustomPlugin customPlugin) {
        try {
            pluginService.saveCustomPlugin(customPlugin);
            return ResponseEntity.ok(Message.success("Add success"));
        } catch (IOException e) {
            log.error("Failed to save custom plugin", e);
            return ResponseEntity.badRequest().body(Message.fail(FAIL_CODE, "Failed to save custom plugin: " + e.getMessage()));
        }
    }


    @PostMapping("/official")
    @Operation(summary = "upload official plugin", description = "upload official plugin")
    public ResponseEntity<Message<Void>> addOfficialPlugin(@Valid OfficialPlugin officialPlugin) {
        pluginService.saveOfficialPlugin(officialPlugin);
        return ResponseEntity.ok(Message.success("Add success"));
    }



    @GetMapping("/instances/{type}")
    @Operation(summary = "Get Plugins information", description = "Obtain plugins information based on conditions")
    public ResponseEntity<Message<Page<PluginMetadata>>> getPlugins(
        @Parameter(description = "plugin name search", example = "status") @RequestParam(required = false) String name,
        @Parameter(description = "plugin type search", example = "official or custom") @PathVariable String type,
        @Parameter(description = "List current page", example = "0") @RequestParam(defaultValue = "0") int pageIndex,
        @Parameter(description = "Number of list pagination", example = "8") @RequestParam(defaultValue = "8") int pageSize) {
        Page<PluginMetadata> alertPage = pluginService.getPlugins(name, type, pageIndex, pageSize);
        return ResponseEntity.ok(Message.success(alertPage));
    }


    @GetMapping("/official/infos")
    public ResponseEntity<Message<List<Map<String, String>>>> getOfficialPluginInfos() {
        return ResponseEntity.ok(Message.success(pluginService.getOfficialPluginInfos()));
    }



    @DeleteMapping
    @Operation(summary = "Delete plugins based on ID", description = "Delete plugins based on ID")
    public ResponseEntity<Message<Void>> deletePlugins(
        @Parameter(description = "Plugin IDs ", example = "6565463543") @RequestParam(required = false) List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            pluginService.deletePlugins(new HashSet<>(ids));
        }
        return ResponseEntity.ok(Message.success("Delete success"));
    }


    @PutMapping()
    @Operation(summary = "Update enable status", description = "Delete plugins based on ID")
    public ResponseEntity<Message<Void>> updatePluginStatus(@RequestBody PluginMetadata plugin) {
        pluginService.updateStatus(plugin);
        return ResponseEntity.ok(Message.success("Update success"));
    }

    @GetMapping("/params/define")
    @Operation(summary = "get param define", description = "get param define by jar path")
    public ResponseEntity<Message<PluginParametersVO>> getParamDefine(@RequestParam Long pluginMetadataId) {
        PluginParametersVO plugins = pluginService.getParamDefine(pluginMetadataId);
        return ResponseEntity.ok(Message.success(plugins));
    }

    @PostMapping("/params")
    @Operation(summary = "save param define", description = "save param define")
    public ResponseEntity<Message<Boolean>> saveParams(@RequestBody List<PluginParam> pluginParams) {
        pluginService.savePluginParam(pluginParams);
        return ResponseEntity.ok(Message.success(true));
    }

}
