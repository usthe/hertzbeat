/*
 *
 *  * Licensed to the Apache Software Foundation (ASF) under one or more
 *  * contributor license agreements.  See the NOTICE file distributed with
 *  * this work for additional information regarding copyright ownership.
 *  * The ASF licenses this file to You under the Apache License, Version 2.0
 *  * (the "License"); you may not use this file except in compliance with
 *  * the License.  You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.apache.hertzbeat.collector.collect.common.script;

import org.apache.hertzbeat.common.constants.ScriptTypeConstants;
import org.apache.hertzbeat.common.script.ScriptExecutor;
import org.apache.hertzbeat.common.script.executor.GraalJavaScriptExecutor;
import org.apache.hertzbeat.common.support.valid.ScriptValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test case for {@link ScriptExecutor}
 */
@SpringBootTest
class PluginScriptTest {

    @Autowired
    private ScriptValidator scriptValidator;

    @Autowired
    private GraalJavaScriptExecutor scriptExecutor;

    @Test
    public void testJavaScriptScriptComponentValidateFunction() {
        String correctScript = """
                var a=3;
                var b=2;
                var c=1;
                var d=5;
               
                function addByArray(values) {
                    var sum = 0;
                    for (var i = 0; i < values.length; i++) {
                        sum += values[i];
                    }
                    return sum;
                }
               
                var result = addByArray([a,b,c,d]);
               """;

        String wrongScript = """
                var a=3;
                var b=2;
                var c=1;
                var d=5;
                
                fon addByArray(values) {
                    var sum = 0;
                    for (var i = 0; i < values.length; i++) {
                        sum += values[i];
                    }
                    return sum;
                }
                
                var result = addByArray([a,b,c,d]);""";

        Assertions.assertTrue(scriptValidator.validate(correctScript));
        Assertions.assertFalse(scriptValidator.validate(wrongScript));
        Assertions.assertTrue(scriptValidator.validate(correctScript, ScriptTypeConstants.JAVASCRIPT));
        Assertions.assertFalse(scriptValidator.validate(correctScript, ScriptTypeConstants.JAVA));
    }

    @Test
    public void testJavaScriptScriptComponentValueFunction() throws Exception {
        String script = """
                var a=3;
                var b=2;
                var c=1;
                var d=5;
               
                function addByArray(values) {
                    var sum = 0;
                    for (var i = 0; i < values.length; i++) {
                        sum += values[i];
                    }
                    return sum;
                }
               
                var result = addByArray([a,b,c,d]);
                return result;
               """;
        scriptExecutor.compile(script);
        Object object = scriptExecutor.executeScript(script);
        Assertions.assertEquals(11, object);
        Assertions.assertNotEquals(12, object);

    }
}
