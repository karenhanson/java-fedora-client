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
public class RepositoryModelTests {
            
    /**
     * Simple verification that JSON file can be converted to Repository model
     * @throws Exception
     */
    @Test
    public void testRepositoryFromJsonConversion() throws Exception {
        
        InputStream json = RepositoryModelTests.class.getResourceAsStream("/repository.json");
        ObjectMapper objectMapper = new ObjectMapper();
        Repository repository = objectMapper.readValue(json, Repository.class);
        
        assertEquals(TestValues.REPOSITORY_ID_1, repository.getId().toString());
        assertEquals(TestValues.REPOSITORY_NAME, repository.getName());
        assertEquals(TestValues.REPOSITORY_DESCRIPTION, repository.getDescription());
        assertEquals(TestValues.REPOSITORY_URL, repository.getUrl().toString());
        assertEquals(TestValues.REPOSITORY_FORMSCHEMA, repository.getFormSchema());
    }

    /**
     * Simple verification that Repository model can be converted to JSON
     * @throws Exception
     */
    @Test
    public void testRepositoryToJsonConversion() throws Exception {

        Repository repository = createRepository();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRepository = objectMapper.writeValueAsString(repository);

        JSONObject root = new JSONObject(jsonRepository);

        assertEquals(root.getString("@id"),TestValues.REPOSITORY_ID_1);
        assertEquals(root.getString("@type"),"Repository");
        assertEquals(root.getString("name"),TestValues.REPOSITORY_NAME);
        assertEquals(root.getString("description"),TestValues.REPOSITORY_DESCRIPTION);
        assertEquals(root.getString("url"),TestValues.REPOSITORY_URL);        
        assertEquals(root.getString("formSchema"),TestValues.REPOSITORY_FORMSCHEMA);          
    }
    
    /**
     * Creates two identical Repositorys and checks the equals and hashcodes match. 
     * Modifies one field on one of the repositorys and verifies they no longer are 
     * equal or have matching hashcodes.
     * @throws Exception
     */
    @Test
    public void testRepositoryEqualsAndHashCode() throws Exception {

        Repository repository1 = createRepository();
        Repository repository2 = createRepository();
        
        assertEquals(repository1,repository2);
        repository1.setName("different");
        assertTrue(!repository1.equals(repository2));
        
        assertTrue(repository1.hashCode()!=repository2.hashCode());
        repository1 = repository2;
        assertEquals(repository1.hashCode(),repository2.hashCode());
        
    }
    
    private Repository createRepository() throws Exception {
        Repository repository = new Repository();
        repository.setId(new URI(TestValues.REPOSITORY_ID_1));
        repository.setName(TestValues.REPOSITORY_NAME);
        repository.setDescription(TestValues.REPOSITORY_DESCRIPTION);
        repository.setUrl(new URI(TestValues.REPOSITORY_URL));
        repository.setFormSchema(TestValues.REPOSITORY_FORMSCHEMA);
        
        return repository;
    }
    
}
