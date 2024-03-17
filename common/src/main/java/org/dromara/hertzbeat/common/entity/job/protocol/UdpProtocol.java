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

package org.dromara.hertzbeat.common.entity.job.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Use udp implemented by socket for service port availability detection
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UdpProtocol {
    /**
     * IP ADDRESS OR DOMAIN NAME OF THE PEER HOST
     */
    private String host;
    
    /**
     * Port number
     */
    private String port;

    /**
     * TIME OUT PERIOD
     */
    private String timeout;
    
    /**
     * Custom protocol packet hexString Hexadecimal character string
     */
    private String content;
}
