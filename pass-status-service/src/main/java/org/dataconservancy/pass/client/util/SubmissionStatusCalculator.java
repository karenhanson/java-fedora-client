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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dataconservancy.pass.model.Deposit;
import org.dataconservancy.pass.model.Deposit.DepositStatus;
import org.dataconservancy.pass.model.RepositoryCopy;
import org.dataconservancy.pass.model.RepositoryCopy.CopyStatus;
import org.dataconservancy.pass.model.Submission;
import org.dataconservancy.pass.model.Submission.SubmissionStatus;
import org.dataconservancy.pass.model.SubmissionEvent;
import org.dataconservancy.pass.model.SubmissionEvent.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.dataconservancy.pass.model.Submission.SubmissionStatus.APPROVAL_REQUESTED;
import static org.dataconservancy.pass.model.Submission.SubmissionStatus.CANCELLED;
import static org.dataconservancy.pass.model.Submission.SubmissionStatus.CHANGES_REQUESTED;
import static org.dataconservancy.pass.model.Submission.SubmissionStatus.COMPLETE;
import static org.dataconservancy.pass.model.Submission.SubmissionStatus.MANUSCRIPT_REQUIRED;
import static org.dataconservancy.pass.model.Submission.SubmissionStatus.NEEDS_ATTENTION;
import static org.dataconservancy.pass.model.Submission.SubmissionStatus.SUBMITTED;

/**
 * A utility to calculate and validate the Submission Status. Separate calculations are provided depending
 * on whether the Submission has been submitted or not since different data and rules apply 
 *
 * @author Karen Hanson
 */
public class SubmissionStatusCalculator  {

    private static final Logger LOG = LoggerFactory.getLogger(SubmissionStatusCalculator.class);
    
    /**
     * Calculates the appropriate post-Submission status based on data provided. 
     * <p>
     * Post-Submission calculations uses the {@code Deposits} and {@code RepositoryCopies} associated 
     * with a {@code Submission's} {@code Repositories} to determine the status of a Submission 
     * after it has been submitted ({@code Submission.status=true}.
     * </p>
     * @param repositories Submission repositories
     * @param deposits Submission deposits
     * @param repositoryCopies Submission repository copies
     * @return Calculated submission status
     */
    public static SubmissionStatus calculatePostSubmissionStatus(List<URI> repositories,
                                            List<Deposit> deposits,
                                            List<RepositoryCopy> repositoryCopies) {
        if (repositories==null) {repositories = new ArrayList<URI>();}
        if (deposits==null) {deposits = new ArrayList<Deposit>();}
        if (repositoryCopies==null) {repositoryCopies = new ArrayList<RepositoryCopy>();}
        Map<URI, SubmissionStatus> statusMap = mapPostSubmissionRepositoryStatuses(repositories, deposits, repositoryCopies);
        return calculateFromStatusMap(statusMap);        
    }

    
    /**
     * Calculates the appropriate pre-Submission status based on data provided. 
     * <p>
     * Pre-Submission calculations use the {@code SubmissionEvents} associated with the {@link Submission} 
     * to determine the status of a Submission before it has been submitted ({@code Submission.status=false}.
     * </p>
     * @param submissionEvents List of submission events
     * @return Calculated submission status
     */
    public static SubmissionStatus calculatePreSubmissionStatus(List<SubmissionEvent> submissionEvents) {
    if (submissionEvents==null) {submissionEvents = new ArrayList<SubmissionEvent>();}
        if (submissionEvents.size()>0) {
            // should only be used to set a status if the status is starting as null since UI is best for setting status, 
            // but will warn if the most recent event does not reflect current status
            EventType mostRecentEventType = Collections
                                                .max(submissionEvents, Comparator.comparing(SubmissionEvent::getPerformedDate))
                                                .getEventType();

            return mapEventTypeToSubmissionStatus(mostRecentEventType);
            
            
        } else {
            // has not yet been acted on, must be awaiting manuscript
            return MANUSCRIPT_REQUIRED;
        }
    }
    
    

    /**
     * Checks validity of {@link SubmissionStatus} change, will throw exception or output a warning if there are any 
     * validation issue with the change
     * @param submitted Whether the submission is submitted
     * @param fromStatus Original status
     * @param toStatus Desired new status
     */
    public static void validateStatusChange(boolean submitted, SubmissionStatus fromStatus, SubmissionStatus toStatus) {
        if (toStatus==null) {
            throw new IllegalArgumentException("The new status cannot be null");
        }
        if (submitted) {
            if (!toStatus.isSubmitted()) {
                String msg = String.format("Failed to validate the change of status due to conflicting data. The status "
                        + "`%s` cannot be assigned to a Submission that has not yet been submitted. There may be a data issue.", fromStatus);  
                throw new RuntimeException(msg);
            }  
        } else {
            if (toStatus.isSubmitted()) {
                String msg = String.format("Failed to validate the change of status due to conflicting data. The status "
                        + "`%s` cannot be assigned to a Submission that has already been submitted. There may be a data issue.", fromStatus);  
                throw new RuntimeException(msg);
            }
            if (fromStatus!=null && fromStatus.isSubmitted()) {
                String msg = String.format("Failed to validate the change of status due to conflicting data. The current "
                        + "status of the Submission is `%s`. This indicates that the Submission was already submitted and "
                        + "therefore should not be assigned a pre-submission status. There may be a data issue.", fromStatus);  
                throw new RuntimeException(msg);
            }
    
            if (fromStatus!=null && !toStatus.equals(fromStatus)) {
                LOG.warn("The current status of the Submission conflicts with the status calculated based on the most recent SubmissionEvent. "
                        + "The status on the Submission record is `{}`, while the calculated status is `{}`. The UI is responsible for setting "
                        + "pre-Submission statuses, but this mismatch may indicate a data issue.", fromStatus, toStatus);                 
            }  
        }
        
    }
    
    
    private static SubmissionStatus calculateFromStatusMap(Map<URI, SubmissionStatus> submissionRepositoryStatusMap) {
        //we only need to know if a status is present or not to determine combined status
        Set<SubmissionStatus> statuses = new HashSet<SubmissionStatus>(submissionRepositoryStatusMap.values());        
        
        if (statuses.contains(NEEDS_ATTENTION)) {
            return NEEDS_ATTENTION;
        } else if (statuses.size()==1 && statuses.contains(COMPLETE)) {
            return COMPLETE;
        } else {
            return SUBMITTED;
        }
    }
    
    private static Map<URI, SubmissionStatus> mapPostSubmissionRepositoryStatuses(List<URI> repositories, 
                                                                                 List<Deposit> deposits, 
                                                                                 List<RepositoryCopy> repoCopies) {

        Map<URI, SubmissionStatus> statusMap = new HashMap<URI, SubmissionStatus>();
        
        for (URI repositoryUri : repositories) {
            statusMap.put(repositoryUri, null);            
        }
        for (Deposit d : deposits) {
            if (DepositStatus.REJECTED.equals(d.getDepositStatus())) {
                statusMap.put(d.getRepository(), NEEDS_ATTENTION);                
            } else {
                statusMap.put(d.getRepository(), SUBMITTED);
            }
        }
        for (RepositoryCopy rc : repoCopies) {
            URI repoId = rc.getRepository();
            CopyStatus copyStatus = rc.getCopyStatus();
            if (CopyStatus.COMPLETE.equals(copyStatus)) {
                statusMap.put(repoId, COMPLETE);
            } else if (CopyStatus.REJECTED.equals(copyStatus) || CopyStatus.STALLED.equals(copyStatus)) {
                statusMap.put(repoId, NEEDS_ATTENTION);
            } else {
                // There is a RepositoryCopy and nothing is wrong. Note in this state, it will overwrite a status of 
                // REJECTED on the Deposit. This assumes that if all is OK with the RepositoryCopy things have been 
                // resolved.
                statusMap.put(repoId, SubmissionStatus.SUBMITTED);
            }
        }
        
        return statusMap;
    }
    
    private static SubmissionStatus mapEventTypeToSubmissionStatus(EventType eventType) {
        switch (eventType) {
            case APPROVAL_REQUESTED: return APPROVAL_REQUESTED;
            case APPROVAL_REQUESTED_NEWUSER: return APPROVAL_REQUESTED;  
            case SUBMITTED : return SUBMITTED;
            case CANCELLED: return CANCELLED;
            case CHANGES_REQUESTED: return CHANGES_REQUESTED;
            default: return null;
        }
    }

}