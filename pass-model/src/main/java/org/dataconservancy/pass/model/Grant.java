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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.dataconservancy.pass.model.support.ZuluDateTimeDeserializer;
import org.dataconservancy.pass.model.support.ZuluDateTimeSerializer;
import org.joda.time.DateTime;

/**
 * Grant model for the PASS system
 * @author Karen Hanson
 */

public class Grant extends PassEntity {

    /** 
     * String type name, specifically used to set "@type" in JSON serialization
     */
    @JsonProperty("@type")
    private String type = PassEntityType.GRANT.getName();
    
    /** 
     * Award number from funder 
     */
    private String awardNumber;
    
    /** 
     * Status of award 
     */
    private AwardStatus awardStatus;
    
    /** 
     * Award number or ID assigned to the grant within the researcher's institution 
     */
    private String localAwardId;
    
    /** 
     * Title of the research project 
     */
    private String projectName;
    
    /** 
     * The URI of the funder.id of the sponsor that is the original source of the funds 
     */
    private URI primaryFunder;
    
    /** 
     * The URI of the funder.id of the organization from which funds are directly received 
     */
    private URI directFunder;

    /** 
     * URI of the Principal investigator 
     */
    private URI pi;
    
    /** 
     * Co-principal investigator list 
     */
    private List<URI> coPis;
    
    /* TODO: need to decide how to handle these dates that have no times. 
     * Using zulu date for now**/
    /** Date the grant was awarded */
    @JsonSerialize(using = ZuluDateTimeSerializer.class)
    @JsonDeserialize(using = ZuluDateTimeDeserializer.class)
    private DateTime awardDate;
    
    /** Date the grant started */
    @JsonSerialize(using = ZuluDateTimeSerializer.class)
    @JsonDeserialize(using = ZuluDateTimeDeserializer.class)
    private DateTime startDate;
    
    /** Date the grant ended */
    @JsonSerialize(using = ZuluDateTimeSerializer.class)
    @JsonDeserialize(using = ZuluDateTimeDeserializer.class)
    private DateTime endDate;
    
    /** List of Submission URIs related to Grant */
    private List<URI> submissions;
    
    /** Status of award/grant */
    public enum AwardStatus {
        @JsonProperty("active")
        ACTIVE,
        @JsonProperty("pre-award")
        PRE_AWARD,
        @JsonProperty("terminated")
        TERMINATED;
    }

    
    @Override
    public String getType() {
        return type;
    }
    
    
    /**
     * @return the awardNumber
     */
    public String getAwardNumber() {
        return awardNumber;
    }

    
    /**
     * @param awardNumber the awardNumber to set
     */
    public void setAwardNumber(String awardNumber) {
        this.awardNumber = awardNumber;
    }


    /**
     * @return the awardStatus
     */
    public AwardStatus getAwardStatus() {
        return awardStatus;
    }

    
    /**
     * @param awardStatus the awardStatus to set
     */
    public void setAwardStatus(AwardStatus awardStatus) {
        this.awardStatus = awardStatus;
    }
    
    
    /**
     * @return the localAwardId
     */
    public String getLocalAwardId() {
        return localAwardId;
    }

    
    /**
     * @param localAwardId the localAwardId to set
     */
    public void setLocalAwardId(String localAwardId) {
        this.localAwardId = localAwardId;
    }

    
    /**
     * @return the projectName
     */
    public String getProjectName() {
        return projectName;
    }

    
    /**
     * @param projectName the projectName to set
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    
    /**
     * @return the URI of the primaryFunder
     */
    public URI getPrimaryFunder() {
        return primaryFunder;
    }

    
    /**
     * @param primaryFunder the URI of the primaryFunder to set
     */
    public void setPrimaryFunder(URI primaryFunder) {
        this.primaryFunder = primaryFunder;
    }

    
    /**
     * @return the URI of the directFunder
     */
    public URI getDirectFunder() {
        return directFunder;
    }

    
    /**
     * @param directFunder the URI of the directFunder to set
     */
    public void setDirectFunder(URI directFunder) {
        this.directFunder = directFunder;
    }

    
    /**
     * @return the URI of the pi
     */
    public URI getPi() {
        return pi;
    }

    
    /**
     * @param pi the URI of the pi to set
     */
    public void setPi(URI pi) {
        this.pi = pi;
    }

    
    /**
     * @return the coPi Person URIs
     */
    public List<URI> getCoPis() {
        return coPis;
    }

    
    /**
     * @param coPis the Person URIs of the coPis to set
     */
    public void setCoPis(List<URI> coPis) {
        this.coPis = coPis;
    }


    /**
     * @return the awardDate
     */
    public DateTime getAwardDate() {
        return awardDate;
    }

    
    /**
     * @param awardDate the awardDate to set
     */
    public void setAwardDate(DateTime awardDate) {
        this.awardDate = awardDate;
    }

    
    /**
     * @return the startDate
     */
    public DateTime getStartDate() {
        return startDate;
    }

    
    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    
    /**
     * @return the endDate
     */
    public DateTime getEndDate() {
        return endDate;
    }

    
    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    
    /**
     * @return the list of submission URIs
     */
    public List<URI> getSubmissions() {
        return submissions;
    }

    
    /**
     * @param submissions the submission URIs to set
     */
    public void setSubmissions(List<URI> submissions) {
        this.submissions = submissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Grant that = (Grant) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (awardNumber != null ? !awardNumber.equals(that.awardNumber) : that.awardNumber != null) return false;
        if (awardStatus != null ? !awardStatus.equals(that.awardStatus) : that.awardStatus != null) return false;
        if (localAwardId != null ? !localAwardId.equals(that.localAwardId) : that.localAwardId != null) return false;
        if (projectName != null ? !projectName.equals(that.projectName) : that.projectName != null) return false;
        if (primaryFunder != null ? !primaryFunder.equals(that.primaryFunder) : that.primaryFunder != null) return false;
        if (directFunder != null ? !directFunder.equals(that.directFunder) : that.directFunder != null) return false;
        if (pi != null ? !pi.equals(that.pi) : that.pi != null) return false;
        if (coPis != null ? !coPis.equals(that.coPis) : that.coPis != null) return false;
        if (awardDate != null ? !awardDate.equals(that.awardDate) : that.awardDate != null) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
        if (submissions != null ? !submissions.equals(that.submissions) : that.submissions != null) return false;
        return true;

    }

    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (awardNumber != null ? awardNumber.hashCode() : 0);
        result = 31 * result + (awardStatus != null ? awardStatus.hashCode() : 0);
        result = 31 * result + (localAwardId != null ? localAwardId.hashCode() : 0);
        result = 31 * result + (projectName != null ? projectName.hashCode() : 0);
        result = 31 * result + (primaryFunder != null ? primaryFunder.hashCode() : 0);
        result = 31 * result + (directFunder != null ? directFunder.hashCode() : 0);
        result = 31 * result + (pi != null ? pi.hashCode() : 0);
        result = 31 * result + (coPis != null ? coPis.hashCode() : 0);
        result = 31 * result + (awardDate != null ? awardDate.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (submissions != null ? submissions.hashCode() : 0);
        return result;
    }
    
}
