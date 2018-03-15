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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Model has been annotated with JSON tags. These tests do a simple check to ensure the
 * Jackson integration is functional and the equals / hashcode functions work
 * @author Karen Hanson
 */
public class SubmissionModelTests {

    private DateTimeFormatter dateFormatter = ISODateTimeFormat.dateTime().withZoneUTC();
    
    /**
     * Simple verification that JSON file can be converted to Submission model
     * @throws Exception
     */
    @Test
    public void testSubmissionFromJsonConversion() throws Exception {

        
        InputStream json = SubmissionModelTests.class.getResourceAsStream("/submission.json");
        ObjectMapper objectMapper = new ObjectMapper();
        Submission submission = objectMapper.readValue(json, Submission.class);
        
        assertEquals(TestValues.SUBMISSION_ID_1, submission.getId().toString());
        assertEquals("Submission", submission.getType());
        assertEquals(TestValues.SUBMISSION_STATUS, submission.getStatus());
        assertEquals(TestValues.SUBMISSION_TITLE, submission.getTitle());
        assertEquals(TestValues.PERSON_ID_1, submission.getAuthors().get(0).toString());
        assertEquals(TestValues.PERSON_ID_2, submission.getAuthors().get(1).toString());
        assertEquals(TestValues.SUBMISSION_ABSTRACT, submission.getSubmissionAbstract());
        assertEquals(TestValues.SUBMISSION_DOI, submission.getDoi());
        assertEquals(TestValues.JOURNAL_ID_1, submission.getJournal().toString());
        assertEquals(TestValues.SUBMISSION_VOLUME, submission.getVolume());
        assertEquals(TestValues.SUBMISSION_ISSUE,submission.getIssue());
        assertEquals(TestValues.DEPOSIT_ID_1, submission.getDeposits().get(0).toString());
        assertEquals(TestValues.DEPOSIT_ID_2, submission.getDeposits().get(1).toString());
        assertEquals(TestValues.GRANT_ID_1, submission.getGrants().get(0).toString());
        assertEquals(TestValues.GRANT_ID_2, submission.getGrants().get(1).toString());
        assertEquals(TestValues.WORKFLOW_ID_1, submission.getWorkflows().get(0).toString());
        assertEquals(TestValues.SUBMISSION_DATE_STR, dateFormatter.print(submission.getSubmittedDate()));
    }

    /**
     * Simple verification that Submission model can be converted to JSON
     * @throws Exception
     */
    @Test
    public void testSubmissionToJsonConversion() throws Exception {

        Submission submission = createSubmission();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonSubmission = objectMapper.writeValueAsString(submission);

        JSONObject root = new JSONObject(jsonSubmission);

        assertEquals(root.getString("@id"),TestValues.SUBMISSION_ID_1);
        assertEquals(root.getString("@type"),"Submission");
        assertEquals(root.getString("status"),TestValues.SUBMISSION_STATUS.getValue());
        assertEquals(root.getString("title"),TestValues.SUBMISSION_TITLE);
        assertEquals(root.getJSONArray("authors").get(0),TestValues.PERSON_ID_1);
        assertEquals(root.getJSONArray("authors").get(1),TestValues.PERSON_ID_2);
        assertEquals(root.getString("abstract"),TestValues.SUBMISSION_ABSTRACT);
        assertEquals(root.getString("doi"),TestValues.SUBMISSION_DOI);
        assertEquals(root.getString("journal"),TestValues.JOURNAL_ID_1);
        assertEquals(root.getString("volume"),TestValues.SUBMISSION_VOLUME);
        assertEquals(root.getString("issue"),TestValues.SUBMISSION_ISSUE);
        assertEquals(root.getJSONArray("deposits").get(0),TestValues.DEPOSIT_ID_1);
        assertEquals(root.getJSONArray("deposits").get(1),TestValues.DEPOSIT_ID_2);
        assertEquals(root.getJSONArray("grants").get(0),TestValues.GRANT_ID_1);
        assertEquals(root.getJSONArray("grants").get(1),TestValues.GRANT_ID_2);
        assertEquals(root.getJSONArray("workflows").get(0),TestValues.WORKFLOW_ID_1);
        assertEquals(root.getString("submittedDate"),TestValues.SUBMISSION_DATE_STR);    
    }
    
    /**
     * Creates two identical Submissions and checks the equals and hashcodes match. 
     * Modifies one field on one of the submissions and verifies they no longer are 
     * equal or have matching hashcodes.
     * @throws Exception
     */
    @Test
    public void testSubmissionEqualsAndHashCode() throws Exception {

        Submission submission1 = createSubmission();
        Submission submission2 = createSubmission();
        
        assertEquals(submission1,submission2);
        submission1.setIssue("different");
        assertTrue(!submission1.equals(submission2));
        
        assertTrue(submission1.hashCode()!=submission2.hashCode());
        submission1 = submission2;
        assertEquals(submission1.hashCode(),submission2.hashCode());
        
    }
    
    private Submission createSubmission() throws Exception {
        Submission submission = new Submission();
        submission.setId(new URI(TestValues.SUBMISSION_ID_1));
        submission.setStatus(TestValues.SUBMISSION_STATUS);
        submission.setTitle(TestValues.SUBMISSION_TITLE);
        
        List<URI> authors = new ArrayList<URI>();
        authors.add(new URI(TestValues.PERSON_ID_1));
        authors.add(new URI(TestValues.PERSON_ID_2));
        submission.setAuthors(authors);
        
        submission.setSubmissionAbstract(TestValues.SUBMISSION_ABSTRACT);
        submission.setDoi(TestValues.SUBMISSION_DOI);
        submission.setJournal(new URI(TestValues.JOURNAL_ID_1));
        submission.setVolume(TestValues.SUBMISSION_VOLUME);
        submission.setIssue(TestValues.SUBMISSION_ISSUE);
        
        List<URI> deposits = new ArrayList<URI>();
        deposits.add(new URI(TestValues.DEPOSIT_ID_1));
        deposits.add(new URI(TestValues.DEPOSIT_ID_2));
        submission.setDeposits(deposits);

        List<URI> grants = new ArrayList<URI>();
        grants.add(new URI(TestValues.GRANT_ID_1));
        grants.add(new URI(TestValues.GRANT_ID_2));
        submission.setGrants(grants);

        List<URI> workflows = new ArrayList<URI>();
        workflows.add(new URI(TestValues.WORKFLOW_ID_1));
        submission.setWorkflows(workflows);

        DateTime dt = dateFormatter.parseDateTime(TestValues.SUBMISSION_DATE_STR);
        submission.setSubmittedDate(dt);
        
        return submission;
    }
    
}
