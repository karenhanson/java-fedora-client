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

import org.dataconservancy.pass.model.File.FileRole;
import org.json.JSONObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Model has been annotated with JSON tags. These tests do a simple check to ensure the
 * Jackson integration is functional and the equals / hashcode functions work
 * @author Karen Hanson
 */
public class FileModelTests {
            
    /**
     * Simple verification that JSON file can be converted to File model
     * @throws Exception
     */
    @Test
    public void testFileFromJsonConversion() throws Exception {
        
        InputStream json = FileModelTests.class.getResourceAsStream("/file.json");
        ObjectMapper objectMapper = new ObjectMapper();
        File file = objectMapper.readValue(json, File.class);
        
        assertEquals(TestValues.FILE_ID_1, file.getId().toString());
        assertEquals("File", file.getType());
        assertEquals(TestValues.FILE_NAME, file.getName());
        assertEquals(TestValues.FILE_URI, file.getUri().toString());
        assertEquals(TestValues.FILE_DESCRIPTION, file.getDescription());
        assertEquals(TestValues.FILE_ROLE, file.getFileRole().getValue());
        assertEquals(TestValues.FILE_MIMETYPE, file.getMimeType());
        assertEquals(TestValues.SUBMISSION_ID_1, file.getSubmission().toString());
    }

    /**
     * Simple verification that File model can be converted to JSON
     * @throws Exception
     */
    @Test
    public void testFileToJsonConversion() throws Exception {

        File file= createFile();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonFile = objectMapper.writeValueAsString(file);

        JSONObject root = new JSONObject(jsonFile);

        assertEquals(root.getString("@id"),TestValues.FILE_ID_1);
        assertEquals(root.getString("@type"), PassEntityType.FILE.getName());
        assertEquals(root.getString("name"),TestValues.FILE_NAME);
        assertEquals(root.getString("uri"),TestValues.FILE_URI);
        assertEquals(root.getString("description"),TestValues.FILE_DESCRIPTION);
        assertEquals(root.getString("fileRole"),TestValues.FILE_ROLE);
        assertEquals(root.getString("mimeType"),TestValues.FILE_MIMETYPE);            
        assertEquals(root.getString("submission"),TestValues.SUBMISSION_ID_1);            
    }
    
    /**
     * Creates two identical Files and checks the equals and hashcodes match. 
     * Modifies one field on one of the Files and verifies they no longer are 
     * equal or have matching hashcodes.
     * @throws Exception
     */
    @Test
    public void testFileEqualsAndHashCode() throws Exception {

        File file1 = createFile();
        File file2 = createFile();
        
        assertEquals(file1,file2);
        file1.setDescription("different");
        assertTrue(!file1.equals(file2));
        
        assertTrue(file1.hashCode()!=file2.hashCode());
        file1 = file2;
        assertEquals(file1.hashCode(),file2.hashCode());
        
    }
    
    private File createFile() throws Exception {
        File file = new File();
        file.setId(new URI(TestValues.FILE_ID_1));
        file.setName(TestValues.FILE_NAME);
        file.setUri(new URI(TestValues.FILE_URI));
        file.setDescription(TestValues.FILE_DESCRIPTION);
        file.setFileRole(FileRole.of(TestValues.FILE_ROLE));
        file.setMimeType(TestValues.FILE_MIMETYPE);
        file.setSubmission(new URI(TestValues.SUBMISSION_ID_1));
        
        return file;
    }
    
}
