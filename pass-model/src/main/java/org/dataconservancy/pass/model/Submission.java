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

import java.net.URI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.dataconservancy.pass.model.support.ZuluDateTimeDeserializer;
import org.dataconservancy.pass.model.support.ZuluDateTimeSerializer;
import org.joda.time.DateTime;

/**
 * Submission model. Contains details of work being submitted, where it is being deposited to, related Grants etc.
 * @author Karen Hanson
 */

public class Submission extends PassEntity {

    /** 
     * Stringified JSON representation of metadata captured by the relevant repository forms
     */
    private String metadata;
    
    /** 
     * Source of Submission record 
     */
    private Source source;
    
    /** 
     * When true, this value signals that the Submission will no longer be edited by the User. 
     * It indicates to Deposit services that it can generate Deposits for any Repositories that need one. 
     */
    private Boolean submitted;

    /** 
     * Date the record was submitted by the User through PASS 
     */
    @JsonSerialize(using = ZuluDateTimeSerializer.class)
    @JsonDeserialize(using = ZuluDateTimeDeserializer.class)
    private DateTime submittedDate;
    
    /** 
     * Status of Submission. Focused on informing User of current state of Submission.
     */
    private SubmissionStatus submissionStatus;

    /** 
     * Overall status of Submission's Deposits
     */
    private AggregatedDepositStatus aggregatedDepositStatus;
    
    /**
     * URI of Publication associated with the Submission
     */
    private URI publication;
    
    /** 
     * List of repositories that the submission will be deposited to
     * Note that the order of the list does not carry any particular significance 
     */
    private List<URI> repositories = new ArrayList<>();

    /**
     * URI of the User responsible for managing and submitting the Submission 
     * this may be a a `User.id` or may temporarily hold a `mailto:` URI containing
     * the encoded name and email address of the submitter (e.g. "mailto:John%20Doe%3Cjohndoe%40example.org%3E")
     * 
     */
    private URI submitter;

    /**
     * URI of the User(s) who prepared, or who could contribute to the preparation of, the Submission.
     * Prepares can edit the content of the Submission (describe the Publication, add Grants, add Files, 
     * select Repositories) but cannot approve any Repository agreements or submit the Publication.
     * Note that the order of the list does not carry any particular significance
     */
    private List<URI> preparers = new ArrayList<>();
    
    /** 
     * List of URIs for grants associated with the submission 
     * Note that the order of the list does not carry any particular significance
     */
    private List<URI> grants = new ArrayList<>();

    
    /**
     * Submission constructor
     */
    public Submission() {};

    
    /**
     * Copy constructor, this will copy the values of the object provided into the new object
     * @param submission the submission to copy
     */
    public Submission(Submission submission) {
        super(submission);
        this.metadata = submission.metadata;
        this.source = submission.source;
        this.submitted = submission.submitted;
        this.submittedDate = submission.submittedDate;
        this.submissionStatus = submission.submissionStatus;
        this.aggregatedDepositStatus = submission.aggregatedDepositStatus;
        this.publication = submission.publication;
        this.repositories = new ArrayList<URI>(submission.repositories);
        this.submitter = submission.submitter;
        this.preparers = new ArrayList<URI>(submission.preparers);
        this.grants = new ArrayList<URI>(submission.grants);
    }
    
    
    /** 
     *  The possible values for the Submission.submissionStatus field. 
     *  Note that not all Submissions will go through every status.
     */
    public enum SubmissionStatus {
        /**
         * When the PASS system identifies a need for a User to submit a Publication to a particular Repository, 
         * it will create a new Submission record with this status in order to prompt the User to provide the 
         * document and complete the Submission.
         */
        @JsonProperty("manuscript-required")
        MANUSCRIPT_REQUIRED("manuscript-required", false),
        
        /**
         * A Submission was prepared by a preparer but now needs the submitter to approve and submit it or provide 
         * feedback.
         */        
        @JsonProperty("approval-requested")
        APPROVAL_REQUESTED("approval-requested", false),
        
        /**
         * A Submission was prepared by a preparer, but on review by the submitter, a change was requested. 
         * The Submission has been handed back to the preparer for editing.
         */
        @JsonProperty("changes-requested")
        CHANGES_REQUESTED("changes-requested", false),

        /**
         * A Submission was prepared and then cancelled by the submitter or preparer without being submitted. 
         * No further edits can be made to the Submission.
         */
        @JsonProperty("cancelled")
        CANCELLED("cancelled", false),

        /**
         * The submit button has been pressed through the UI. From this status forward, the Submission 
         * becomes read-only to both the submitter and preparers. This status indicates that either 
         * (a) the Submission is still being processed, or (b) PASS has finished the Deposit process, 
         * but there is not yet confirmation from the Repository that indicates the Submission was valid. 
         * Some Submissions may remain in a submitted state indefinitely depending on PASS's capacity to 
         * verify completion of the process in the target Repository.
         */
        @JsonProperty("submitted")
        SUBMITTED("submitted", true),

        /**
         * Indicates that a User action may be required outside of PASS. The Submission is stalled or 
         * has been rejected by one or more Repository
         */
        @JsonProperty("needs-attention")
        NEEDS_ATTENTION("needs-attention", true),

        /**
         * The target repositories have all received a copy of the Submission, and have indicated that 
         * the Submission was successful.
         */
        @JsonProperty("complete")
        COMPLETE("complete", true);

        private static final Map<String, SubmissionStatus> map = new HashMap<>(values().length, 1);  
        static {
          for (SubmissionStatus s : values()) map.put(s.value, s);
        }
        
        private String value;
        
        private boolean submitted;
        
        private SubmissionStatus(String value, boolean submitted){
            this.value = value;
            this.submitted = submitted;
        }
        
        
        /** Parse the submission status.
         * 
         * @param status Serialized submission status string
         * @return The submission status
         */
        public static SubmissionStatus of(String status) {
            SubmissionStatus result = map.get(status);
            if (result == null) {
              throw new IllegalArgumentException("Invalid Submission Status: " + status);
            }
            return result;
          }

        /** Determine if submitted.
         * 
         * @return True if submitted.
         */
        public boolean isSubmitted() {
            return submitted;
        }
        
        @Override
        public String toString() {
            return this.value;
        }
        
    }

    
    /** 
     * Possible aggregatedDepositStatus of a submission, this is dependent on information from the server and
     * is calculated using the status of associated Deposits
     */
    public enum AggregatedDepositStatus {
        /**
         * No Deposits have been initiated for the Submission
         */
        @JsonProperty("not-started")
        NOT_STARTED("not-started"),
        
        /**
         * One or more Deposits for the Submission have been initiated, and at least one 
         * has not reached the status of "accepted"
         */
        @JsonProperty("in-progress")
        IN_PROGRESS("in-progress"),
        
        /**
         * One or more Deposits for the Submission has a status of "failed"
         */
        @JsonProperty("failed")
        FAILED("failed"), 
        
        /**
         * All related Deposits have a status of "accepted"
         */
        @JsonProperty("accepted")
        ACCEPTED("accepted"), 
        
        /**
         * One or more Deposits for the Submission has a status of "rejected"
         */
        @JsonProperty("rejected")
        REJECTED("rejected");

        private static final Map<String, AggregatedDepositStatus> map = new HashMap<>(values().length, 1);  
        static {
          for (AggregatedDepositStatus s : values()) map.put(s.value, s);
        }
        
        private String value;
        
        private AggregatedDepositStatus(String value){
            this.value = value;
        }
        
        /** 
         * Parse the aggregated deposit status.
         * 
         * @param status Serialized status
         * @return parsed deposit status.
         */
        public static AggregatedDepositStatus of(String status) {
            AggregatedDepositStatus result = map.get(status);
            if (result == null) {
              throw new IllegalArgumentException("Invalid Aggregated Deposit Status: " + status);
            }
            return result;
          }

        @Override
        public String toString() {
            return this.value;
        }
        
    }
    

    /** 
     * Source of the Submission, from a PASS user or imported from another source*/
    public enum Source {
        
        /** PASS source */
        @JsonProperty("pass")
        PASS("pass"),
        
        /** Other source */
        @JsonProperty("other")
        OTHER("other");
        
        private String value;
        
        private Source(String value){
            this.value = value;
        }
        
        @Override
        public String toString() {
            return this.value;
        }
    }
    
    /**
    * @return the metadata
    */
    public String getMetadata() {
        return metadata;
    }

    
    /**
     * @param metadata the metadata to set
     */
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    
    /**
     * @return the source
     */
    public Source getSource() {
        return source;
    }

    
    /**
     * @param source the source to set
     */
    public void setSource(Source source) {
        this.source = source;
    }

    
    /**
     * @return the submitted
     */
    public Boolean calculate() {
        return submitted;
    }

    
    /**
     * @return Boolean indicating submitted
     */
    public Boolean getSubmitted() {
        return submitted;
    }


    
    /**
     * @param submitted the submitted to set
     */
    public void setSubmitted(Boolean submitted) {
        this.submitted = submitted;
    }

    
    /**
     * @return the submittedDate
     */
    public DateTime getSubmittedDate() {
        return submittedDate;
    }

    
    /**
     * @param submittedDate the submittedDate to set
     */
    public void setSubmittedDate(DateTime submittedDate) {
        this.submittedDate = submittedDate;
    }

    
    /**
     * @return the submissionStatus
     */
    public SubmissionStatus getSubmissionStatus() {
        return submissionStatus;
    }

    
    /**
     * @return the aggregatedDepositStatus
     */
    public AggregatedDepositStatus getAggregatedDepositStatus() {
        return aggregatedDepositStatus;
    }

    
    /**
     * @param aggregatedDepositStatus the aggregatedDepositStatus to set
     */
    public void setAggregatedDepositStatus(AggregatedDepositStatus aggregatedDepositStatus) {
        this.aggregatedDepositStatus = aggregatedDepositStatus;
    }

    
    /**
     * @param submissionStatus the submissionStatus to set
     */
    public void setSubmissionStatus(SubmissionStatus submissionStatus) {
        this.submissionStatus = submissionStatus;
    }

    
    /**
     * @return the publication
     */
    public URI getPublication() {
        return publication;
    }

    
    /**
     * @param publication the publication to set
     */
    public void setPublication(URI publication) {
        this.publication = publication;
    }

    
    /**
     * @return the repositories
     */
    public List<URI> getRepositories() {
        return repositories;
    }

    
    /**
     * @param repositories the repositories to set
     */
    public void setRepositories(List<URI> repositories) {
        this.repositories = repositories;
    }
    
    
    /**
     * @return the submitter
     */
    public URI getSubmitter() {
        return submitter;
    }

    
    /** Set the submitter
     * @param submitter the submitter to set
     */
    public void setSubmitter(URI submitter) {
        this.submitter = submitter;
    }

    
    /** Gets the list of preparers
     * @return the preparers
     */
    public List<URI> getPreparers() {
        return preparers;
    }

    
    /**
     * @param preparers the preparers to set
     */
    public void setPreparers(List<URI> preparers) {
        this.preparers = preparers;
    }

    
    /**
     * @return the grants
     */
    public List<URI> getGrants() {
        return grants;
    }

    
    /**
     * @param grants the grants to set
     */
    public void setGrants(List<URI> grants) {
        this.grants = grants;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Submission that = (Submission) o;

        if (metadata != null ? !metadata.equals(that.metadata) : that.metadata != null) return false;
        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        if (submitted != null ? !submitted.equals(that.submitted) : that.submitted != null) return false;
        if (submittedDate != null ? !submittedDate.equals(that.submittedDate) : that.submittedDate != null) return false;        
        if (submissionStatus != null ? !submissionStatus.equals(that.submissionStatus) : that.submissionStatus != null) return false;
        if (aggregatedDepositStatus != null ? !aggregatedDepositStatus.equals(that.aggregatedDepositStatus) : that.aggregatedDepositStatus != null) return false;
        if (publication != null ? !publication.equals(that.publication) : that.publication != null) return false;
        if (repositories != null ? !repositories.equals(that.repositories) : that.repositories != null) return false;
        if (submitter != null ? !submitter.equals(that.submitter) : that.submitter != null) return false;
        if (preparers != null ? !preparers.equals(that.preparers) : that.preparers != null) return false;
        if (grants != null ? !grants.equals(that.grants) : that.grants != null) return false;
        return true;
    }

    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (metadata != null ? metadata.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (submitted != null ? submitted.hashCode() : 0);
        result = 31 * result + (submittedDate != null ? submittedDate.hashCode() : 0);
        result = 31 * result + (submissionStatus != null ? submissionStatus.hashCode() : 0);
        result = 31 * result + (aggregatedDepositStatus != null ? aggregatedDepositStatus.hashCode() : 0);
        result = 31 * result + (publication != null ? publication.hashCode() : 0);
        result = 31 * result + (repositories != null ? repositories.hashCode() : 0);
        result = 31 * result + (submitter != null ? submitter.hashCode() : 0);
        result = 31 * result + (preparers != null ? preparers.hashCode() : 0);
        result = 31 * result + (grants != null ? grants.hashCode() : 0);
        return result;
    }

}
