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

import java.util.ArrayList;
import java.util.List;

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
public class PolicyModelTests {
            
    /**
     * Simple verification that JSON file can be converted to Policy model
     * @throws Exception
     */
    @Test
    public void testPolicyFromJsonConversion() throws Exception {
        
        InputStream json = PolicyModelTests.class.getResourceAsStream("/policy.json");
        ObjectMapper objectMapper = new ObjectMapper();
        Policy policy = objectMapper.readValue(json, Policy.class);
        
        assertEquals(TestValues.POLICY_ID_1, policy.getId().toString());
        assertEquals("Policy", policy.getType());
        assertEquals(TestValues.POLICY_TITLE, policy.getTitle());
        assertEquals(TestValues.POLICY_DESCRIPTION, policy.getDescription());
        assertEquals(TestValues.REPOSITORY_ID_1, policy.getRepositories().get(0).toString());
        assertEquals(TestValues.REPOSITORY_ID_2, policy.getRepositories().get(1).toString());
        assertEquals(TestValues.POLICY_ISDEFAULT, policy.getIsDefault());
    }

    /**
     * Simple verification that Policy model can be converted to JSON
     * @throws Exception
     */
    @Test
    public void testPolicyToJsonConversion() throws Exception {

        Policy policy = createPolicy();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPolicy = objectMapper.writeValueAsString(policy);

        JSONObject root = new JSONObject(jsonPolicy);

        assertEquals(root.getString("@id"),TestValues.POLICY_ID_1);
        assertEquals(root.getString("@type"),"Policy");
        assertEquals(root.getString("title"),TestValues.POLICY_TITLE);
        assertEquals(root.getString("description"),TestValues.POLICY_DESCRIPTION);
        assertEquals(root.getJSONArray("repositories").get(0),TestValues.REPOSITORY_ID_1);
        assertEquals(root.getJSONArray("repositories").get(1),TestValues.REPOSITORY_ID_2);
        assertEquals(root.getBoolean("isDefault"),TestValues.POLICY_ISDEFAULT);         
    }
    
    /**
     * Creates two identical Policys and checks the equals and hashcodes match. 
     * Modifies one field on one of the policys and verifies they no longer are 
     * equal or have matching hashcodes.
     * @throws Exception
     */
    @Test
    public void testPolicyEqualsAndHashCode() throws Exception {

        Policy policy1 = createPolicy();
        Policy policy2 = createPolicy();
        
        assertEquals(policy1,policy2);
        policy1.setIsDefault(!policy1.getIsDefault());
        assertTrue(!policy1.equals(policy2));
        
        assertTrue(policy1.hashCode()!=policy2.hashCode());
        policy1 = policy2;
        assertEquals(policy1.hashCode(),policy2.hashCode());
        
    }
    
    private Policy createPolicy() throws Exception {
        Policy policy = new Policy();
        policy.setId(new URI(TestValues.POLICY_ID_1));
        policy.setTitle(TestValues.POLICY_TITLE);
        policy.setDescription(TestValues.POLICY_DESCRIPTION);
        
        List<URI> repositories = new ArrayList<URI>();
        repositories.add(new URI(TestValues.REPOSITORY_ID_1));
        repositories.add(new URI(TestValues.REPOSITORY_ID_2));
        policy.setRepositories(repositories);

        policy.setIsDefault(TestValues.POLICY_ISDEFAULT);
        
        return policy;
    }
    
}
