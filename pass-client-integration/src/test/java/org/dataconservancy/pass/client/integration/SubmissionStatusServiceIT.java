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
package org.dataconservancy.pass.client.integration;

import java.net.URI;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import org.dataconservancy.pass.client.SubmissionStatusService;
import org.dataconservancy.pass.model.Deposit;
import org.dataconservancy.pass.model.Deposit.DepositStatus;
import org.dataconservancy.pass.model.RepositoryCopy;
import org.dataconservancy.pass.model.RepositoryCopy.CopyStatus;
import org.dataconservancy.pass.model.Submission;
import org.dataconservancy.pass.model.Submission.SubmissionStatus;
import org.dataconservancy.pass.model.SubmissionEvent;
import org.dataconservancy.pass.model.SubmissionEvent.EventType;
import org.joda.time.DateTime;

import static org.junit.Assert.assertEquals;

/**
 * Integration tests for Submission Status Service.
 * @author Karen Hanson
 */
public class SubmissionStatusServiceIT extends ClientITBase {

    //some test URIs 
    private URI repo1Id;
    private URI repo2Id;
    private URI publicationId;
    
    @Before
    public void createDefaultTestData() throws Exception {

        repo1Id = new URI("repository:1");
        repo2Id = new URI("repository:2");
        publicationId = new URI("publication:1");
        
    }
    
    
    /**
     * Make sure that if the status is null when the calculateAndUpdateSubmissionStatus is run,
     * null gets replaced with a value.
     */
    @Test
    public void testSetPreSubmissionStatusWhenNull() {
        
        // create a random Submission, but add important values for this test
        Submission submission = random(Submission.class, 1);
        submission.setSubmitted(false);
        submission.setRepositories(Arrays.asList(repo1Id, repo2Id));
        submission.setPublication(publicationId);
        submission.setSubmissionStatus(null);
        URI submissionId = client.createResource(submission);
        this.createdUris.put(submissionId, Submission.class);
        
        // add a couple of events that will be used to calculate status
        SubmissionEvent subEvent1 = random(SubmissionEvent.class, 1);
        subEvent1.setEventType(EventType.APPROVAL_REQUESTED);
        subEvent1.setPerformedDate(new DateTime(2018, 2, 1, 12, 1, 0, 0));
        subEvent1.setSubmission(submissionId);
        URI subEvent1Id = client.createResource(subEvent1);
        this.createdUris.put(subEvent1Id, SubmissionEvent.class);
        
        SubmissionEvent subEvent2 = random(SubmissionEvent.class, 1);
        subEvent2.setEventType(EventType.CANCELLED);
        subEvent2.setPerformedDate(new DateTime(2018, 2, 1, 14, 1, 0, 0));
        subEvent2.setSubmission(submissionId);
        URI subEvent2Id = client.createResource(subEvent2);
        this.createdUris.put(subEvent2Id, SubmissionEvent.class);
        
        //wait for index
        attempt(RETRIES, () -> {
            final URI uri = client.findByAttribute(SubmissionEvent.class, "@id", subEvent2Id);
            assertEquals(subEvent2Id, uri);
        }); 

        SubmissionStatusService service = new SubmissionStatusService(submissionId);
        SubmissionStatus newStatus = service.calculateAndUpdateSubmissionStatus();
        //check correct value returned
        assertEquals(SubmissionStatus.CANCELLED, newStatus);
        
        //also check database updated
        submission = client.readResource(submissionId, Submission.class);
        assertEquals(SubmissionStatus.CANCELLED, submission.getSubmissionStatus());
        
    }

    /**
     * Make sure that if the status is pre-submission and has a value it does not override status value
     */
    @Test
    public void testDoNotOverridePreSubmissionStatusWhenHasValue() {
        
        // create a random Submission, but add important values for this test
        Submission submission = random(Submission.class, 1);
        submission.setSubmitted(false);
        submission.setRepositories(Arrays.asList(repo1Id, repo2Id));
        submission.setPublication(publicationId);
        submission.setSubmissionStatus(SubmissionStatus.APPROVAL_REQUESTED);
        URI submissionId = client.createResource(submission);
        this.createdUris.put(submissionId, Submission.class);
        
        // add a couple of events that will be used to calculate status
        SubmissionEvent subEvent1 = random(SubmissionEvent.class, 1);
        subEvent1.setEventType(EventType.APPROVAL_REQUESTED);
        subEvent1.setPerformedDate(new DateTime(2018, 2, 1, 12, 1, 0, 0));
        subEvent1.setSubmission(submissionId);
        URI subEvent1Id = client.createResource(subEvent1);
        this.createdUris.put(subEvent1Id, SubmissionEvent.class);
        
        SubmissionEvent subEvent2 = random(SubmissionEvent.class, 1);
        subEvent2.setEventType(EventType.CANCELLED);
        subEvent2.setPerformedDate(new DateTime(2018, 2, 1, 14, 1, 0, 0));
        subEvent2.setSubmission(submissionId);
        URI subEvent2Id = client.createResource(subEvent2);
        this.createdUris.put(subEvent2Id, SubmissionEvent.class);
        
        //wait for index
        attempt(RETRIES, () -> {
            final URI uri = client.findByAttribute(SubmissionEvent.class, "@id", subEvent2Id);
            assertEquals(subEvent2Id, uri);
        }); 

        SubmissionStatusService service = new SubmissionStatusService(submissionId);
        SubmissionStatus newStatus = service.calculateAndUpdateSubmissionStatus();
        //check correct value returned
        assertEquals(SubmissionStatus.APPROVAL_REQUESTED, newStatus);
        
        //also check database updated
        submission = client.readResource(submissionId, Submission.class);
        assertEquals(SubmissionStatus.APPROVAL_REQUESTED, submission.getSubmissionStatus());
        
    }

    
    /**
     * Make sure that if the status is pre-submission and has a value it will override the value
     * if the user set override to true
     */
    @Test
    public void testOverridePreSubmissionStatusWhenSet() {
        
        // create a random Submission, but add important values for this test
        Submission submission = random(Submission.class, 1);
        submission.setSubmitted(false);
        submission.setRepositories(Arrays.asList(repo1Id, repo2Id));
        submission.setPublication(publicationId);
        submission.setSubmissionStatus(SubmissionStatus.APPROVAL_REQUESTED);
        URI submissionId = client.createResource(submission);
        this.createdUris.put(submissionId, Submission.class);
        
        // add a couple of events that will be used to calculate status
        SubmissionEvent subEvent1 = random(SubmissionEvent.class, 1);
        subEvent1.setEventType(EventType.APPROVAL_REQUESTED);
        subEvent1.setPerformedDate(new DateTime(2018, 2, 1, 12, 1, 0, 0));
        subEvent1.setSubmission(submissionId);
        URI subEvent1Id = client.createResource(subEvent1);
        this.createdUris.put(subEvent1Id, SubmissionEvent.class);
        
        SubmissionEvent subEvent2 = random(SubmissionEvent.class, 1);
        subEvent2.setEventType(EventType.CHANGES_REQUESTED);
        subEvent2.setPerformedDate(new DateTime(2018, 2, 1, 14, 1, 0, 0));
        subEvent2.setSubmission(submissionId);
        URI subEvent2Id = client.createResource(subEvent2);
        this.createdUris.put(subEvent2Id, SubmissionEvent.class);
        
        //wait for index
        attempt(RETRIES, () -> {
            final URI uri = client.findByAttribute(SubmissionEvent.class, "@id", subEvent2Id);
            assertEquals(subEvent2Id, uri);
        }); 

        SubmissionStatusService service = new SubmissionStatusService(submissionId);
        //this time we set override to true
        SubmissionStatus newStatus = service.calculateAndUpdateSubmissionStatus(true);
        //check correct value returned
        assertEquals(SubmissionStatus.CHANGES_REQUESTED, newStatus);
        
        //also check database updated
        submission = client.readResource(submissionId, Submission.class);
        assertEquals(SubmissionStatus.CHANGES_REQUESTED, submission.getSubmissionStatus());
        
    }
    

    
    /**
     * Make sure Deposits used in post-submission status calc
     */
    @Test
    public void testPostSubmissionStatusFromDeposits() {
        
        // create a random Submission, but add important values for this test
        Submission submission = random(Submission.class, 1);
        submission.setSubmitted(true);
        submission.setRepositories(Arrays.asList(repo1Id, repo2Id));
        submission.setPublication(publicationId);
        submission.setSubmissionStatus(SubmissionStatus.APPROVAL_REQUESTED);
        URI submissionId = client.createResource(submission);
        this.createdUris.put(submissionId, Submission.class);
        
        // add a couple of deposits that will be used to calculate status
        Deposit deposit1 = random(Deposit.class, 1);
        deposit1.setDepositStatus(DepositStatus.ACCEPTED);
        deposit1.setRepository(repo1Id);
        deposit1.setSubmission(submissionId);
        URI deposit1Id = client.createResource(deposit1);
        this.createdUris.put(deposit1Id, Deposit.class);
        
        Deposit deposit2 = random(Deposit.class, 1);
        deposit2.setDepositStatus(DepositStatus.FAILED);
        deposit2.setRepository(repo2Id);
        deposit2.setSubmission(submissionId);
        URI deposit2Id = client.createResource(deposit1);
        this.createdUris.put(deposit2Id, Deposit.class);
        
        //wait for index
        attempt(RETRIES, () -> {
            final URI uri = client.findByAttribute(Deposit.class, "@id", deposit2Id);
            assertEquals(deposit2Id, uri);
        }); 

        SubmissionStatusService service = new SubmissionStatusService(submissionId);
        //this time we set override to true
        SubmissionStatus newStatus = service.calculateAndUpdateSubmissionStatus();
        //check correct value returned
        assertEquals(SubmissionStatus.SUBMITTED, newStatus);
        
        //also check database updated
        submission = client.readResource(submissionId, Submission.class);
        assertEquals(SubmissionStatus.SUBMITTED, submission.getSubmissionStatus());
        
    }

    
    /**
     * Make sure RepositoryCopies used in post-submission status calc
     */
    @Test
    public void testPostSubmissionStatusFromRepositoryCopies() {
        
        // create a random Submission, but add important values for this test
        Submission submission = random(Submission.class, 1);
        submission.setSubmitted(true);
        submission.setRepositories(Arrays.asList(repo1Id, repo2Id));
        submission.setPublication(publicationId);
        submission.setSubmissionStatus(SubmissionStatus.SUBMITTED);
        URI submissionId = client.createResource(submission);
        this.createdUris.put(submissionId, Submission.class);
        
        // add a couple of deposits that will be used to calculate status
        Deposit deposit1 = random(Deposit.class, 1);
        deposit1.setDepositStatus(DepositStatus.ACCEPTED);
        deposit1.setRepository(repo1Id);
        deposit1.setSubmission(submissionId);
        URI deposit1Id = client.createResource(deposit1);
        this.createdUris.put(deposit1Id, Deposit.class);
        
        //using rejected status, going to override this with the repocopy
        Deposit deposit2 = random(Deposit.class, 1);
        deposit2.setDepositStatus(DepositStatus.REJECTED);
        deposit2.setRepository(repo2Id);
        deposit2.setSubmission(submissionId);
        URI deposit2Id = client.createResource(deposit1);
        this.createdUris.put(deposit2Id, Deposit.class);
        
        // completed repocopy for repo1
        RepositoryCopy repoCopy1 = random(RepositoryCopy.class, 1);
        repoCopy1.setPublication(publicationId);
        repoCopy1.setRepository(repo1Id);
        repoCopy1.setCopyStatus(CopyStatus.COMPLETE);
        URI repoCopy1Id = client.createResource(repoCopy1);
        this.createdUris.put(repoCopy1Id, RepositoryCopy.class);

        //completed repocopy fro repo2
        RepositoryCopy repoCopy2 = random(RepositoryCopy.class, 1);
        repoCopy2.setPublication(publicationId);
        repoCopy2.setRepository(repo2Id);
        repoCopy2.setCopyStatus(CopyStatus.COMPLETE);
        URI repoCopy2Id = client.createResource(repoCopy2);
        this.createdUris.put(repoCopy2Id, RepositoryCopy.class);
        
        //wait for index
        attempt(RETRIES, () -> {
            final URI uri = client.findByAttribute(RepositoryCopy.class, "@id", repoCopy2Id);
            assertEquals(repoCopy2Id, uri);
        }); 

        SubmissionStatusService service = new SubmissionStatusService(submissionId);
        //this time we won't update Submission record, just return the value
        SubmissionStatus newStatus = service.calculateSubmissionStatus();
        assertEquals(SubmissionStatus.COMPLETE, newStatus);
        
        //also check database was not updated with the new value
        submission = client.readResource(submissionId, Submission.class);
        assertEquals(SubmissionStatus.SUBMITTED, submission.getSubmissionStatus());
        
    }

    
    /**
     * Make sure both Deposits and RepositoryCopies are used in post-submission status calc.
     * This time, we want to make sure that the item NEEDS ATTENTION if one Deposit fails
     * and the other succeeds, even though there is a complete RepositoryCopy for one.
     */
    @Test
    public void testPostSubmissionStatusFromDepositsAndRepoCopies() {
        
        // create a random Submission, but add important values for this test
        Submission submission = random(Submission.class, 1);
        submission.setSubmitted(true);
        submission.setRepositories(Arrays.asList(repo1Id, repo2Id));
        submission.setPublication(publicationId);
        submission.setSubmissionStatus(SubmissionStatus.SUBMITTED);
        URI submissionId = client.createResource(submission);
        this.createdUris.put(submissionId, Submission.class);
        
        // add a couple of deposits that will be used to calculate status
        Deposit deposit1 = random(Deposit.class, 1);
        deposit1.setDepositStatus(DepositStatus.ACCEPTED);
        deposit1.setRepository(repo1Id);
        deposit1.setSubmission(submissionId);
        URI deposit1Id = client.createResource(deposit1);
        this.createdUris.put(deposit1Id, Deposit.class);
        
        //using rejected status, going to override this with the repocopy
        Deposit deposit2 = random(Deposit.class, 1);
        deposit2.setDepositStatus(DepositStatus.REJECTED);
        deposit2.setRepository(repo2Id);
        deposit2.setSubmission(submissionId);
        URI deposit2Id = client.createResource(deposit2);
        this.createdUris.put(deposit2Id, Deposit.class);

        //completed repocopy fro repo2
        RepositoryCopy repoCopy1 = random(RepositoryCopy.class, 1);
        repoCopy1.setPublication(publicationId);
        repoCopy1.setRepository(repo1Id);
        repoCopy1.setCopyStatus(CopyStatus.COMPLETE);
        URI repoCopy1Id = client.createResource(repoCopy1);
        this.createdUris.put(repoCopy1Id, RepositoryCopy.class);
        
        //wait for index
        attempt(RETRIES, () -> {
            final URI uri = client.findByAttribute(RepositoryCopy.class, "@id", repoCopy1Id);
            assertEquals(repoCopy1Id, uri);
        }); 

        SubmissionStatusService service = new SubmissionStatusService(submissionId);
        SubmissionStatus newStatus = service.calculateAndUpdateSubmissionStatus();
        assertEquals(SubmissionStatus.NEEDS_ATTENTION, newStatus);
        
        //also check database was not updated with the new value
        submission = client.readResource(submissionId, Submission.class);
        assertEquals(SubmissionStatus.NEEDS_ATTENTION, submission.getSubmissionStatus());
        
    }
}
