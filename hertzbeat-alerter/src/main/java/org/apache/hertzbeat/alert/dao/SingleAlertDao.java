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

package org.apache.hertzbeat.alert.dao;

import java.util.List;
import org.apache.hertzbeat.common.entity.alerter.SingleAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Single Alert Database Operations
 */
public interface SingleAlertDao extends JpaRepository<SingleAlert, Long>, JpaSpecificationExecutor<SingleAlert> {
    
    /**
     * Query alert by fingerprint
     * @param fingerprint alert fingerprint
     * @return alert
     */
    SingleAlert findByFingerprint(String fingerprint);


    /**
     * Query alerts by status 
     * @param status status firing or resolved
     * @return alerts
     */
    List<SingleAlert> querySingleAlertsByStatus(String status);
}
