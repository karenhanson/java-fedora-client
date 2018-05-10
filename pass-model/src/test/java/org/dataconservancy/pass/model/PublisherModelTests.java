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
package org.dataconservancy.pass.model;

import java.io.InputStream;

import java.net.URI;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import org.json.JSONObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Model has been annotated with JSON tags. These tests do a simple check to ensure the
 * Jackson integration is functional and the equals / hashcode functions work
 * @author Karen Hanson
 */
public class PublisherModelTests {
            
    /**
     * Simple verification that JSON file can be converted to Publisher model
     * @throws Exception
     */
    @Test
    public void testPublisherFromJsonConversion() throws Exception {
        
        InputStream json = PublisherModelTests.class.getResourceAsStream("/publisher.json");
        ObjectMapper objectMapper = new ObjectMapper();
        Publisher publisher = objectMapper.readValue(json, Publisher.class);
        
        assertEquals(TestValues.PUBLISHER_ID_1, publisher.getId().toString());
        assertEquals(TestValues.PUBLISHER_NAME, publisher.getName());
        assertEquals(TestValues.PUBLISHER_PMCPARTICIPATION, publisher.getPmcParticipation().name());
    }

    /**
     * Simple verification that Publisher model can be converted to JSON
     * @throws Exception
     */
    @Test
    public void testPublisherToJsonConversion() throws Exception {

        Publisher publisher = createPublisher();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPublisher = objectMapper.writeValueAsString(publisher);

        JSONObject root = new JSONObject(jsonPublisher);

        assertEquals(root.getString("@id"),TestValues.PUBLISHER_ID_1);
        assertEquals(root.getString("@type"),"Publisher");
        assertEquals(root.getString("name"),TestValues.PUBLISHER_NAME);
        assertEquals(root.getString("pmcParticipation"),TestValues.PUBLISHER_PMCPARTICIPATION);         
    }
    
    /**
     * Creates two identical Publishers and checks the equals and hashcodes match. 
     * Modifies one field on one of the publishers and verifies they no longer are 
     * equal or have matching hashcodes.
     * @throws Exception
     */
    @Test
    public void testPublisherEqualsAndHashCode() throws Exception {

        Publisher publisher1 = createPublisher();
        Publisher publisher2 = createPublisher();
        
        assertEquals(publisher1,publisher2);
        publisher1.setName("different");
        assertTrue(!publisher1.equals(publisher2));
        
        assertTrue(publisher1.hashCode()!=publisher2.hashCode());
        publisher1 = publisher2;
        assertEquals(publisher1.hashCode(),publisher2.hashCode());
        
    }
    
    private Publisher createPublisher() throws Exception {
        Publisher publisher = new Publisher();
        publisher.setId(new URI(TestValues.PUBLISHER_ID_1));
        publisher.setName(TestValues.PUBLISHER_NAME);
        publisher.setPmcParticipation(PmcParticipation.valueOf(TestValues.PUBLISHER_PMCPARTICIPATION));
        
        return publisher;
    }
    
}
