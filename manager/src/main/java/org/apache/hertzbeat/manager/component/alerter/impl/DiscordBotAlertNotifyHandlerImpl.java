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

package org.apache.hertzbeat.manager.component.alerter.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hertzbeat.common.entity.alerter.Alert;
import org.apache.hertzbeat.common.entity.manager.NoticeReceiver;
import org.apache.hertzbeat.common.entity.manager.NoticeTemplate;
import org.apache.hertzbeat.manager.pojo.dto.DiscordDTO;
import org.apache.hertzbeat.manager.support.exception.AlertNoticeException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * Send alarm information through Discord robot
 */
@Component
@RequiredArgsConstructor
@Slf4j
final class DiscordBotAlertNotifyHandlerImpl extends AbstractAlertNotifyHandlerImpl {

    @Override
    public void send(NoticeReceiver receiver, NoticeTemplate noticeTemplate, Alert alert) throws AlertNoticeException {
        try {
            var notifyBody = DiscordDTO.DiscordNotifyDTO.builder()
                    .embeds(List.of(DiscordDTO.EmbedDTO.builder()
                            .title("[" + bundle.getString("alerter.notify.title") + "]")
                            .description(renderContent(noticeTemplate, alert))
                            .build()))
                    .build();
            var url = String.format(alerterProperties.getDiscordWebhookUrl(), receiver.getDiscordChannelId());
            var headers = new HttpHeaders();
            headers.add("Authorization", "Bot " + receiver.getDiscordBotToken());
            headers.setContentType(MediaType.APPLICATION_JSON);
            var request = new HttpEntity<>(notifyBody, headers);
            var entity = restTemplate.postForEntity(url, request, DiscordDTO.DiscordResponseDTO.class);
            if (entity.getStatusCode() == HttpStatus.OK && entity.getBody() != null) {
                var body = entity.getBody();
                if (body.getId() != null) {
                    log.debug("Send Discord Bot Success");
                } else {
                    log.warn("Send Discord Bot Failed: {}, error_code: {}", body.getCode(), body.getMessage());
                    throw new AlertNoticeException(body.getMessage());
                }
            } else {
                log.warn("Send Discord Bot Failed {}", entity.getBody());
                throw new AlertNoticeException("Http StatusCode " + entity.getStatusCode());
            }
        } catch (Exception e) {
            throw new AlertNoticeException("[Discord Bot Notify Error] " + e.getMessage());
        }
    }

    @Override
    public byte type() {
        return 9;
    }

}
