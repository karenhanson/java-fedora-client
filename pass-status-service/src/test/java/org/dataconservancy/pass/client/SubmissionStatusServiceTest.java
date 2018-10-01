package org.dataconservancy.pass.client;

import java.net.URI;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
        
        Map<String, Collection<URI>> submissionIncoming = new HashMap<String, Collection<URI>>();
        submissionIncoming.put("submission", new HashSet<URI>(Arrays.asList(deposit1Id, deposit2Id)));
        submissionIncoming.put("acls", new HashSet<URI>(Arrays.asList(new URI("fake:uri"))));

        Map<String, Collection<URI>> publicationsIncoming = new HashMap<String, Collection<URI>>();
        publicationsIncoming.put("publication", new HashSet<URI>(Arrays.asList(repoCopy1Id, repoCopy2Id)));
        
        Submission submission = new Submission();
        submission.setId(new URI("submission:1"));
        submission.setRepositories(repositories);
        submission.setPublication(publicationId);
        submission.setSubmitted(true);
        
        service = new SubmissionStatusService(submission, client);
        
        when(client.getIncoming(Mockito.any())).thenReturn(submissionIncoming).thenReturn(publicationsIncoming);;
        when(client.readResource(Mockito.any(), eq(Deposit.class))).thenReturn(deposit(DepositStatus.ACCEPTED, repo1Id)).thenReturn(deposit(DepositStatus.ACCEPTED, repo2Id));
        when(client.readResource(Mockito.any(), eq(RepositoryCopy.class))).thenReturn(repoCopy(CopyStatus.ACCEPTED,repo1Id)).thenReturn(repoCopy(CopyStatus.ACCEPTED,repo2Id));
        
        SubmissionStatus newStatus = service.calculateSubmissionStatus();
        assertEquals(SubmissionStatus.SUBMITTED, newStatus);

        verify(client, Mockito.times(2)).getIncoming(Mockito.any());
        verify(client, Mockito.times(2)).readResource(Mockito.any(), eq(Deposit.class));
        verify(client, Mockito.times(2)).readResource(Mockito.any(), eq(RepositoryCopy.class));
        verify(client, Mockito.times(0)).readResource(Mockito.any(), eq(SubmissionEvent.class));
        
    }
    

    /**
     * Basic test to ensure that appropriate calls are made to client when calculating status of
     * Submission not yet submitted.
     * @throws Exception
     */
    @Test
    public void testCalcSubmissionStatusPreSubmission() throws Exception {

        List<URI> repositories = Arrays.asList(repo1Id, repo2Id);
        
        Map<String, Collection<URI>> submissionIncoming = new HashMap<String, Collection<URI>>();
        submissionIncoming.put("submission", new HashSet<URI>(Arrays.asList(subEvent1Id, subEvent2Id)));
        submissionIncoming.put("acls", new HashSet<URI>(Arrays.asList(new URI("fake:uri"))));
        
        Submission submission = new Submission();
        submission.setId(new URI("submission:1"));
        submission.setRepositories(repositories);
        submission.setPublication(publicationId);
        submission.setSubmitted(false);
        
        service = new SubmissionStatusService(submission, client);
        
        when(client.getIncoming(Mockito.any())).thenReturn(submissionIncoming);
        when(client.readResource(Mockito.any(), eq(SubmissionEvent.class)))
            .thenReturn(submissionEvent(new DateTime(2018, 2, 1, 12, 1, 0, 0), EventType.APPROVAL_REQUESTED))
            .thenReturn(submissionEvent(new DateTime(2018, 2, 1, 12, 2, 0, 0), EventType.CHANGES_REQUESTED));

        SubmissionStatus newStatus = service.calculateSubmissionStatus();
        assertEquals(SubmissionStatus.CHANGES_REQUESTED, newStatus);

        verify(client, Mockito.times(1)).getIncoming(Mockito.any());
        verify(client, Mockito.times(2)).readResource(Mockito.any(), eq(SubmissionEvent.class));
        verify(client, Mockito.times(0)).readResource(Mockito.any(), eq(Deposit.class));
        verify(client, Mockito.times(0)).readResource(Mockito.any(), eq(RepositoryCopy.class));
        
    }
        
}
