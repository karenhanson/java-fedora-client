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
 * Files are associated with a Submissions to be used to form Deposits into Repositories
 * @author Karen Hanson
 */
public class File extends PassEntity {
    
    /** 
     * Name of file, defaults to filesystem.name 
     */
    private String name;
        
    /** 
     * URI to the bytestream that Deposit services will use to retrieve the bytestream for Deposit
     */
    private URI uri;
    
    /** 
     * Description of file provided by User
     */
    private String description;
    
    /** 
     * Role of the file e.g. manuscript, supplemental
     */
    private FileRole fileRole;

    /** 
     * Mime-type of file
     */
    private String mimeType;

    /** 
     * URI of the Submission the File is a part of
     */
    private URI submission;
    

    /** 
     * list of possible File Roles 
     */
    public enum FileRole {
        /**
        * Author accepted manuscript
        */
        @JsonProperty("manuscript")
        MANUSCRIPT("manuscript"),
        
        /**
        * Supplemental material for the Publication
        */
        @JsonProperty("supplemental")
        SUPPLEMENTAL("supplemental"),
        
        /**
        * An image, data plot, map, or schematic
        */
        @JsonProperty("figure")
        FIGURE("figure"),
        
        /**
        * Tabular data
        */
        @JsonProperty("table")
        TABLE("table");

        private static final Map<String, FileRole> map = new HashMap<>(values().length, 1);  
        static {
          for (FileRole r : values()) map.put(r.value, r);
        }
        
        private String value;
        
        private FileRole(String value){
            this.value = value;
        }
        
        public static FileRole of(String role) {
            FileRole result = map.get(role);
            if (result == null) {
              throw new IllegalArgumentException("Invalid File Role: " + role);
            }
            return result;
        }
        
        @Override
        public String toString() {
            return this.value;
        }
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    
    /**
     * @return the uri
     */
    public URI getUri() {
        return uri;
    }

    
    /**
     * @param uri the uri to set
     */
    public void setUri(URI uri) {
        this.uri = uri;
    }

    
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    
    /**
     * @return the fileRole
     */
    public FileRole getFileRole() {
        return fileRole;
    }

    
    /**
     * @param fileRole the fileRole to set
     */
    public void setFileRole(FileRole fileRole) {
        this.fileRole = fileRole;
    }

    
    /**
     * @return the mimeType
     */
    public String getMimeType() {
        return mimeType;
    }

    
    /**
     * @param mimeType the mimeType to set
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
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

        File that = (File) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (fileRole != null ? !fileRole.equals(that.fileRole) : that.fileRole != null) return false;
        if (mimeType != null ? !mimeType.equals(that.mimeType) : that.mimeType != null) return false;
        if (submission != null ? !submission.equals(that.submission) : that.submission != null) return false;
        return true;
    }

    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (fileRole != null ? fileRole.hashCode() : 0);
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        result = 31 * result + (submission != null ? submission.hashCode() : 0);
        return result;
    }
    
}
