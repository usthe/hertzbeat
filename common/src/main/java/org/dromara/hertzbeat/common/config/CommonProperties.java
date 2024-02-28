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

package org.dromara.hertzbeat.common.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * common module properties
 *
 * @author tom
 */
@Getter
@ConfigurationProperties(prefix = "common")
public class CommonProperties {

    /**
     * secret key for password aes entry, must 16 bits
     */
    private String secretKey;

    /**
     * data queue impl
     */
    private DataQueueProperties queue;

    /**
     * sms impl properties
     */
    private SmsProperties sms;

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setQueue(DataQueueProperties queue) {
        this.queue = queue;
    }

    public void setSms(SmsProperties sms) {
        this.sms = sms;
    }

    @Getter
    public static class DataQueueProperties {

        private QueueType type = QueueType.Memory;
        
        private KafkaProperties kafka;

        public void setType(QueueType type) {
            this.type = type;
        }

        public void setKafka(KafkaProperties kafka) {
            this.kafka = kafka;
        }
    }

    public enum QueueType {
        /** in memory **/
        Memory,
        /** kafka **/
        Kafka,
        /** with netty connect **/
        Netty,
        /** rabbit mq **/
        Rabbit_Mq
    }
    
    @Getter
    public static class KafkaProperties {
        /**
         * kafka的连接服务器url
         */
        private String servers;
        /**
         * metrics data topic
         */
        private String metricsDataTopic;
        /**
         * alerts data topic
         */
        private String alertsDataTopic;

        public void setServers(String servers) {
            this.servers = servers;
        }

        public void setMetricsDataTopic(String metricsDataTopic) {
            this.metricsDataTopic = metricsDataTopic;
        }

        public void setAlertsDataTopic(String alertsDataTopic) {
            this.alertsDataTopic = alertsDataTopic;
        }
    }

    @Getter
    public static class SmsProperties {
        private TencentSmsProperties tencent;

        public void setTencent(TencentSmsProperties tencent) {
            this.tencent = tencent;
        }
    }

    @Getter
    public static class TencentSmsProperties {

        /**
         * 腾讯云账户访问密钥id
         */
        private String secretId;

        /**
         * 腾讯云账户访问密钥key
         */
        private String secretKey;

        /**
         * SMS短信应用app id
         */
        private String appId;

        /**
         * 短信签名
         */
        private String signName;

        /**
         * 短信模版ID
         */
        private String templateId;

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public void setSecretId(String secretId) {
            this.secretId = secretId;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public void setSignName(String signName) {
            this.signName = signName;
        }

        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }
    }
}
