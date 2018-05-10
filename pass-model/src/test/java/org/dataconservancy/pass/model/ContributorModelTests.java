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
public class ContributorModelTests {
            
    /**
     * Simple verification that JSON file can be converted to Contributor model
     * @throws Exception
     */
    @Test
    public void testContributorFromJsonConversion() throws Exception {
        
        InputStream json = ContributorModelTests.class.getResourceAsStream("/contributor.json");
        ObjectMapper objectMapper = new ObjectMapper();
        Contributor contributor = objectMapper.readValue(json, Contributor.class);
        
        assertEquals(TestValues.CONTRIBUTOR_ID_1, contributor.getId().toString());
        assertEquals(TestValues.USER_FIRST_NAME, contributor.getFirstName());
        assertEquals(TestValues.USER_MIDDLE_NAME, contributor.getMiddleName());
        assertEquals(TestValues.USER_LAST_NAME, contributor.getLastName());
        assertEquals(TestValues.USER_DISPLAY_NAME, contributor.getDisplayName());
        assertEquals(TestValues.USER_EMAIL, contributor.getEmail());
        assertEquals(TestValues.USER_ORCID_ID, contributor.getOrcidId());
        assertEquals(TestValues.USER_AFFILIATION, contributor.getAffiliation());
        assertEquals(TestValues.PUBLICATION_ID_1, contributor.getPublication().toString());
        assertEquals(TestValues.USER_ID_1, contributor.getUser().toString());
        assertEquals(TestValues.CONTRIBUTOR_ROLE_1, Contributor.Role.FIRST_AUTHOR.toString());
        assertEquals(TestValues.CONTRIBUTOR_ROLE_2, Contributor.Role.AUTHOR.toString());       
        
    }

    /**
     * Simple verification that Contributor model can be converted to JSON
     * @throws Exception
     */
    @Test
    public void testContributorToJsonConversion() throws Exception {

        Contributor contributor= createContributor();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContributor = objectMapper.writeValueAsString(contributor);

        JSONObject root = new JSONObject(jsonContributor);

        assertEquals(root.getString("@id"),TestValues.CONTRIBUTOR_ID_1);
        assertEquals(root.getString("@type"), PassEntityType.CONTRIBUTOR.getName());
        assertEquals(root.getString("firstName"),TestValues.USER_FIRST_NAME);
        assertEquals(root.getString("middleName"),TestValues.USER_MIDDLE_NAME);
        assertEquals(root.getString("lastName"),TestValues.USER_LAST_NAME);
        assertEquals(root.getString("displayName"),TestValues.USER_DISPLAY_NAME);
        assertEquals(root.getString("email"),TestValues.USER_EMAIL);            
        assertEquals(root.getString("orcidId"),TestValues.USER_ORCID_ID);                
        assertEquals(root.getString("affiliation"),TestValues.USER_AFFILIATION);         
    }
    
    /**
     * Creates two identical Contributors and checks the equals and hashcodes match. 
     * Modifies one field on one of the Contributors and verifies they no longer are 
     * equal or have matching hashcodes.
     * @throws Exception
     */
    @Test
    public void testContributorEqualsAndHashCode() throws Exception {

        Contributor contributor1 = createContributor();
        Contributor contributor2 = createContributor();
        
        assertEquals(contributor1,contributor2);
        contributor1.setFirstName("different");
        assertTrue(!contributor1.equals(contributor2));
        
        assertTrue(contributor1.hashCode()!=contributor2.hashCode());
        contributor1 = contributor2;
        assertEquals(contributor1.hashCode(),contributor2.hashCode());
        
    }
    
    private Contributor createContributor() throws Exception {
        Contributor contributor = new Contributor();
        contributor.setId(new URI(TestValues.CONTRIBUTOR_ID_1));
        contributor.setFirstName(TestValues.USER_FIRST_NAME);
        contributor.setMiddleName(TestValues.USER_MIDDLE_NAME);
        contributor.setLastName(TestValues.USER_LAST_NAME);
        contributor.setDisplayName(TestValues.USER_DISPLAY_NAME);
        contributor.setEmail(TestValues.USER_EMAIL);
        contributor.setOrcidId(TestValues.USER_ORCID_ID);
        contributor.setAffiliation(TestValues.USER_AFFILIATION);
        contributor.setUser(new URI(TestValues.USER_ID_1));
        contributor.setPublication(new URI(TestValues.PUBLICATION_ID_1));
        
        return contributor;
    }
    
}
