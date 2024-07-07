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

package org.apache.hertzbeat.manager.service;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.hertzbeat.common.entity.dto.PluginUpload;
import org.apache.hertzbeat.common.entity.manager.PluginItem;
import org.apache.hertzbeat.common.entity.manager.PluginMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

/**
 * plugin service
 */
public interface PluginService {

    /**
     * load jar to classloader
     */
    void loadJarToClassLoader();


    /**
     * verify the type of the jar package
     *
     * @param jarFile jar file
     * @return return the full path of the Plugin interface implementation class
     */
    List<PluginItem> validateJarFile(File jarFile);


    /**
     * save plugin
     */
    void savePlugin(PluginUpload pluginUpload);

    /**
     * Determine whether the plugin is enabled
     *
     * @param clazz plugin Class
     * @return return true if enabled
     */
    boolean pluginIsEnable(Class<?> clazz);


    /**
     * get plugin page list
     *
     * @param specification Query condition
     * @param pageRequest   Paging condition
     * @return Plugins
     */
    Page<PluginMetadata> getPlugins(Specification<PluginMetadata> specification, PageRequest pageRequest);

    /**
     * execute plugin
     * @param clazz plugin interface
     * @param execute run plugin logic
     * @param <T> plugin type
     */
    <T> void pluginExecute(Class<T> clazz, Consumer<T> execute);

    /**
     * delete plugin
     *
     * @param ids set of plugin id
     */
    void deletePlugins(Set<Long> ids);

    void updateStatus(PluginMetadata plugin);

}
