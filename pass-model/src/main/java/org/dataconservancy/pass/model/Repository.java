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

/**
 * Describes a Repository. A Repository is the target of a Deposit.
 * @author Karen Hanson
 */

public class Repository extends PassEntity {
    
    /** 
     * Name of repository e.g. "PubMed Central" 
     */
    private String name;

    /** 
     * Several sentence description of repository 
     */
    private String description;

    /** 
     * URL to the homepage of the repository so that PASS users can view the platform before deciding whether to participate in it 
     */
    private URI url;

    /** 
     * Stringified JSON representing a form template to be loaded by the front-end when this Repository is selected
     */
    private String formSchema;

    
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
     * @return the url
     */
    public URI getUrl() {
        return url;
    }

    
    /**
     * @param url the url to set
     */
    public void setUrl(URI url) {
        this.url = url;
    }

    
    /**
     * @return the formSchema
     */
    public String getFormSchema() {
        return formSchema;
    }

    
    /**
     * @param url the url to set
     */
    public void setFormSchema(String formSchema) {
        this.formSchema = formSchema;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Repository that = (Repository) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (formSchema != null ? !formSchema.equals(that.formSchema) : that.formSchema != null) return false;
        return true;
    }

    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (formSchema != null ? formSchema.hashCode() : 0);
        return result;
    }
    
}
