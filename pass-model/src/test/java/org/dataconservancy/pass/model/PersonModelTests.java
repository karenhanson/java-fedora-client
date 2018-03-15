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
public class PersonModelTests {
            
    /**
     * Simple verification that JSON file can be converted to Person model
     * @throws Exception
     */
    @Test
    public void testPersonFromJsonConversion() throws Exception {
        
        InputStream json = PersonModelTests.class.getResourceAsStream("/person.json");
        ObjectMapper objectMapper = new ObjectMapper();
        Person person = objectMapper.readValue(json, Person.class);
        
        assertEquals(TestValues.PERSON_ID_1, person.getId().toString());
        assertEquals("Person", person.getType());
        assertEquals(TestValues.PERSON_FIRST_NAME, person.getFirstName());
        assertEquals(TestValues.PERSON_MIDDLE_NAME, person.getMiddleName());
        assertEquals(TestValues.PERSON_LAST_NAME, person.getLastName());
        assertEquals(TestValues.PERSON_DISPLAY_NAME, person.getDisplayName());
        assertEquals(TestValues.PERSON_EMAIL, person.getEmail());
        assertEquals(TestValues.PERSON_INSTITUTIONAL_ID, person.getInstitutionalId());
        assertEquals(TestValues.PERSON_ORCID_ID, person.getOrcidId());
        assertEquals(TestValues.PERSON_AFFILIATION, person.getAffiliation());
    }

    /**
     * Simple verification that Person model can be converted to JSON
     * @throws Exception
     */
    @Test
    public void testPersonToJsonConversion() throws Exception {

        Person person = createPerson();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPerson = objectMapper.writeValueAsString(person);

        JSONObject root = new JSONObject(jsonPerson);

        assertEquals(root.getString("@id"),TestValues.PERSON_ID_1);
        assertEquals(root.getString("@type"),"Person");
        assertEquals(root.getString("firstName"),TestValues.PERSON_FIRST_NAME);
        assertEquals(root.getString("middleName"),TestValues.PERSON_MIDDLE_NAME);
        assertEquals(root.getString("lastName"),TestValues.PERSON_LAST_NAME);
        assertEquals(root.getString("displayName"),TestValues.PERSON_DISPLAY_NAME);
        assertEquals(root.getString("email"),TestValues.PERSON_EMAIL);        
        assertEquals(root.getString("institutionalId"),TestValues.PERSON_INSTITUTIONAL_ID);        
        assertEquals(root.getString("orcidId"),TestValues.PERSON_ORCID_ID);                
        assertEquals(root.getString("affiliation"),TestValues.PERSON_AFFILIATION);         
    }
    
    /**
     * Creates two identical Persons and checks the equals and hashcodes match. 
     * Modifies one field on one of the persons and verifies they no longer are 
     * equal or have matching hashcodes.
     * @throws Exception
     */
    @Test
    public void testPersonEqualsAndHashCode() throws Exception {

        Person person1 = createPerson();
        Person person2 = createPerson();
        
        assertEquals(person1,person2);
        person1.setFirstName("different");
        assertTrue(!person1.equals(person2));
        
        assertTrue(person1.hashCode()!=person2.hashCode());
        person1 = person2;
        assertEquals(person1.hashCode(),person2.hashCode());
        
    }
    
    private Person createPerson() throws Exception {
        Person person = new Person();
        person.setId(new URI(TestValues.PERSON_ID_1));
        person.setFirstName(TestValues.PERSON_FIRST_NAME);
        person.setMiddleName(TestValues.PERSON_MIDDLE_NAME);
        person.setLastName(TestValues.PERSON_LAST_NAME);
        person.setDisplayName(TestValues.PERSON_DISPLAY_NAME);
        person.setEmail(TestValues.PERSON_EMAIL);
        person.setInstitutionalId(TestValues.PERSON_INSTITUTIONAL_ID);
        person.setOrcidId(TestValues.PERSON_ORCID_ID);
        person.setAffiliation(TestValues.PERSON_AFFILIATION);
        
        return person;
    }
    
}
