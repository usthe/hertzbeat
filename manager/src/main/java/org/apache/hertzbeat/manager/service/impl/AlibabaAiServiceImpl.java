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

package org.apache.hertzbeat.manager.service.impl;

import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.hertzbeat.common.constants.AiConstants;
import org.apache.hertzbeat.common.constants.AiTypeEnum;
import org.apache.hertzbeat.manager.pojo.dto.AiMessage;
import org.apache.hertzbeat.manager.pojo.dto.AliAiRequestParamDTO;
import org.apache.hertzbeat.manager.pojo.dto.AliAiResponse;
import org.apache.hertzbeat.manager.service.AiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;


/**
 * alibaba Ai
 */
@Service("AlibabaAiServiceImpl")
@Slf4j
public class AlibabaAiServiceImpl implements AiService {

    @Value("${aiConfig.model:qwen-turbo}")
    private String model;
    @Value("${aiConfig.api-key}")
    private String apiKey;


    private WebClient webClient;

    @PostConstruct
    private void init() {
        this.webClient = WebClient.builder()
                .baseUrl(AiConstants.AliAiConstants.URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                //sse
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(item -> item.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .build();
    }

    @Override
    public AiTypeEnum getType() {
        return AiTypeEnum.alibabaAi;
    }

    @Override
    public Flux<String> requestAi(String text) {
        checkParam(text, apiKey);

        AliAiRequestParamDTO aliAiRequestParamDTO = AliAiRequestParamDTO.builder()
                .model(model)
                .input(AliAiRequestParamDTO.Input.builder()
                        .messages(List.of(new AiMessage(AiConstants.AliAiConstants.REQUEST_ROLE, text)))
                        .build())
                .parameters(AliAiRequestParamDTO.Parameters.builder()
                        .maxTokens(AiConstants.AliAiConstants.MAX_TOKENS)
                        .temperature(AiConstants.AliAiConstants.TEMPERATURE)
                        .enableSearch(true)
                        .resultFormat("message")
                        .incrementalOutput(true)
                        .build())
                .build();


        return webClient.post()
                .body(BodyInserters.fromValue(aliAiRequestParamDTO))
                .retrieve()
                .bodyToFlux(AliAiResponse.class)
                .map(aliAiResponse -> {
                    if (Objects.nonNull(aliAiResponse)) {
                        List<AliAiResponse.Choice> choices = aliAiResponse.getOutput().getChoices();
                        if (CollectionUtils.isEmpty(choices)) {
                            return "";
                        }
                        return choices.get(0).getMessage().getContent();
                    }
                    return "";
                })
                .doOnError(error -> log.info("AiResponse Exception:{}", error.toString()));

    }


    private void checkParam(String param, String apiKey) {
        Assert.notNull(param, "text is null");
        Assert.notNull(apiKey, "aiConfig.api-key is null");
    }
}
