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

/**
 * Describes a Policy. Policies determine the rules that need to be followed by a Submission.
 * @author Karen Hanson
 */

public class Policy extends PassEntity {

    /** 
     * String type name, specifically used to set "@type" in JSON serialization
     */
    @JsonProperty("@type")
    private String type = PassEntityType.POLICY.getName();
    
    /** 
     * Title of policy e.g. "NIH Public Access Policy" 
     */
    private String title;
    
    /** 
     * Several sentence description of policy 
     */
    private String description;
    
    /** 
     * List of URIs for repositories that can satisfying this policy 
     */
    private List<URI> repositories = new ArrayList<>();
    
    /** 
     * True if this policy should appear for all submission. This can be used to identify a policy or 
     * policies that should show up for everything 
     */
    private Boolean isDefault;

    
    @Override
    public String getType() {
        return type;
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
     * @return the list of URIs of repositories
     */
    public List<URI> getRepositories() {
        return repositories;
    }

    
    /**
     * @param repositories list of URIs of repositories to set
     */
    public void setRepositories(List<URI> repositories) {
        this.repositories = repositories;
    }

    
    /**
     * @return the isDefault
     */
    public Boolean getIsDefault() {
        return isDefault;
    }

    
    /**
     * @param isDefault the isDefault to set
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Policy that = (Policy) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (repositories != null ? !repositories.equals(that.repositories) : that.repositories != null) return false;
        if (isDefault != null ? !isDefault.equals(that.isDefault) : that.isDefault != null) return false;
        return true;
    }

    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (repositories != null ? repositories.hashCode() : 0);
        result = 31 * result + (isDefault != null ? isDefault.hashCode() : 0);
        return result;
    }
       
}
