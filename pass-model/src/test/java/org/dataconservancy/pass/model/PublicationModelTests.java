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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Model has been annotated with JSON tags. These tests do a simple check to ensure the
 * Jackson integration is functional and the equals / hashcode functions work
 * @author Karen Hanson
 */
public class PublicationModelTests {

    /**
     * Simple verification that JSON file can be converted to Publication model
     * @throws Exception
     */
    @Test
    public void testPublicationFromJsonConversion() throws Exception {

        
        InputStream json = PublicationModelTests.class.getResourceAsStream("/publication.json");
        ObjectMapper objectMapper = new ObjectMapper();
        Publication publication = objectMapper.readValue(json, Publication.class);
        
        assertEquals(TestValues.PUBLICATION_ID_1, publication.getId().toString());
        assertEquals(TestValues.PUBLICATION_TITLE, publication.getTitle());
        assertEquals(TestValues.PUBLICATION_ABSTRACT, publication.getPublicationAbstract());
        assertEquals(TestValues.PUBLICATION_DOI, publication.getDoi());
        assertEquals(TestValues.PUBLICATION_PMID, publication.getPmid());
        assertEquals(TestValues.PUBLICATION_VOLUME, publication.getVolume());
        assertEquals(TestValues.PUBLICATION_ISSUE,publication.getIssue());
        assertEquals(TestValues.JOURNAL_ID_1, publication.getJournal().toString());
    }

    /**
     * Simple verification that Publication model can be converted to JSON
     * @throws Exception
     */
    @Test
    public void testPublicationToJsonConversion() throws Exception {

        Publication submission = createPublication();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonSubmission = objectMapper.writeValueAsString(submission);

        JSONObject root = new JSONObject(jsonSubmission);

        assertEquals(root.getString("@id"),TestValues.PUBLICATION_ID_1);
        assertEquals(root.getString("@type"),"Publication");
        assertEquals(root.getString("title"),TestValues.PUBLICATION_TITLE);
        assertEquals(root.getString("abstract"),TestValues.PUBLICATION_ABSTRACT);
        assertEquals(root.getString("doi"),TestValues.PUBLICATION_DOI);
        assertEquals(root.getString("pmid"),TestValues.PUBLICATION_PMID);
        assertEquals(root.getString("volume"),TestValues.PUBLICATION_VOLUME);
        assertEquals(root.getString("issue"),TestValues.PUBLICATION_ISSUE);
        assertEquals(root.getString("journal"),TestValues.JOURNAL_ID_1);
    }
    
    /**
     * Creates two identical Publication and checks the equals and hashcodes match. 
     * Modifies one field on one of the publications and verifies they no longer are 
     * equal or have matching hashcodes.
     * @throws Exception
     */
    @Test
    public void testSubmissionEqualsAndHashCode() throws Exception {
        Publication publication1 = createPublication();
        Publication publication2 = createPublication();
        
        assertEquals(publication1,publication2);
        publication1.setIssue("different");
        assertTrue(!publication1.equals(publication2));
        
        assertTrue(publication1.hashCode()!=publication2.hashCode());
        publication1 = publication2;
        assertEquals(publication1.hashCode(),publication2.hashCode());
        
    }
    
    /**
     * Test copy constructor creates a valid duplicate that is not the same object
     * @throws Exception
     */
    @Test
    public void testPublicationCopyConstructor() throws Exception {
        Publication publication = createPublication();        
        Publication publicationCopy = new Publication(publication);
        assertEquals(publication, publicationCopy);
        
        String newDoi ="different:doi";
        publicationCopy.setDoi(newDoi);
        assertEquals(TestValues.PUBLICATION_DOI, publication.getDoi());
        assertEquals(newDoi, publicationCopy.getDoi());

        String newVersionTag ="abcdef";
        publicationCopy.setVersionTag(newVersionTag);
        assertNull(publication.getVersionTag());
        assertEquals(newVersionTag, publicationCopy.getVersionTag());
    }
    
    private Publication createPublication() throws Exception {
        Publication publication = new Publication();
        publication.setId(new URI(TestValues.PUBLICATION_ID_1));
        publication.setTitle(TestValues.PUBLICATION_TITLE);
        publication.setPublicationAbstract(TestValues.PUBLICATION_ABSTRACT);
        publication.setDoi(TestValues.PUBLICATION_DOI);
        publication.setPmid(TestValues.PUBLICATION_PMID);
        publication.setVolume(TestValues.PUBLICATION_VOLUME);
        publication.setIssue(TestValues.PUBLICATION_ISSUE);
        publication.setJournal(new URI(TestValues.JOURNAL_ID_1));
        return publication;
    }
    
}
