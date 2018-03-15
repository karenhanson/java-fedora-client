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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Describes a Journal and the path of it's participation in PubMedCentral
 * @author Karen Hanson
 */

public class Journal extends PassEntity {

    /** 
     * String type name, specifically used to set "@type" in JSON serialization
     */
    @JsonProperty("@type")
    private String type = PassEntityType.JOURNAL.getName();
    
    /** 
     * Name of journal 
     */
    private String name;
    
    /** 
     * Array of ISSN(s) for Journal 
     */
    private List<String> issns;
    
    /** 
     * ID of publisher 
     */
    private URI publisher;
    
    /** 
     * National Library of Medicine Title Abbreviation
     */
    private String nlmta;

    /** 
     * This field indicates whether a journal participates in the NIH Public Access Program by sending final 
     * published article to PMC. If so, whether it requires additional processing fee.  
     */
    private PmcParticipation pmcParticipation;

    
    @Override
    public String getType() {
        return type;
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
     * @return the issns
     */
    public List<String> getIssns() {
        return issns;
    }

    
    /**
     * @param issn the issn list to set
     */
    public void setIssns(List<String> issn) {
        this.issns = issn;
    }

    
    /**
     * @return the publisher ID
     */
    public URI getPublisher() {
        return publisher;
    }

    
    /**
     * @param publisher the publisher to set
     */
    public void setPublisher(URI publisher) {
        this.publisher = publisher;
    }

    
    /**
     * @return the nlmta
     */
    public String getNlmta() {
        return nlmta;
    }

    
    /**
     * @param nlmta the nlmta to set
     */
    public void setNlmta(String nlmta) {
        this.nlmta = nlmta;
    }
    
    /**
     * @return the pmcParticipation
     */
    public PmcParticipation getPmcParticipation() {
        return pmcParticipation;
    }

    
    /**
     * @param pmcParticipation the pmcParticipation to set
     */
    public void setPmcParticipation(PmcParticipation pmcParticipation) {
        this.pmcParticipation = pmcParticipation;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Journal that = (Journal) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (issns != null ? !issns.equals(that.issns) : that.issns != null) return false;
        if (publisher != null ? !publisher.equals(that.publisher) : that.publisher != null) return false;
        if (nlmta != null ? !nlmta.equals(that.nlmta) : that.nlmta != null) return false;
        if (pmcParticipation != null ? !pmcParticipation.equals(that.pmcParticipation) : that.pmcParticipation != null) return false;
        return true;
    }

    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (issns != null ? issns.hashCode() : 0);
        result = 31 * result + (publisher != null ? publisher.hashCode() : 0);
        result = 31 * result + (nlmta != null ? nlmta.hashCode() : 0);
        result = 31 * result + (pmcParticipation != null ? pmcParticipation.hashCode() : 0);
        return result;
    }
    
    
}
