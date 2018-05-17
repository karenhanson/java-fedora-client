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

/**
 * @author Karen Hanson
 */
public class ConfigUtil {

    /**
     * Retrieve property from a system property or renvironment variable or set to default
     * <p>
     * Given a string as a system property name (and a default) will:
     * <ol>
     * <li>Look up the system property with the matching name, and return it if defined</li>
     * <li>Try to match an environment variable matching the key transformed TO_UPPER_CASE with periods substituted
     * with underscores</li>
     * <li>Use the default of none others match</li>
     * </ol>
     * </p>
     *
     * @param key - property/variable name in property-normal form (period separators, ideally all lowercas)
     * @param defaultValue
     * @return
     */
    public static String getSystemProperty(final String key, final String defaultValue) {
        return System.getProperty(key, System.getenv().getOrDefault(toEnvName(key), defaultValue));
    }

    static String toEnvName(String name) {
        return name.toUpperCase().replace('.', '_');
    }
}
