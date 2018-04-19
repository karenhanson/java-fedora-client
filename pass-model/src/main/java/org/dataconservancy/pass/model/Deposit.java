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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Submission can have multiple Deposits, each to a different Repository. This describes a single deposit to a Repository and captures 
 * its current status.
 * @author Karen Hanson
 */

public class Deposit extends PassEntity {

    /** 
     * String type name, specifically used to set "@type" in JSON serialization
     */
    @JsonProperty("@type")
    private String type = PassEntityType.DEPOSIT.getName();
    
    /** 
     * Status of deposit 
     */
    private DepositStatus depositStatus;
    
    /** 
     * URI of Repository being deposited to 
     */
    private URI repository;
    
    /** 
     * ID assigned by repository 
     */
    private String assignedId;
    
    /** 
     * URL to access the item in the repository 
     */
    private String accessUrl;
    
    /** 
     * True if deposit was requested by the user rather than resulting from a required policy 
     */
    private Boolean requested;
    
    /**
     * URI of the Submission that this Deposit is a part of
     */
    private URI submission;
    
    /**
     * Possible deposit statuses. Note that some repositories may not go through every status.
     */
    public enum DepositStatus {
        /**
         * PASS has sent a package to the target Repository and is waiting for an update on the status
         */
        @JsonProperty("submitted")
        SUBMITTED("submitted"),
         /**
         * The target Repository has rejected the Deposit
         */
        @JsonProperty("accepted")
        ACCEPTED("accepted"),
        /**
        * The target Repository has accepted the files into the repository. More steps may be performed by the Repository, but the 
        * requirements of the Deposit have been satisfied
        */
       @JsonProperty("rejected")
       REJECTED("rejected");
                
        private String value;
        private DepositStatus(String value){
            this.value = value;
        }
        public String getValue() {
            return this.value;
        }
    }

    
    @Override
    public String getType() {
        return type;
    }
    
    
    /**
     * @return the status
     */
    public DepositStatus getDepositStatus() {
        return depositStatus;
    }

    
    /**
     * @param depositStatus the status to set
     */
    public void setDepositStatus(DepositStatus depositStatus) {
        this.depositStatus = depositStatus;
    }

    
    /**
     * @return the URI of the repository
     */
    public URI getRepository() {
        return repository;
    }

    
    /**
     * @param repository the URI of the repository to set
     */
    public void setRepository(URI repository) {
        this.repository = repository;
    }

    
    /**
     * @return the assignedId
     */
    public String getAssignedId() {
        return assignedId;
    }

    
    /**
     * @param assignedId the assignedId to set
     */
    public void setAssignedId(String assignedId) {
        this.assignedId = assignedId;
    }

    
    /**
     * @return the accessUrl
     */
    public String getAccessUrl() {
        return accessUrl;
    }

    
    /**
     * @param accessUrl the accessUrl to set
     */
    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    
    /**
     * @return the requested
     */
    public Boolean getRequested() {
        return requested;
    }

    
    /**
     * @param requested the requested to set
     */
    public void setRequested(Boolean requested) {
        this.requested = requested;
    }

    
    /**
     * @return the submission
     */
    public URI getSubmission() {
        return submission;
    }


    /**
     * @param submission the submission to set
     */
    public void setSubmission(URI submission) {
        this.submission = submission;
    }
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Deposit that = (Deposit) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (depositStatus != null ? !depositStatus.equals(that.depositStatus) : that.depositStatus != null) return false;
        if (repository != null ? !repository.equals(that.repository) : that.repository != null) return false;
        if (assignedId != null ? !assignedId.equals(that.assignedId) : that.assignedId != null) return false;
        if (accessUrl != null ? !accessUrl.equals(that.accessUrl) : that.accessUrl != null) return false;
        if (requested != null ? !requested.equals(that.requested) : that.requested != null) return false;
        if (submission != null ? !submission.equals(that.submission) : that.submission != null) return false;
        return true;
    }
    

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (depositStatus != null ? depositStatus.hashCode() : 0);
        result = 31 * result + (repository != null ? repository.hashCode() : 0);
        result = 31 * result + (assignedId != null ? assignedId.hashCode() : 0);
        result = 31 * result + (accessUrl != null ? accessUrl.hashCode() : 0);
        result = 31 * result + (requested != null ? requested.hashCode() : 0);
        result = 31 * result + (submission != null ? submission.hashCode() : 0);
        return result;
    }
    
}
