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
import java.util.List;

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
     * String type name, specifically used to set "@type" in JSON serialization
     */
    @JsonProperty("@type")
    private String type = PassEntityType.SUBMISSION.getName();
    
    /** 
     * Status of Submission 
     */
    private Status status;

    /** 
     * Title of work represented by Submission e.g. the title of the article 
     */
    private String title;

    /** 
     * Contact name for corresponding author
     */
    private String corrAuthorName;

    /** 
     * Contact email for corresponding author 
     */
    private String corrAuthorEmail;
    
    /** 
     * Abstract for work represented by Submission 
     */
    @JsonProperty("abstract")
    private String submissionAbstract;

    /** 
     * DOI of item being submitted 
     */
    private String doi;

    /** 
     * URI of the journal the submission is part of (if article) 
     */
    private URI journal;

    /** 
     * Volume of journal that contains item (if article) 
     */
    private String volume;

    /** 
     * Issue of journal that contains item (if article) 
     */
    private String issue;

    /** 
     * List of places the submission will be deposited to 
     */
    private List<URI> deposits = new ArrayList<>();

    /** 
     * List of URIs for grants associated with the submission 
     */
    private List<URI> grants = new ArrayList<>();

    /** 
     * Date the record was submitted by the User through PASS 
     */
    @JsonSerialize(using = ZuluDateTimeSerializer.class)
    @JsonDeserialize(using = ZuluDateTimeDeserializer.class)
    private DateTime submittedDate;
    
    /** 
     * Source of Submission record 
     */
    private Source source;
    
    
    /** 
     * Possible statuses of a submission, this is dependent on information from the server and
     * is calculated using the status of associated Deposits
     */
    public enum Status {
        /**
         * The submission is not yet in compliance with applicable policies. 
         * One or more required Deposits have not been initiated.
         */
        @JsonProperty("non-compliant-not-started")
        NON_COMPLIANT_NOT_STARTED("non-compliant-not-started"),
        /**
         * All required Deposits for the Submission have been initiated, 
         * but at least one could not be completed and may require additional work by 
         * the user before being classified as compliant
         */
        @JsonProperty("non-compliant-in-progress")
        NON_COMPLIANT_IN_PROGRESS("non-compliant-in-progress"),
        /**
         * Submission is in compliance with all known applicable policies. 
         * All of the required Deposits have been initiated, but at least one has 
         * not yet reached the `Accepted` status.
         */
        @JsonProperty("compliant-in-progress")
        COMPLIANT_IN_PROGRESS("compliant-in-progress"),
        /**
         * Submission is in compliance with all known applicable policies. 
         * All related Deposits have a status of Accepted
         */
        @JsonProperty("compliant-complete")
        COMPLIANT_COMPLETE("compliant-complete");

        private String value;
        private Status(String value){
            this.value = value;
        }
        public String getValue() {
            return this.value;
        }
        
    }


    /** 
     * Source of the Submission, from a PASS user or imported from another source*/
    public enum Source {
        @JsonProperty("pass")
        PASS,
        @JsonProperty("other")
        OTHER
    }

    
    @Override
    public String getType() {
        return type;
    }
        
        
    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    
    /**
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    
    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    
    /**
     * @return corrAuthorName the corresponding author name
     */
    public String getCorrAuthorName() {
        return corrAuthorName;
    }

    
    /**
     * @param the corresponding author name
     */
    public void setCorrAuthorName(String corrAuthorName) {
        this.corrAuthorName = corrAuthorName;
    }

    
    /**
     * @return corrAuthorEmail the corresponding author email
     */
    public String getCorrAuthorEmail() {
        return corrAuthorEmail;
    }

    
    /**
     * @param the corresponding author email
     */
    public void setCorrAuthorEmail(String corrAuthorEmail) {
        this.corrAuthorEmail = corrAuthorEmail;
    }

    
    /**
     * @return the submissionAbstract
     */
    public String getSubmissionAbstract() {
        return submissionAbstract;
    }

    
    /**
     * @param submissionAbstract the submissionAbstract to set
     */
    public void setSubmissionAbstract(String submissionAbstract) {
        this.submissionAbstract = submissionAbstract;
    }

    
    /**
     * @return the doi
     */
    public String getDoi() {
        return doi;
    }

    
    /**
     * @param doi the doi to set
     */
    public void setDoi(String doi) {
        this.doi = doi;
    }

    
    /**
     * @return the URI of the Journal
     */
    public URI getJournal() {
        return journal;
    }

    
    /**
     * @param journal the URI journal to set
     */
    public void setJournal(URI journal) {
        this.journal = journal;
    }

    
    /**
     * @return the volume
     */
    public String getVolume() {
        return volume;
    }

    
    /**
     * @param volume the volume to set
     */
    public void setVolume(String volume) {
        this.volume = volume;
    }

    
    /**
     * @return the issue
     */
    public String getIssue() {
        return issue;
    }

    
    /**
     * @param issue the issue to set
     */
    public void setIssue(String issue) {
        this.issue = issue;
    }

    
    /**
     * @return the deposits
     */
    public List<URI> getDeposits() {
        return deposits;
    }

    
    /**
     * @param deposits the deposits to set
     */
    public void setDeposits(List<URI> deposits) {
        this.deposits = deposits;
    }

    
    /**
     * @return the URIs of grants associated with the submission
     */
    public List<URI> getGrants() {
        return grants;
    }

    
    /**
     * @param grants List of URIs of grants to set
     */
    public void setGrants(List<URI> grants) {
        this.grants = grants;
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
    

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Submission that = (Submission) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (corrAuthorName != null ? !corrAuthorName.equals(that.corrAuthorName) : that.corrAuthorName != null) return false;
        if (corrAuthorEmail != null ? !corrAuthorEmail.equals(that.corrAuthorEmail) : that.corrAuthorEmail != null) return false;
        if (submissionAbstract != null ? !submissionAbstract.equals(that.submissionAbstract) : that.submissionAbstract != null) return false;
        if (doi != null ? !doi.equals(that.doi) : that.doi != null) return false;
        if (journal != null ? !journal.equals(that.journal) : that.journal != null) return false;
        if (volume != null ? !volume.equals(that.volume) : that.volume != null) return false;
        if (issue != null ? !issue.equals(that.issue) : that.issue != null) return false;
        if (deposits != null ? !deposits.equals(that.deposits) : that.deposits != null) return false;
        if (grants != null ? !grants.equals(that.grants) : that.grants != null) return false;
        if (submittedDate != null ? !submittedDate.equals(that.submittedDate) : that.submittedDate != null) return false;
        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        return true;
    }

    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (corrAuthorName != null ? corrAuthorName.hashCode() : 0);
        result = 31 * result + (corrAuthorEmail != null ? corrAuthorEmail.hashCode() : 0);
        result = 31 * result + (submissionAbstract != null ? submissionAbstract.hashCode() : 0);
        result = 31 * result + (doi != null ? doi.hashCode() : 0);
        result = 31 * result + (journal != null ? journal.hashCode() : 0);
        result = 31 * result + (volume != null ? volume.hashCode() : 0);
        result = 31 * result + (issue != null ? issue.hashCode() : 0);
        result = 31 * result + (deposits != null ? deposits.hashCode() : 0);
        result = 31 * result + (grants != null ? grants.hashCode() : 0);
        result = 31 * result + (submittedDate != null ? submittedDate.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        return result;
    }

}
