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
        assertEquals("User", user.getType());
        assertEquals(TestValues.USER_NAME, user.getUsername());
        assertEquals(TestValues.USER_ROLE, user.getRole().getValue());
        assertEquals(TestValues.PERSON_ID_1, user.getPerson().toString());
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
        assertEquals(root.getString("role"),TestValues.USER_ROLE);
        assertEquals(root.getString("person"),TestValues.PERSON_ID_1);        
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
        user.setRole(Role.of(TestValues.USER_ROLE));
        user.setPerson(new URI(TestValues.PERSON_ID_1));
        
        return user;
    }
    
}
