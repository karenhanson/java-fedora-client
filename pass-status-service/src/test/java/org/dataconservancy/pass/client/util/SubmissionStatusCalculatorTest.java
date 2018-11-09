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
package org.dataconservancy.pass.client.util;

import java.net.URI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.dataconservancy.pass.client.SubmissionStatusTestBase;
import org.dataconservancy.pass.model.Deposit;
import org.dataconservancy.pass.model.Deposit.DepositStatus;
import org.dataconservancy.pass.model.RepositoryCopy;
import org.dataconservancy.pass.model.RepositoryCopy.CopyStatus;
import org.dataconservancy.pass.model.SubmissionEvent;
import org.dataconservancy.pass.model.SubmissionEvent.EventType;
import org.joda.time.DateTime;

import static org.dataconservancy.pass.model.Submission.SubmissionStatus.APPROVAL_REQUESTED;
import static org.dataconservancy.pass.model.Submission.SubmissionStatus.CANCELLED;
import static org.dataconservancy.pass.model.Submission.SubmissionStatus.CHANGES_REQUESTED;
import static org.dataconservancy.pass.model.Submission.SubmissionStatus.COMPLETE;
import static org.dataconservancy.pass.model.Submission.SubmissionStatus.MANUSCRIPT_REQUIRED;
import static org.dataconservancy.pass.model.Submission.SubmissionStatus.NEEDS_ATTENTION;
import static org.dataconservancy.pass.model.Submission.SubmissionStatus.SUBMITTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests the SubmissionStatusCalculator utility functions
 * @author Karen Hanson
 */
public class SubmissionStatusCalculatorTest extends SubmissionStatusTestBase  {

    /**
     * 3 repositories listed, deposits only, no repositoryCopies. As long as none are rejected, it 
     * should always be SUBMITTED
     * @throws Exception
     */
    @Test
    public void testPostSubmissionStatusDepositOnlySubmitted() throws Exception {
        List<URI> repositories = Arrays.asList(repo1Id, repo2Id, repo3Id);
                        
        List<Deposit> deposits = Arrays.asList(deposit(DepositStatus.ACCEPTED, repo1Id),
                                               deposit(DepositStatus.FAILED, repo2Id));
        assertEquals(SUBMITTED, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, deposits, null));        
        
        deposits = Arrays.asList(deposit(DepositStatus.ACCEPTED, repo1Id),
                                 deposit(DepositStatus.FAILED, repo2Id),
                                 deposit(DepositStatus.ACCEPTED, repo3Id));
        
        assertEquals(SUBMITTED, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, deposits, null)); 

        deposits = Arrays.asList(deposit(DepositStatus.ACCEPTED, repo1Id),
                                 deposit(DepositStatus.ACCEPTED, repo2Id),
                                 deposit(DepositStatus.ACCEPTED, repo3Id));
        assertEquals(SUBMITTED, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, deposits, null));   
        
    }


    /**
     * 3 repositories listed, no deposits, repositoryCopies only. As long as none are rejected, it 
     * should always be SUBMITTED
     * @throws Exception
     */
    @Test
    public void testPostSubmissionStatusRepoCopyOnlySubmitted() throws Exception {
        List<URI> repositories = Arrays.asList(repo1Id, repo2Id, repo3Id);
        List<RepositoryCopy> repositoryCopies = Arrays.asList(repoCopy(CopyStatus.COMPLETE, repo1Id));
    
        assertEquals(SUBMITTED, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, null, repositoryCopies));        


        repositoryCopies = Arrays.asList(repoCopy(CopyStatus.COMPLETE, repo1Id),
                                         repoCopy(CopyStatus.COMPLETE, repo2Id));
        assertEquals(SUBMITTED, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, null, repositoryCopies));        
    
        //add one more in-progress repocopy
        repositoryCopies = Arrays.asList(repoCopy(CopyStatus.COMPLETE, repo1Id),
                                         repoCopy(CopyStatus.COMPLETE, repo2Id), 
                                         repoCopy(CopyStatus.IN_PROGRESS, repo3Id));
        assertEquals(SUBMITTED, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, null, repositoryCopies));  
          
    }


    /**
     * 1 repositories listed, no deposits, repositoryCopy only. copyStatus is null
     * @throws Exception
     */
    @Test
    public void testSubmittedNoDepositCopyNoStatus() throws Exception {
        List<URI> repositories = Arrays.asList(repo1Id);
        List<RepositoryCopy> repositoryCopies = Arrays.asList(repoCopy(null, repo1Id));
    
        assertEquals(SUBMITTED, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, null, repositoryCopies));
    }


    /**
     * 1 repositories listed, no deposits, repositoryCopy only. copyStatus is null
     * @throws Exception
     */
    @Test
    public void testSubmittedDepositNullStatusNoRepoCopy() throws Exception {
        List<URI> repositories = Arrays.asList(repo1Id);
        List<Deposit> deposits = Arrays.asList(deposit(null, repo1Id));
    
        assertEquals(SUBMITTED, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, deposits, null));
    }

    
    /**
     * 3 repositories with various states for deposits and repositoryCopies all of which should come out as 
     * having the status of SUBMITTED
     * @throws Exception
     */
    @Test
    public void testPostSubmissionStatusDepositAndRepoCopySubmitted() throws Exception {
        List<URI> repositories = Arrays.asList(repo1Id, repo2Id, repo3Id);
                        
        List<Deposit> deposits = Arrays.asList(deposit(DepositStatus.ACCEPTED, repo1Id),
                                               deposit(DepositStatus.FAILED, repo2Id));
        
        List<RepositoryCopy> repositoryCopies = Arrays.asList(repoCopy(CopyStatus.COMPLETE, repo1Id));

        assertEquals(SUBMITTED, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, deposits, repositoryCopies));

        repositoryCopies = Arrays.asList(repoCopy(CopyStatus.COMPLETE, repo1Id),
                                         repoCopy(CopyStatus.COMPLETE, repo2Id));
        assertEquals(SUBMITTED, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, deposits, repositoryCopies));

        deposits = Arrays.asList(deposit(DepositStatus.ACCEPTED, repo1Id),
                                 deposit(DepositStatus.FAILED, repo2Id),
                                 deposit(DepositStatus.ACCEPTED, repo3Id));
        assertEquals(SUBMITTED, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, deposits, repositoryCopies));       
    
        //add one more in-progress repocopy
        repositoryCopies = Arrays.asList(repoCopy(CopyStatus.COMPLETE, repo1Id),
                                         repoCopy(CopyStatus.COMPLETE, repo2Id), 
                                         repoCopy(CopyStatus.IN_PROGRESS, repo3Id));
        assertEquals(SUBMITTED, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, deposits, repositoryCopies));  
                
    }


    /**
     * Tests various situations with Deposits only that should trigger {@code SubmissionStatus.NEEDS_ATTENTION}
     * as the {@code Submission.submissionStatus} value
     * @throws Exception
     */
    @Test
    public void testPostSubmissionStatusDepositOnlyNeedsAttention() throws Exception {
        //1 repo 1 rejected deposit
        List<URI> repositories = Arrays.asList(repo1Id);                        
        List<Deposit> deposits = Arrays.asList(deposit(DepositStatus.REJECTED, repo1Id));
        assertEquals(NEEDS_ATTENTION, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, deposits, null));   
        
        // 3 repo, 2 deposits, 1 rejected
        repositories = Arrays.asList(repo1Id, repo2Id, repo3Id);
        deposits = Arrays.asList(deposit(DepositStatus.ACCEPTED, repo1Id),
                                 deposit(DepositStatus.REJECTED, repo2Id));
        assertEquals(NEEDS_ATTENTION, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, deposits, null));        
        
        // 3 repo, 3 deposits, 1 rejected
        deposits = Arrays.asList(deposit(DepositStatus.REJECTED, repo1Id),
                                 deposit(DepositStatus.ACCEPTED, repo2Id),
                                 deposit(DepositStatus.ACCEPTED, repo3Id));        
        assertEquals(NEEDS_ATTENTION, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, deposits, null));   
        
    }


    /**
     * Tests various situations with RepositoryCopies only that should trigger {@code SubmissionStatus.NEEDS_ATTENTION}
     * as the {@code Submission.submissionStatus} value
     * @throws Exception
     */
    @Test
    public void testPostSubmissionStatusRepoCopyOnlyNeedsAttention() throws Exception {
        List<URI> repositories = Arrays.asList(repo1Id);
        List<RepositoryCopy> repositoryCopies = Arrays.asList(repoCopy(CopyStatus.REJECTED, repo1Id));
        assertEquals(NEEDS_ATTENTION, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, null, repositoryCopies));  
        
        repositoryCopies = Arrays.asList(repoCopy(CopyStatus.STALLED, repo1Id));
        assertEquals(NEEDS_ATTENTION, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, null, repositoryCopies));        

        repositories = Arrays.asList(repo1Id, repo2Id, repo3Id);
        repositoryCopies = Arrays.asList(repoCopy(CopyStatus.COMPLETE, repo1Id),
                                         repoCopy(CopyStatus.STALLED, repo2Id));
        assertEquals(NEEDS_ATTENTION, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, null, repositoryCopies));        
    
        //add one more in-progress repocopy
        repositoryCopies = Arrays.asList(repoCopy(CopyStatus.COMPLETE, repo1Id),
                                         repoCopy(CopyStatus.REJECTED, repo2Id), 
                                         repoCopy(CopyStatus.IN_PROGRESS, repo3Id));
        assertEquals(NEEDS_ATTENTION, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, null, repositoryCopies));                  
    }

    
    /**
     * Various numbers of repositories with various states for deposits and repositoryCopies all of which 
     * should come out as having status of {@code SubmissionStatus.NEEDS_ATTENTION}
     * @throws Exception
     */
    @Test
    public void testPostSubmissionStatusDepositAndRepoCopyNeedsAttention() throws Exception {
        List<URI> repositories = Arrays.asList(repo1Id, repo2Id);
        List<Deposit> deposits = Arrays.asList(deposit(DepositStatus.ACCEPTED, repo1Id),
                                               deposit(DepositStatus.REJECTED, repo2Id));
        List<RepositoryCopy> repositoryCopies = Arrays.asList(repoCopy(CopyStatus.COMPLETE, repo1Id));
        assertEquals(NEEDS_ATTENTION, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, deposits, repositoryCopies));

        deposits = Arrays.asList(deposit(DepositStatus.ACCEPTED, repo1Id),
                                 deposit(DepositStatus.ACCEPTED, repo2Id));
        repositoryCopies = Arrays.asList(repoCopy(CopyStatus.COMPLETE, repo1Id),
                                         repoCopy(CopyStatus.STALLED, repo2Id));
        assertEquals(NEEDS_ATTENTION, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, deposits, repositoryCopies));

        repositoryCopies = Arrays.asList(repoCopy(CopyStatus.IN_PROGRESS, repo1Id),
                                         repoCopy(CopyStatus.REJECTED, repo2Id));
        assertEquals(NEEDS_ATTENTION, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, deposits, repositoryCopies));       
    
        repositories = Arrays.asList(repo1Id, repo2Id, repo3Id);
        deposits = Arrays.asList(deposit(DepositStatus.ACCEPTED, repo1Id),
                                 deposit(DepositStatus.ACCEPTED, repo2Id),
                                 deposit(DepositStatus.FAILED, repo2Id));
        repositoryCopies = Arrays.asList(repoCopy(CopyStatus.COMPLETE, repo2Id), 
                                         repoCopy(CopyStatus.STALLED, repo3Id));
        assertEquals(NEEDS_ATTENTION, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, deposits, repositoryCopies));  
                
    }

    
    /**
     * This confirms that if there are completed repository copies for each repository listed, the status is complete, 
     * regardless of whether there is a deposit for each one or not.
     * @throws Exception
     */
    @Test
    public void testPostSubmissionStatusComplete() throws Exception {
        List<URI> repositories = Arrays.asList(repo1Id);
        List<RepositoryCopy> repositoryCopies = Arrays.asList(repoCopy(CopyStatus.COMPLETE, repo1Id));        
        assertEquals(COMPLETE, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, null, repositoryCopies));
        
        List<Deposit> deposits = Arrays.asList(deposit(DepositStatus.ACCEPTED, repo1Id));
        assertEquals(COMPLETE, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, deposits, repositoryCopies));
        
        repositories = Arrays.asList(repo1Id, repo2Id, repo3Id);
        deposits = Arrays.asList(deposit(DepositStatus.ACCEPTED, repo1Id),
                                 deposit(DepositStatus.REJECTED, repo2Id));
        repositoryCopies = Arrays.asList(repoCopy(CopyStatus.COMPLETE, repo1Id),
                                         repoCopy(CopyStatus.COMPLETE, repo2Id),
                                         repoCopy(CopyStatus.COMPLETE, repo3Id));
        assertEquals(COMPLETE, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, deposits, repositoryCopies));
        
        assertEquals(COMPLETE, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, null, repositoryCopies));
                
    }

    
    /**
     * This confirms that if you try to provide a null fo the repositories list it will throw an exception
     * @throws Exception
     */
    @Test(expected=IllegalArgumentException.class)
    public void testPostSubmissionStatusNulls() throws Exception { 
        SubmissionStatusCalculator.calculatePostSubmissionStatus(null, null, null);                
    }

    
    /**
     * This confirms that even if a Deposit is rejected, a RepositoryCopy's status will override the Deposit status
     * @throws Exception
     */
    @Test
    public void testPostSubmissionStatusRepoCopyOverrideDeposit() throws Exception {
        List<URI> repositories = Arrays.asList(repo1Id);
        List<Deposit> deposits = Arrays.asList(deposit(DepositStatus.REJECTED, repo1Id));
        List<RepositoryCopy> repositoryCopies = Arrays.asList(repoCopy(CopyStatus.COMPLETE, repo1Id));
        assertEquals(COMPLETE, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, deposits, repositoryCopies));

        repositoryCopies = Arrays.asList(repoCopy(CopyStatus.IN_PROGRESS, repo1Id));
        assertEquals(SUBMITTED, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, deposits, repositoryCopies));
        
        repositories = Arrays.asList(repo1Id, repo2Id);
        deposits = Arrays.asList(deposit(DepositStatus.ACCEPTED, repo1Id),
                                 deposit(DepositStatus.REJECTED, repo2Id));
        repositoryCopies = Arrays.asList(repoCopy(CopyStatus.COMPLETE, repo1Id),
                                         repoCopy(CopyStatus.IN_PROGRESS, repo2Id));
        assertEquals(SUBMITTED, SubmissionStatusCalculator.calculatePostSubmissionStatus(repositories, deposits, repositoryCopies));
                
    }
    

    /**
     * Confirms that MANUSCRIPT_REQUIRED is appropriately assigned as a pre-submission status.
     */
    @Test
    public void testPreSubmissionStatusManuscriptExpected() {
        assertEquals(MANUSCRIPT_REQUIRED, SubmissionStatusCalculator.calculatePreSubmissionStatus(null));
        assertEquals(MANUSCRIPT_REQUIRED, SubmissionStatusCalculator.calculatePreSubmissionStatus(new ArrayList<SubmissionEvent>()));        
    }
    
    /**
     * Tests various scenarios where outcome should be APPROVAL_REQUESTED
     */
    @Test 
    public void testPreSubmissionStatusApprovalRequested() {
        List<SubmissionEvent> submissionEvents = 
                Arrays.asList(submissionEvent(new DateTime(2018, 2, 1, 12, 0, 0, 0), EventType.APPROVAL_REQUESTED));
        assertEquals(APPROVAL_REQUESTED, SubmissionStatusCalculator.calculatePreSubmissionStatus(submissionEvents)); 

        submissionEvents = 
                Arrays.asList(submissionEvent(new DateTime(2018, 2, 1, 12, 0, 0, 0), EventType.APPROVAL_REQUESTED_NEWUSER));
        assertEquals(APPROVAL_REQUESTED, SubmissionStatusCalculator.calculatePreSubmissionStatus(submissionEvents)); 
        
        submissionEvents = 
                Arrays.asList(submissionEvent(new DateTime(2018, 2, 1, 12, 0, 0, 0), EventType.APPROVAL_REQUESTED_NEWUSER),
                              submissionEvent(new DateTime(2018, 2, 2, 12, 0, 0, 0), EventType.CHANGES_REQUESTED),
                              submissionEvent(new DateTime(2018, 2, 3, 12, 0, 0, 0), EventType.APPROVAL_REQUESTED));
        assertEquals(APPROVAL_REQUESTED, SubmissionStatusCalculator.calculatePreSubmissionStatus(submissionEvents));         
    }
    

    /**
     * Tests various scenarios in which the pre-submission status should be "CHANGES_REQUESTED"
     */
    @Test 
    public void testPreSubmissionStatusChangesRequested() {
        List<SubmissionEvent> submissionEvents = 
                Arrays.asList(submissionEvent(new DateTime(2018, 2, 1, 12, 0, 0, 0), EventType.CHANGES_REQUESTED));
        assertEquals(CHANGES_REQUESTED, SubmissionStatusCalculator.calculatePreSubmissionStatus(submissionEvents)); 
        
        submissionEvents = 
                Arrays.asList(submissionEvent(new DateTime(2018, 2, 1, 12, 0, 0, 0), EventType.APPROVAL_REQUESTED),
                              submissionEvent(new DateTime(2018, 2, 2, 12, 0, 0, 0), EventType.CHANGES_REQUESTED));
        assertEquals(CHANGES_REQUESTED, SubmissionStatusCalculator.calculatePreSubmissionStatus(submissionEvents));    
        
        submissionEvents = 
                Arrays.asList(submissionEvent(new DateTime(2018, 2, 1, 12, 0, 0, 0), EventType.APPROVAL_REQUESTED),
                              submissionEvent(new DateTime(2018, 2, 1, 12, 2, 0, 0), EventType.CHANGES_REQUESTED),
                              submissionEvent(new DateTime(2018, 2, 1, 12, 2, 3, 0), EventType.APPROVAL_REQUESTED),
                              submissionEvent(new DateTime(2018, 2, 1, 12, 2, 3, 1), EventType.CHANGES_REQUESTED));
        assertEquals(CHANGES_REQUESTED, SubmissionStatusCalculator.calculatePreSubmissionStatus(submissionEvents));          
    }
    

    /**
     * Tests various scenarios in which the pre-submission status should be "CANCELLED"
     */
    @Test 
    public void testPreSubmissionStatusCancelled() {
        List<SubmissionEvent> submissionEvents = 
                Arrays.asList(submissionEvent(new DateTime(2018, 2, 1, 12, 0, 0, 0), EventType.CANCELLED));
        assertEquals(CANCELLED, SubmissionStatusCalculator.calculatePreSubmissionStatus(submissionEvents)); 
        
        submissionEvents = 
                Arrays.asList(submissionEvent(new DateTime(2018, 2, 1, 12, 0, 0, 0), EventType.APPROVAL_REQUESTED),
                              submissionEvent(new DateTime(2018, 2, 2, 12, 0, 0, 0), EventType.CANCELLED));
        assertEquals(CANCELLED, SubmissionStatusCalculator.calculatePreSubmissionStatus(submissionEvents));    
        
        submissionEvents = 
                Arrays.asList(submissionEvent(new DateTime(2018, 2, 1, 12, 1, 0, 0), EventType.APPROVAL_REQUESTED),
                              submissionEvent(new DateTime(2018, 2, 1, 12, 2, 0, 0), EventType.CHANGES_REQUESTED),
                              submissionEvent(new DateTime(2018, 2, 1, 12, 3, 3, 0), EventType.APPROVAL_REQUESTED),
                              submissionEvent(new DateTime(2018, 2, 1, 12, 4, 3, 1), EventType.CANCELLED));
        assertEquals(CANCELLED, SubmissionStatusCalculator.calculatePreSubmissionStatus(submissionEvents));          
    }
    
    /**
     * Makes sure typical status changes log as valid. Some of these will generate warnings
     * since this service is not supposed to change the pre-Submission statuses - it will warn
     * when one is being changed.
     */
    @Test
    public void testValidateStatusChangeOK() {
        //if any of there throw exception the test fails:
        SubmissionStatusCalculator.validateStatusChange(false, APPROVAL_REQUESTED, CHANGES_REQUESTED);
        SubmissionStatusCalculator.validateStatusChange(false, CHANGES_REQUESTED, APPROVAL_REQUESTED);
        SubmissionStatusCalculator.validateStatusChange(false, CHANGES_REQUESTED, CANCELLED);
        SubmissionStatusCalculator.validateStatusChange(true, APPROVAL_REQUESTED, SUBMITTED);
        SubmissionStatusCalculator.validateStatusChange(true, MANUSCRIPT_REQUIRED, SUBMITTED);
        SubmissionStatusCalculator.validateStatusChange(true, SUBMITTED, COMPLETE);
        SubmissionStatusCalculator.validateStatusChange(true, SUBMITTED, NEEDS_ATTENTION);
        SubmissionStatusCalculator.validateStatusChange(true, NEEDS_ATTENTION, SUBMITTED);
        SubmissionStatusCalculator.validateStatusChange(true, NEEDS_ATTENTION, COMPLETE);        
    }
    
    /**
     * Makes sure typical invalid status changes are caught
     */
    @Test
    public void testValidateStatusChangeException() {

        //if any of there throw exception the test fails:
        try {
            //should fail based on assigning submitted status to unsubmitted 
            SubmissionStatusCalculator.validateStatusChange(true, APPROVAL_REQUESTED, CHANGES_REQUESTED);
            fail("Expected exception to be thrown");
        } catch (Exception ex) {}

        try {
            //should fail based on change from submitted to unsubmitted status
            SubmissionStatusCalculator.validateStatusChange(false, SUBMITTED, CANCELLED);
            fail("Expected exception to be thrown");
        } catch (Exception ex) {}    

        try {
            //should fail based on Submission have submitted=false but trying to assign post-submission status
            SubmissionStatusCalculator.validateStatusChange(false, APPROVAL_REQUESTED, SUBMITTED);
            fail("Expected exception to be thrown");
        } catch (Exception ex) {}    

        try {
            //should be OK, but log should show a warning
            SubmissionStatusCalculator.validateStatusChange(false, APPROVAL_REQUESTED, CHANGES_REQUESTED);
        } catch (Exception ex) {
            fail("Exception should not have been thrown, just a warning for this.");
        }    
    }
    
    
    
}
