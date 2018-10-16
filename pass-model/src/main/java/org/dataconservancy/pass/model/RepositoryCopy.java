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

/**
 * A Repository Copy represents a copy of a Publication that exists in a target Repository. 
 * @author Karen Hanson
 */

public class RepositoryCopy extends PassEntity {
    
    /** 
     * IDs assigned by the repository 
     */
    private List<String> externalIds = new ArrayList<String>();
        
    /** 
     * Status of deposit 
     */
    private CopyStatus copyStatus;
    
    /** 
     * URL to access the item in the repository 
     */
    private URI accessUrl;
    
    /**
     * URI of the Publication that this Repository Copy is a copy of 
     */
    private URI publication;
    
    /** 
     * URI of Repository the Copy is in
     */
    private URI repository;

    
    /**
     * RepositoryCopy constructor
     */
    public RepositoryCopy() {};

    
    /**
     * Copy constructor, this will copy the values of the object provided into the new object
     * @param repositoryCopy the repositoryCopy to copy
     */
    public RepositoryCopy(RepositoryCopy repositoryCopy) {
        super(repositoryCopy);
        this.externalIds = new ArrayList<String>(repositoryCopy.externalIds);
        this.copyStatus = repositoryCopy.copyStatus;
        this.accessUrl = repositoryCopy.accessUrl;
        this.publication = repositoryCopy.publication;
        this.repository = repositoryCopy.repository;
    }
    
    
    /**
     * Possible repository copy statuses. Note that some repositories may not go through every status.
     */
    public enum CopyStatus {
        /**
        * The target Repository has rejected the Deposit
        */
        @JsonProperty("accepted")
        ACCEPTED("accepted"),
        /**
         * PASS has sent a package to the target Repository and is waiting for an update on the status
         */
        @JsonProperty("in-progress")
        IN_PROGRESS("in-progress"),
        /**
        * The target [Repository](Repository.md) has detected a problem that has caused the progress to stall.
        */
        @JsonProperty("stalled")
        STALLED("stalled"),
        /**
        * The target Repository has rejected the Deposit
        */
        @JsonProperty("complete")
        COMPLETE("complete"),

        /**
         * The RepositoryCopy has been rejected by the remote Repository.
         */
        @JsonProperty("rejected")
        REJECTED("rejected");

        private static final Map<String, CopyStatus> map = new HashMap<>(values().length, 1);  
        static {
          for (CopyStatus c : values()) map.put(c.value, c);
        }
        
        private String value;
        
        private CopyStatus(String value){
            this.value = value;
        }
        
        /** Parse the copy status.
         * 
         * @param status Serialized status.
         * @return Parsed status.
         */
        public static CopyStatus of(String status) {
            CopyStatus result = map.get(status);
            if (result == null) {
              throw new IllegalArgumentException("Invalid Copy Status: " + status);
            }
            return result;
        }

        @Override
        public String toString() {
            return this.value;
        }
        
    }
    
    /**
     * @return the externalIds
     */
    public List<String> getExternalIds() {
        return externalIds;
    }

    
    /**
     * @param externalIds the externalIds to set
     */
    public void setExternalIds(List<String> externalIds) {
        this.externalIds = externalIds;
    }

    
    /**
     * @return the repository copy status
     */
    public CopyStatus getCopyStatus() {
        return copyStatus;
    }
    
    
    /**
     * @return the accessUrl
     */
    public URI getAccessUrl() {
        return accessUrl;
    }

    
    /**
     * @param accessUrl the accessUrl to set
     */
    public void setAccessUrl(URI accessUrl) {
        this.accessUrl = accessUrl;
    }

    
    /**
     * @param copyStatus The repository's status to set
     */
    public void setCopyStatus(CopyStatus copyStatus) {
        this.copyStatus = copyStatus;
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

    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RepositoryCopy that = (RepositoryCopy) o;

        if (externalIds != null ? !externalIds.equals(that.externalIds) : that.externalIds != null) return false;
        if (copyStatus != null ? !copyStatus.equals(that.copyStatus) : that.copyStatus != null) return false;
        if (accessUrl != null ? !accessUrl.equals(that.accessUrl) : that.accessUrl != null) return false;
        if (publication != null ? !publication.equals(that.publication) : that.publication != null) return false;
        if (repository != null ? !repository.equals(that.repository) : that.repository != null) return false;
        return true;
    }
    

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (externalIds != null ? externalIds.hashCode() : 0);
        result = 31 * result + (copyStatus != null ? copyStatus.hashCode() : 0);
        result = 31 * result + (accessUrl != null ? accessUrl.hashCode() : 0);
        result = 31 * result + (publication != null ? publication.hashCode() : 0);
        result = 31 * result + (repository != null ? repository.hashCode() : 0);
        return result;
    }
    
}
