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

import org.dataconservancy.pass.model.User.Role;
import org.json.JSONObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Model has been annotated with JSON tags. These tests do a simple check to ensure the
 * Jackson integration is functional and the equals / hashcode functions work
 * @author Karen Hanson
 */
public class UserModelTest {
            
    /**
     * Simple verification that JSON file can be converted to User model
     * @throws Exception
     */
    @Test
    public void testUserFromJsonConversion() throws Exception {
        
        InputStream json = UserModelTest.class.getResourceAsStream("/user.json");
        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.readValue(json, User.class);
        
        assertEquals(TestValues.USER_ID_1, user.getId().toString());
        assertEquals(TestValues.USER_NAME, user.getUsername());
        assertEquals(TestValues.USER_ROLE_1, user.getRoles().get(0).toString());
        assertEquals(TestValues.USER_ROLE_2, user.getRoles().get(1).toString());
        assertEquals(TestValues.USER_FIRST_NAME, user.getFirstName());
        assertEquals(TestValues.USER_MIDDLE_NAME, user.getMiddleName());
        assertEquals(TestValues.USER_LAST_NAME, user.getLastName());
        assertEquals(TestValues.USER_DISPLAY_NAME, user.getDisplayName());
        assertEquals(TestValues.USER_EMAIL, user.getEmail());
        assertEquals(TestValues.USER_AFFILIATION, user.getAffiliation());
        assertEquals(TestValues.USER_INSTITUTIONAL_ID, user.getInstitutionalId());
        assertEquals(TestValues.USER_ORCID_ID, user.getOrcidId());
    }

    /**
     * Simple verification that User model can be converted to JSON
     * @throws Exception
     */
    @Test
    public void testUserToJsonConversion() throws Exception {

        User user = createUser();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUser = objectMapper.writeValueAsString(user);

        JSONObject root = new JSONObject(jsonUser);

        assertEquals(root.getString("@id"),TestValues.USER_ID_1);
        assertEquals(root.getString("@type"),"User");
        assertEquals(root.getString("username"),TestValues.USER_NAME);
        assertEquals(root.getString("firstName"),TestValues.USER_FIRST_NAME);
        assertEquals(root.getString("middleName"),TestValues.USER_MIDDLE_NAME);
        assertEquals(root.getString("lastName"),TestValues.USER_LAST_NAME);
        assertEquals(root.getString("displayName"),TestValues.USER_DISPLAY_NAME);
        assertEquals(root.getString("email"),TestValues.USER_EMAIL);        
        assertEquals(root.getString("affiliation"),TestValues.USER_AFFILIATION);         
        assertEquals(root.getString("institutionalId"),TestValues.USER_INSTITUTIONAL_ID);   
        assertEquals(root.getString("localKey"),TestValues.USER_LOCALKEY);         
        assertEquals(root.getString("orcidId"),TestValues.USER_ORCID_ID);              
        assertEquals(root.getJSONArray("roles").get(0),TestValues.USER_ROLE_1);
        assertEquals(root.getJSONArray("roles").get(1),TestValues.USER_ROLE_2);
    }
    
    /**
     * Creates two identical Users and checks the equals and hashcodes match. 
     * Modifies one field on one of the users and verifies they no longer are 
     * equal or have matching hashcodes.
     * @throws Exception
     */
    @Test
    public void testUserEqualsAndHashCode() throws Exception {

        User user1 = createUser();
        User user2 = createUser();
        
        assertEquals(user1,user2);
        user1.setUsername("different");
        assertTrue(!user1.equals(user2));
        
        assertTrue(user1.hashCode()!=user2.hashCode());
        user1 = user2;
        assertEquals(user1.hashCode(),user2.hashCode());
        
    }
    
    private User createUser() throws Exception {
        User user = new User();
        user.setId(new URI(TestValues.USER_ID_1));
        user.setUsername(TestValues.USER_NAME);
        user.setFirstName(TestValues.USER_FIRST_NAME);
        user.setMiddleName(TestValues.USER_MIDDLE_NAME);
        user.setLastName(TestValues.USER_LAST_NAME);
        user.setDisplayName(TestValues.USER_DISPLAY_NAME);
        user.setEmail(TestValues.USER_EMAIL);
        user.setAffiliation(TestValues.USER_AFFILIATION);
        user.setInstitutionalId(TestValues.USER_INSTITUTIONAL_ID);
        user.setLocalKey(TestValues.USER_LOCALKEY);
        user.setOrcidId(TestValues.USER_ORCID_ID);
        
        List<Role> roles = new ArrayList<Role>();
        roles.add(Role.of(TestValues.USER_ROLE_1));
        roles.add(Role.of(TestValues.USER_ROLE_2));
        user.setRoles(roles);
        
        return user;
    }
    
}
