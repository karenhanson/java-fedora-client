package org.dataconservancy.pass.client;

import java.net.URI;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import org.dataconservancy.pass.model.Deposit;
import org.dataconservancy.pass.model.Deposit.DepositStatus;
import org.dataconservancy.pass.model.RepositoryCopy;
import org.dataconservancy.pass.model.RepositoryCopy.CopyStatus;
import org.dataconservancy.pass.model.Submission;
import org.dataconservancy.pass.model.Submission.SubmissionStatus;
import org.dataconservancy.pass.model.SubmissionEvent;
import org.dataconservancy.pass.model.SubmissionEvent.EventType;
import org.joda.time.DateTime;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for simple App.
 */
public class SubmissionStatusServiceTest extends SubmissionStatusTestBase {

    @Mock
    private PassClient client;

    private SubmissionStatusService service;

    @Before 
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
    
    
    /**
     * Basic test to ensure that appropriate calls are made to client when calculating status
     * of submitted Submission
     * @throws Exception
     */
    @Test
    public void testCalcSubmissionStatusPostSubmission() throws Exception {

        List<URI> repositories = Arrays.asList(repo1Id, repo2Id);
        Set<URI> depositUris = new HashSet<URI>(Arrays.asList(deposit1Id, deposit2Id));
        Set<URI> repoCopyUris = new HashSet<URI>(Arrays.asList(repoCopy1Id, repoCopy2Id));
        Set<URI> submissionEvents = new HashSet<URI>();
        
        Submission submission = new Submission();
        submission.setId(new URI("submission:1"));
        submission.setRepositories(repositories);
        submission.setPublication(publicationId);
        submission.setSubmitted(true);
        
        service = new SubmissionStatusService(submission, client);
        
        when(client.findAllByAttribute(eq(Deposit.class), eq("submission"), Mockito.any())).thenReturn(depositUris);
        when(client.readResource(Mockito.any(), eq(Deposit.class))).thenReturn(deposit(DepositStatus.ACCEPTED, repo1Id)).thenReturn(deposit(DepositStatus.ACCEPTED, repo2Id));
        
        when(client.findAllByAttribute(eq(RepositoryCopy.class), eq("submission"), Mockito.any())).thenReturn(repoCopyUris);
        when(client.readResource(Mockito.any(), eq(RepositoryCopy.class))).thenReturn(repoCopy(CopyStatus.ACCEPTED,repo1Id)).thenReturn(repoCopy(CopyStatus.ACCEPTED,repo2Id));
        
        when(client.findAllByAttribute(eq(SubmissionEvent.class), eq("submission"), Mockito.any())).thenReturn(submissionEvents);

        SubmissionStatus newStatus = service.calculateSubmissionStatus();
        assertEquals(SubmissionStatus.SUBMITTED, newStatus);

        verify(client, Mockito.times(1)).findAllByAttribute(eq(Deposit.class),  eq("submission"), Mockito.any());
        verify(client, Mockito.times(1)).findAllByAttribute(eq(RepositoryCopy.class), eq("publication"), Mockito.any());
        verify(client, Mockito.times(0)).findAllByAttribute(eq(SubmissionEvent.class),  eq("submission"), Mockito.any());
        
    }
    

    /**
     * Basic test to ensure that appropriate calls are made to client when calculating status of
     * Submission not yet submitted.
     * @throws Exception
     */
    @Test
    public void testCalcSubmissionStatusPreSubmission() throws Exception {

        List<URI> repositories = Arrays.asList(repo1Id, repo2Id);
        Set<URI> submissionEventUris = new HashSet<URI>(Arrays.asList(subEvent1Id, subEvent2Id));
        
        Submission submission = new Submission();
        submission.setId(new URI("submission:1"));
        submission.setRepositories(repositories);
        submission.setPublication(publicationId);
        submission.setSubmitted(false);
        
        service = new SubmissionStatusService(submission, client);
        
        when(client.findAllByAttribute(eq(SubmissionEvent.class), eq("submission"), Mockito.any())).thenReturn(submissionEventUris);
        when(client.readResource(Mockito.any(), eq(SubmissionEvent.class)))
            .thenReturn(submissionEvent(new DateTime(2018, 2, 1, 12, 1, 0, 0), EventType.APPROVAL_REQUESTED))
            .thenReturn(submissionEvent(new DateTime(2018, 2, 1, 12, 2, 0, 0), EventType.CHANGES_REQUESTED));

        SubmissionStatus newStatus = service.calculateSubmissionStatus();
        assertEquals(SubmissionStatus.CHANGES_REQUESTED, newStatus);

        verify(client, Mockito.times(0)).findAllByAttribute(eq(Deposit.class),  eq("submission"), Mockito.any());
        verify(client, Mockito.times(0)).findAllByAttribute(eq(RepositoryCopy.class), eq("publication"), Mockito.any());
        verify(client, Mockito.times(1)).findAllByAttribute(eq(SubmissionEvent.class),  eq("submission"), Mockito.any());
        
    }
        
}
