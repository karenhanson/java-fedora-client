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

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Submission can have multiple Deposits, each to a different Repository. This describes a single deposit to a Repository and captures 
 * its current status.
 * @author Karen Hanson
 */

public class Deposit extends PassEntity {
    
    /** 
     * A URL or some kind of reference that can be dereferenced, entity body parsed, and used to determine the status of Deposit
     */
    private String depositStatusRef;
    
    /** 
     * Status of deposit 
     */
    private DepositStatus depositStatus;
    
    /**
     * URI of the Submission that this Deposit is a part of
     */
    private URI submission;
   
    /** 
     * URI of Repository being deposited to 
     */
    private URI repository;
    
    /**
     * URI of the Repository Copy representing the copy that is reltaed to this Deposit. The value is null if there is no copy
     */
    private URI repositoryCopy;
    
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
        REJECTED("rejected"),
        /**
         * A failure occurred performing the deposit; it may be re-tried later.
         */
        @JsonProperty("failed")
        FAILED("failed");

        private static final Map<String, DepositStatus> map = new HashMap<>(values().length, 1);  
        static {
          for (DepositStatus d : values()) map.put(d.value, d);
        }
        
        private String value;
        private DepositStatus(String value){
            this.value = value;
        }
        
        /** 
         * Parse deposit status
         * 
         * @param status status string
         * @return parsed status
         */
        public static DepositStatus of(String status) {
            DepositStatus result = map.get(status);
            if (result == null) {
              throw new IllegalArgumentException("Invalid Deposit Status: " + status);
            }
            return result;
        }
        
        @Override
        public String toString() {
            return this.value;
        }
        
    }
    
    /**
     * @return the deposit status
     */
    public DepositStatus getDepositStatus() {
        return depositStatus;
    }

    
    /**
     * @param depositStatus status the deposit status to set
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
     * @return the depositStatusRef
     */
    public String getDepositStatusRef() {
        return depositStatusRef;
    }

    
    /**
     * @param depositStatusRef the depositStatusRef to set
     */
    public void setDepositStatusRef(String depositStatusRef) {
        this.depositStatusRef = depositStatusRef;
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

    
    /**
     * @return the repositoryCopy
     */
    public URI getRepositoryCopy() {
        return repositoryCopy;
    }


    /**
     * @param repositoryCopy the repositoryCopy to set
     */
    public void setRepositoryCopy(URI repositoryCopy) {
        this.repositoryCopy = repositoryCopy;
    }
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Deposit that = (Deposit) o;

        if (depositStatusRef != null ? !depositStatusRef.equals(that.depositStatusRef) : that.depositStatusRef != null) return false;
        if (depositStatus != null ? !depositStatus.equals(that.depositStatus) : that.depositStatus != null) return false;
        if (submission != null ? !submission.equals(that.submission) : that.submission != null) return false;
        if (repository != null ? !repository.equals(that.repository) : that.repository != null) return false;
        if (repositoryCopy != null ? !repositoryCopy.equals(that.repositoryCopy) : that.repositoryCopy != null) return false;
        return true;
    }
    

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (depositStatusRef != null ? depositStatusRef.hashCode() : 0);
        result = 31 * result + (depositStatus != null ? depositStatus.hashCode() : 0);
        result = 31 * result + (submission != null ? submission.hashCode() : 0);
        result = 31 * result + (repository != null ? repository.hashCode() : 0);
        result = 31 * result + (repositoryCopy != null ? repositoryCopy.hashCode() : 0);
        return result;
    }
    
}
