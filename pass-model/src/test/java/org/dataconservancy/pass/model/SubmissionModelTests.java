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

import org.dataconservancy.pass.model.Submission.AggregatedDepositStatus;
import org.dataconservancy.pass.model.Submission.Source;
import org.dataconservancy.pass.model.Submission.SubmissionStatus;
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
        assertEquals(TestValues.SUBMISSION_METADATA, submission.getMetadata());
        assertEquals(TestValues.SUBMISSION_SUBMITTED, submission.getSubmitted());
        assertEquals(TestValues.SUBMISSION_STATUS, submission.getSubmissionStatus().toString());
        assertEquals(TestValues.SUBMISSION_AGG_DEPOSIT_STATUS, submission.getAggregatedDepositStatus().toString());
        assertEquals(TestValues.PUBLICATION_ID_1, submission.getPublication().toString());
        assertEquals(TestValues.USER_ID_1, submission.getSubmitter().toString());
        assertEquals(TestValues.USER_ID_2, submission.getPreparers().get(0).toString());
        assertEquals(TestValues.REPOSITORY_ID_1, submission.getRepositories().get(0).toString());
        assertEquals(TestValues.REPOSITORY_ID_2, submission.getRepositories().get(1).toString());
        assertEquals(TestValues.GRANT_ID_1, submission.getGrants().get(0).toString());
        assertEquals(TestValues.GRANT_ID_2, submission.getGrants().get(1).toString());
        assertEquals(TestValues.SUBMISSION_DATE_STR, dateFormatter.print(submission.getSubmittedDate()));
        assertEquals(TestValues.SUBMISSION_SOURCE, submission.getSource().toString());
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
        assertEquals(root.getString("submissionStatus"),TestValues.SUBMISSION_STATUS);
        assertEquals(root.getString("aggregatedDepositStatus"),TestValues.SUBMISSION_AGG_DEPOSIT_STATUS);
        assertEquals(root.getString("publication"),TestValues.PUBLICATION_ID_1);
        assertEquals(root.getString("submitter"),TestValues.USER_ID_1);
        assertEquals(root.getJSONArray("preparers").get(0),TestValues.USER_ID_2);
        assertEquals(root.getBoolean("submitted"),TestValues.SUBMISSION_SUBMITTED);
        assertEquals(root.getString("metadata"),TestValues.SUBMISSION_METADATA);
        assertEquals(root.getJSONArray("repositories").get(0),TestValues.REPOSITORY_ID_1);
        assertEquals(root.getJSONArray("repositories").get(1),TestValues.REPOSITORY_ID_2);
        assertEquals(root.getJSONArray("grants").get(0),TestValues.GRANT_ID_1);
        assertEquals(root.getJSONArray("grants").get(1),TestValues.GRANT_ID_2);
        assertEquals(root.getString("submittedDate"),TestValues.SUBMISSION_DATE_STR);    
        assertEquals(root.getString("source"), TestValues.SUBMISSION_SOURCE);
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
        submission1.setSubmissionStatus(Submission.SubmissionStatus.CANCELLED);
        assertTrue(!submission1.equals(submission2));
        
        assertTrue(submission1.hashCode()!=submission2.hashCode());
        submission1 = submission2;
        assertEquals(submission1.hashCode(),submission2.hashCode());
        
    }
    
    private Submission createSubmission() throws Exception {
        Submission submission = new Submission();
        submission.setId(new URI(TestValues.SUBMISSION_ID_1));
        submission.setSubmissionStatus(SubmissionStatus.of(TestValues.SUBMISSION_STATUS));
        submission.setAggregatedDepositStatus(AggregatedDepositStatus.of(TestValues.SUBMISSION_AGG_DEPOSIT_STATUS));
        submission.setMetadata(TestValues.SUBMISSION_METADATA);
        submission.setSubmitted(TestValues.SUBMISSION_SUBMITTED);
        submission.setPublication(new URI(TestValues.PUBLICATION_ID_1));
        submission.setSubmitter(new URI(TestValues.USER_ID_1));
        submission.setSource(Source.PASS);
        
        List<URI> preparers = new ArrayList<URI>();
        preparers.add(new URI(TestValues.USER_ID_2));
        submission.setPreparers(preparers);
        
        List<URI> repositories = new ArrayList<URI>();
        repositories.add(new URI(TestValues.REPOSITORY_ID_1));
        repositories.add(new URI(TestValues.REPOSITORY_ID_2));
        submission.setRepositories(repositories);

        List<URI> grants = new ArrayList<URI>();
        grants.add(new URI(TestValues.GRANT_ID_1));
        grants.add(new URI(TestValues.GRANT_ID_2));
        submission.setGrants(grants);

        DateTime dt = dateFormatter.parseDateTime(TestValues.SUBMISSION_DATE_STR);
        submission.setSubmittedDate(dt);
        
        return submission;
    }
    
}
