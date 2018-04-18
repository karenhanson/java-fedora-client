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

import java.net.URI;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import org.dataconservancy.pass.client.PassClient;
import org.dataconservancy.pass.model.Grant;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author Karen Hanson
 */
public class FedoraPassIndexClientITs {

    @Test
    public void testIndexClient() { 
        PassClient client = new FedoraPassClient();
        URI uri = client.findByAttribute(Grant.class, "awardNumber", "r01ey026617");
        assertTrue(uri!=null);
    }

    @Test
    public void testIndexClientMultipleResults() { 
        PassClient client = new FedoraPassClient();
        Set<URI> uris = client.findAllByAttribute(Grant.class, "oapCompliance", "Yes");
        assertTrue(uris.size()>2);
    }

    @Test
    public void testIndexClientMultipleResultsMultipleFilters() { 
        Map<String,Object> filters = new HashMap<String,Object>();
        filters.put("oapCompliance", "Yes");
        filters.put("endDate", "2020-08-10T00:00:00.000Z");        
        PassClient client = new FedoraPassClient();
        Set<URI> uris = client.findAllByAttribute(Grant.class, "oapCompliance", "Yes");
        assertTrue(uris.size()>0);
    }

    @Test
    public void testIndexClientArrayFilter() { 
        PassClient client = new FedoraPassClient();
        URI uri = client.findByAttribute(Grant.class, "submissions", "http://fcrepo:8080/fcrepo/rest/submissions/ba/3c/c7/d8/ba3cc7d8-6c37-40e3-b13a-359cc3bf2f77");
        assertTrue(uri!=null);
    }
    
}
