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
package org.dataconservancy.pass.client.elasticsearch;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashSet;
import java.util.Set;

import org.dataconservancy.pass.client.util.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds information and methods required to configure Fedora.
 * @author Karen Hanson
 */
public class IndexerConfig {

    private static final Logger LOG = LoggerFactory.getLogger(IndexerConfig.class);
    
    private static final String INDEXER_URL_KEY = "pass.elasticsearch.url";
    private static final String DEFAULT_INDEXER_URL = "http://localhost:9200";

    
    /**
     * 
     * @return
     */
    public static Set<URL> getIndexerHostUrl() {
        Set<URL> urls = new HashSet<URL>();
        
        String sUrls = ConfigUtil.getSystemProperty(INDEXER_URL_KEY, DEFAULT_INDEXER_URL);
        String[] arrUrl = sUrls.split(",");

        try {
            for (String sUrl : arrUrl) {
                urls.add(new URL(sUrl));
            }
            LOG.debug("Using indexer host URL(s): {}", sUrls);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Indexer host path contains invalid URL:" + sUrls);
        }
        return urls;
    }

    
}
