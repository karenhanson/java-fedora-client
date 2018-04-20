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

import org.dataconservancy.pass.model.RepositoryCopy.CopyStatus;
import org.json.JSONObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Model has been annotated with JSON tags. These tests do a simple check to ensure the
 * Jackson integration is functional and the equals / hashcode functions work
 * @author Karen Hanson
 */
public class RepositoryCopyModelTests {
            
    /**
     * Simple verification that JSON file can be converted to RepositoryCopy model
     * @throws Exception
     */
    @Test
    public void testRepositoryCopyFromJsonConversion() throws Exception {
        
        InputStream json = RepositoryCopyModelTests.class.getResourceAsStream("/repositorycopy.json");
        ObjectMapper objectMapper = new ObjectMapper();
        RepositoryCopy repositoryCopy = objectMapper.readValue(json, RepositoryCopy.class);
        
        assertEquals(TestValues.REPOSITORYCOPY_ID_1, repositoryCopy.getId().toString());
        assertEquals("RepositoryCopy", repositoryCopy.getType());
        assertEquals(CopyStatus.of(TestValues.REPOSITORYCOPY_STATUS), repositoryCopy.getCopyStatus());
        assertEquals(TestValues.REPOSITORYCOPY_EXTERNALID_1, repositoryCopy.getExternalIds().get(0));
        assertEquals(TestValues.REPOSITORYCOPY_EXTERNALID_2, repositoryCopy.getExternalIds().get(1));
        assertEquals(TestValues.REPOSITORYCOPY_ACCESSURL, repositoryCopy.getAccessUrl().toString());
        assertEquals(TestValues.PUBLICATION_ID_1, repositoryCopy.getPublication().toString());
        assertEquals(TestValues.REPOSITORY_ID_1, repositoryCopy.getRepository().toString());
        
    }

    /**
     * Simple verification that RepositoryCopy model can be converted to JSON
     * @throws Exception
     */
    @Test
    public void testRepositoryCopyToJsonConversion() throws Exception {

        RepositoryCopy repositoryCopy = createRepositoryCopy();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRepositoryCopy = objectMapper.writeValueAsString(repositoryCopy);

        JSONObject root = new JSONObject(jsonRepositoryCopy);

        assertEquals(root.getString("@id"),TestValues.REPOSITORYCOPY_ID_1);
        assertEquals(root.getString("@type"),"RepositoryCopy");
        assertEquals(root.getJSONArray("externalIds").get(0),TestValues.REPOSITORYCOPY_EXTERNALID_1);
        assertEquals(root.getJSONArray("externalIds").get(1),TestValues.REPOSITORYCOPY_EXTERNALID_2);
        assertEquals(root.getString("copyStatus"),TestValues.REPOSITORYCOPY_STATUS);
        assertEquals(root.getString("accessUrl"),TestValues.REPOSITORYCOPY_ACCESSURL);
        assertEquals(root.getString("publication"),TestValues.PUBLICATION_ID_1); 
        assertEquals(root.getString("repository"),TestValues.REPOSITORY_ID_1);
    }
    
    /**
     * Creates two identical RepositoryCopys and checks the equals and hashcodes match. 
     * Modifies one field on one of the RepositoryCopies and verifies they no longer are 
     * equal or have matching hashcodes.
     * @throws Exception
     */
    @Test
    public void testRepositoryCopyEqualsAndHashCode() throws Exception {

        RepositoryCopy repoCopy1 = createRepositoryCopy();
        RepositoryCopy repoCopy2 = createRepositoryCopy();
        
        assertEquals(repoCopy1,repoCopy2);
        repoCopy1.setRepository(new URI(TestValues.REPOSITORY_ID_2));
        assertTrue(!repoCopy1.equals(repoCopy2));
        
        assertTrue(repoCopy1.hashCode()!=repoCopy2.hashCode());
        repoCopy1 = repoCopy2;
        assertEquals(repoCopy1.hashCode(),repoCopy2.hashCode());
        
    }
    
    private RepositoryCopy createRepositoryCopy() throws Exception {
        RepositoryCopy repositoryCopy = new RepositoryCopy();
        repositoryCopy.setId(new URI(TestValues.REPOSITORYCOPY_ID_1));
        repositoryCopy.setCopyStatus(CopyStatus.of(TestValues.REPOSITORYCOPY_STATUS));
        repositoryCopy.setAccessUrl(new URI(TestValues.REPOSITORYCOPY_ACCESSURL));
        repositoryCopy.setPublication(new URI(TestValues.PUBLICATION_ID_1));
        repositoryCopy.setRepository(new URI(TestValues.REPOSITORY_ID_1));

        List<String> externalIds = new ArrayList<String>();
        externalIds.add(TestValues.REPOSITORYCOPY_EXTERNALID_1);
        externalIds.add(TestValues.REPOSITORYCOPY_EXTERNALID_2);        
        repositoryCopy.setExternalIds(externalIds);
        
        return repositoryCopy;
    }
    
}
