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
package org.dataconservancy.pass.client.integration;

import java.net.URI;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.dataconservancy.pass.client.PassClient;
import org.dataconservancy.pass.client.PassClientFactory;
import org.dataconservancy.pass.model.Grant;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Karen Hanson
 */
public class IndexClientIT extends ClientITBase {

    static {
        if (System.getProperty("pass.fedora.baseurl") == null) {
            System.setProperty("pass.fedora.baseurl", "http://localhost:8080/fcrepo/rest/");
        }
        if (System.getProperty("pass.elasticsearch.url") == null) {
            System.setProperty("pass.elasticsearch.url", "http://localhost:9200");
        }
    }
    
    @Test
    public void testMixedCaseAwardNumber() throws InterruptedException {
        PassClient client = PassClientFactory.getPassClient();
        
        Grant grant = random(Grant.class, 1);
        URI grantId = client.createResource(grant);
        
        TimeUnit.SECONDS.sleep(3);
        URI foundGrantId1 = client.findByAttribute(Grant.class, "awardNumber", grant.getAwardNumber().toLowerCase());
        assertEquals(grantId, foundGrantId1);

        URI foundGrantId2 = client.findByAttribute(Grant.class, "awardNumber", grant.getAwardNumber().toUpperCase());
        assertEquals(grantId, foundGrantId2);
    }
}
