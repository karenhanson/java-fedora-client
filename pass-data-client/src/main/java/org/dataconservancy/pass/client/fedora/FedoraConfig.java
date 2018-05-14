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
package org.dataconservancy.pass.client.fedora;

import org.dataconservancy.pass.client.util.ConfigUtil;
import org.dataconservancy.pass.model.PassEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds information and methods required to configure Fedora.
 * @author Karen Hanson
 */
public class FedoraConfig {

    private static final Logger LOG = LoggerFactory.getLogger(FedoraConfig.class);

    private static final String USER_KEY = "pass.fedora.user";
    private static final String DEFAULT_USER  = "admin";

    private static final String PWD_KEY = "pass.fedora.password";
    private static final String DEFAULT_PASSWORD = "moo";
    
    private static final String BASEURL_KEY = "pass.fedora.baseurl";
    private static final String DEFAULT_BASE_URL = "http://localhost:8080/fcrepo/rest/";

    
    /**
     * @param baseUrl the baseUrl to set
     */
    public static String getBaseUrl() {
        String baseUrl = ConfigUtil.getSystemProperty(BASEURL_KEY, DEFAULT_BASE_URL);
        if (!baseUrl.endsWith("/")) {
            baseUrl = baseUrl + "/";
        }
        LOG.debug("Using baseUrl: {}", baseUrl);
        return baseUrl;
    }

    /**
     * Retrieve the user name from a system property, or use default
     * @return user name
     */
    public static String getUserName() {
        String username = ConfigUtil.getSystemProperty(USER_KEY, DEFAULT_USER);
        LOG.debug("Using user name: {}", username);
        return username;
    }

    /**
     * Retrieve the password from a system property, or use default
     * @return user name
     */
    public static String getPassword() {
        String user = ConfigUtil.getSystemProperty(PWD_KEY, DEFAULT_PASSWORD);
        return user;
    }
    
    /**
     * @return the container path
     */
    public static String getContainer(String type) {
        String path = null;
        PassEntityType subdirectory = PassEntityType.getTypeByName(type);
        if (subdirectory!=null) {
            path = getBaseUrl() + subdirectory.getPlural();
        } else {
            throw new IllegalArgumentException("Type not recognized, container path not found.");
        }

        LOG.debug("Returning container path: {}", path);
        return path;
    }
    
}
