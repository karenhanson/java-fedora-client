/*
 * Copyright 2018 Johns Hopkins University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dataconservancy.pass.client.util;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.TestName;

/**
 * @author apb@jhu.edu
 */
public class ConfigUtilTest {

    @Rule
    public final TestName testName = new TestName();

    @Rule
    public final RestoreSystemProperties r = new RestoreSystemProperties();

    @Rule
    public final EnvironmentVariables e = new EnvironmentVariables();

    @Test
    public void getSystemPropertyTest() {
        final String KEY = "test" + "." + testName.getMethodName();
        final String VALUE = UUID.randomUUID().toString();

        System.setProperty(KEY, VALUE);

        assertEquals(VALUE, ConfigUtil.getSystemProperty(KEY, null));
    }

    @Test
    public void getNormalizedEnvVarTest() {
        final String KEY = "test" + "." + testName.getMethodName();
        final String VALUE = UUID.randomUUID().toString();

        /* Set the environment variable AS_UPPER_CASE_WITH_UNDERSCORES */
        e.set(KEY.replace(".", "_").toUpperCase(), VALUE);

        assertEquals(VALUE, ConfigUtil.getSystemProperty(KEY, null));
    }
}
