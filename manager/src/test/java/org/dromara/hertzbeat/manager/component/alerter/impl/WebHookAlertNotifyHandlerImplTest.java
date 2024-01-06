package org.dromara.hertzbeat.manager.component.alerter.impl;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hertzbeat.manager.AbstractSpringIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

/**
 * Test case for {@link WebHookAlertNotifyHandlerImpl}
 */
@Slf4j
class WebHookAlertNotifyHandlerImplTest extends AbstractSpringIntegrationTest {

    @Resource
    private WebHookAlertNotifyHandlerImpl webHookAlertNotifyHandler;

    @BeforeEach
    void setUp() {
    }

    @Test
    void send() {
    }

    @Test
    void type() {
    }
}