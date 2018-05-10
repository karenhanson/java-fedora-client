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
public class FunderModelTests {
            
    /**
     * Simple verification that JSON file can be converted to Funder model
     * @throws Exception
     */
    @Test
    public void testFunderFromJsonConversion() throws Exception {
        
        InputStream json = FunderModelTests.class.getResourceAsStream("/funder.json");
        ObjectMapper objectMapper = new ObjectMapper();
        Funder funder = objectMapper.readValue(json, Funder.class);
        
        assertEquals(TestValues.FUNDER_ID_1, funder.getId().toString());
        assertEquals(TestValues.FUNDER_NAME, funder.getName());
        assertEquals(TestValues.FUNDER_URL, funder.getUrl().toString());
        assertEquals(TestValues.POLICY_ID_1, funder.getPolicy().toString());
        assertEquals(TestValues.FUNDER_LOCALKEY, funder.getLocalKey());
    }

    /**
     * Simple verification that Funder model can be converted to JSON
     * @throws Exception
     */
    @Test
    public void testFunderToJsonConversion() throws Exception {

        Funder funder = createFunder();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonFunder = objectMapper.writeValueAsString(funder);

        JSONObject root = new JSONObject(jsonFunder);

        assertEquals(root.getString("@id"),TestValues.FUNDER_ID_1);
        assertEquals(root.getString("@type"),"Funder");
        assertEquals(root.getString("name"),TestValues.FUNDER_NAME);
        assertEquals(root.getString("url"),TestValues.FUNDER_URL);
        assertEquals(root.getString("policy"),TestValues.POLICY_ID_1);
        assertEquals(root.getString("localKey"),TestValues.FUNDER_LOCALKEY);
    }
    
    /**
     * Creates two identical Funders and checks the equals and hashcodes match. 
     * Modifies one field on one of the Funders and verifies they no longer are 
     * equal or have matching hashcodes.
     * @throws Exception
     */
    @Test
    public void testFunderEqualsAndHashCode() throws Exception {

        Funder funder1 = createFunder();
        Funder funder2 = createFunder();

        assertEquals(funder1,funder2);
        assertEquals(funder1.hashCode(),funder2.hashCode());
        funder1.setName("different");
        assertTrue(!funder1.equals(funder2));
        assertTrue(funder1.hashCode()!=funder2.hashCode());
        
    }
    
    private Funder createFunder() throws Exception {
        Funder funder = new Funder();
        funder.setId(new URI(TestValues.FUNDER_ID_1));
        funder.setName(TestValues.FUNDER_NAME);
        funder.setUrl(new URI(TestValues.FUNDER_URL));
        funder.setPolicy(new URI(TestValues.POLICY_ID_1));
        funder.setLocalKey(TestValues.FUNDER_LOCALKEY);
        return funder;
    }
    
}
