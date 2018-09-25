package org.dataconservancy.pass.client;

import java.net.URI;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.dataconservancy.pass.client.util.SubmissionStatusCalculator;
import org.dataconservancy.pass.model.Deposit;
import org.dataconservancy.pass.model.RepositoryCopy;
import org.dataconservancy.pass.model.Submission;
import org.dataconservancy.pass.model.Submission.SubmissionStatus;
import org.dataconservancy.pass.model.SubmissionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A service for calculating and updating the `Submission.submissionStatus` value based on data
 * related to the Submission. By default, there is a division of responsibility in calculating 
 * this status, with pre-submission statuses being managed by the UI, and post-Submission statuses
 * being managed by back-end services. For this reason, pre-submission statuses will only be changed
 * if the starting value is null, or overrideUIStatus is true.
 * @author Karen Hanson
 *
 */
public class SubmissionStatusService 
{

    private static final Logger LOG = LoggerFactory.getLogger(SubmissionStatusService.class);
    
    private PassClient client;
    
    private Submission submission;
    
    /**
     * Initiate service using a Submission object
     * @param submission
     */
    public SubmissionStatusService(Submission submission) { 
        if (submission==null) {
            throw new IllegalArgumentException("submission cannot be null");
        }
        if (submission.getId()==null) {
            throw new IllegalArgumentException("No status could be calculated for the Submission as it does not have a `Submission.id`.");  
        }
        this.client = PassClientFactory.getPassClient();
        this.submission = submission;
    }


    /**
     * Initiate service using a `Submission.id` 
     * @param submissionId
     */
    public SubmissionStatusService(URI submissionId) { 
        if (submissionId==null) {
            throw new IllegalArgumentException("submissionId cannot be null");
        }
        this.client = PassClientFactory.getPassClient();
        try {
            this.submission = client.readResource(submissionId, Submission.class);
        } catch (Exception ex) {
            String msg = String.format("Failed to retrieve Submission with ID %s from the database", submissionId);
            throw new RuntimeException(msg);
        }
    }
    
    
    /**
     * Supports setting a specific client, primarily for testing.
     * @param submission
     * @param client
     */
    public SubmissionStatusService(Submission submission, PassClient client) {
        if (submission==null) {
            throw new IllegalArgumentException("submission cannot be null");
        }
        if (client==null) {
            throw new IllegalArgumentException("client cannot be null");
        }
        this.submission = submission;
        this.client = client;
    }

    
    /**
     * Calculates the appropriate {@link SubmissionStatus} for the {@link Submission} provided. 
     * This is based on the status of associated {@link Deposit}s and {@link RepositoryCopy}s for 
     * {@code submitted} records, and {@link SubmissionEvent}s for unsubmitted records.
     * @param submission
     * @return
     */
    public SubmissionStatus calculateSubmissionStatus() {

        URI submissionId = submission.getId();
        boolean submitted = submission.getSubmitted();

        SubmissionStatus fromStatus = submission.getSubmissionStatus();
        SubmissionStatus toStatus;
        
        
        if (!submitted) {
            
            // before submission, we need too look at events for clues about status
            Set<URI> submissionEventUris = client.findAllByAttribute(SubmissionEvent.class, "submission", submissionId);
            List<SubmissionEvent> submissionEvents = submissionEventUris.stream()
                    .map(r -> client.readResource(r, SubmissionEvent.class))
                    .collect(Collectors.toList());
            
            toStatus = SubmissionStatusCalculator.calculatePreSubmissionStatus(submissionEvents);            
                        
        } else {
            
            Set<URI> depositUris = client.findAllByAttribute(Deposit.class, "submission", submissionId);
            List<Deposit> deposits = depositUris.stream()
                                        .map(r -> client.readResource(r, Deposit.class))
                                        .collect(Collectors.toList());
            
            Set<URI> repositoryCopyUris = client.findAllByAttribute(RepositoryCopy.class, "publication", submission.getPublication());
            List<RepositoryCopy> repositoryCopies = repositoryCopyUris.stream()
                    .map(r -> client.readResource(r, RepositoryCopy.class))
                    .collect(Collectors.toList());

            toStatus = SubmissionStatusCalculator.calculatePostSubmissionStatus(submission.getRepositories(), deposits, repositoryCopies);
            
        }

        try {
            SubmissionStatusCalculator.validateStatusChange(submitted, fromStatus, toStatus);
        } catch (RuntimeException ex) {
            String msg = String.format("Cannot change status from %s to %s on Submission %s. "
                                        + "The following explaination was provided: %s", fromStatus, toStatus, submissionId, ex.getMessage());
            throw new RuntimeException(msg);
        }
        
        return toStatus;
        
    }
    

    /**
     * Calculates the appropriate {@link SubmissionStatus} for the {@link Submission} provided. 
     * This is based on the status of associated {@link Deposit}s and {@link RepositoryCopy}s for 
     * {@code submitted} records, and {@link SubmissionEvent}s for unsubmitted records then updates 
     * the status as appropriate. <br><br>
     * The UI will typically have responsibility for updating the {@code submissionStatus} before
     * the {@link Submission} is submitted. Therefore, by default this service will not replace 
     * the existing status of an unsubmitted record unless the starting value was null (i.e. it has not 
     * been populated yet). To override this constraint, and replace the value anyway, use the method 
     * {@code calculateAndUpdateSubmissionStatus(boolean overrideUIStatus)} and supply a parameter of {@code true}
     * @return
     */
    public SubmissionStatus calculateAndUpdateSubmissionStatus() {
        return calculateAndUpdateSubmissionStatus(false);
    }
    
    
    /**
     * Calculates the appropriate {@link SubmissionStatus} for the {@link Submission} provided. 
     * This is based on the status of associated {@link Deposit}s and {@link RepositoryCopy}s for 
     * {@code submitted} records, and {@link SubmissionEvent}s for unsubmitted records then updates 
     * the status as appropriate. <br><br>
     * The UI will typically have responsibility for updating the {@code submissionStatus} before
     * the {@link Submission} is submitted. Therefore, by default this service will not replace 
     * the existing status of an unsubmitted record unless the starting value was null (i.e. it has not 
     * been populated yet). To override this constraint, set the {@code overrideUIStatus} parameter to 
     * {@code true}
     * @param overrideUIStatus - {@code true} will override the current pre-submission status on the 
     * {@code Submission} record, regardless of whether it was set by the UI.
     * {@code false} will not replace the current submission value, and favor the value set by the UI
     * @return
     */
    public SubmissionStatus calculateAndUpdateSubmissionStatus(boolean overrideUIStatus) {
        SubmissionStatus fromStatus = submission.getSubmissionStatus();
        SubmissionStatus toStatus = calculateSubmissionStatus();

        if (fromStatus==null || !fromStatus.equals(toStatus)) {
            
            //Applies special rule - this service should not overwrite what the UI has set the status to
            //unless the original status was null or this service has been specifically configured to do so 
            //by setting overrideUIStatus to true.
            if (!overrideUIStatus && !submission.getSubmitted() && fromStatus!=null) {
                LOG.info("Status of Submission {} did not change because pre-submission UI statuses are protected. "
                        + "The current status will stay as `{}`", submission.getId(), fromStatus);      
                return fromStatus;
            }
            
            submission.setSubmissionStatus(toStatus);
            LOG.info("Updating status of Submission {} from `{}` to `{}`", submission.getId(), fromStatus, toStatus);
            client.updateResource(submission);
            
        } else {
            LOG.debug("Status of Submission {} did not change. The current status is `{}`", submission.getId(), fromStatus);            
        }
        
        return toStatus;
    }
    
}
