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
     * A local key assigned to the Grant within the researcher's institution to support matching 
     * between PASS and a local system. In the case of JHU this is the key assigned by COEUS
     */
    private String localKey;
    
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
     * URI of the User who is the Principal investigator
     */
    private URI pi;
    
    /** 
     * List of URIs of the [User] who are the co-principal investigators
     */
    private List<URI> coPis = new ArrayList<>();
    
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
        
    /** Status of award/grant */
    public enum AwardStatus {
        @JsonProperty("active")
        ACTIVE("active"),
        @JsonProperty("pre-award")
        PRE_AWARD("pre-award"),
        @JsonProperty("terminated")
        TERMINATED("terminated");

        private static final Map<String, AwardStatus> map = new HashMap<>(values().length, 1);  
        static {
          for (AwardStatus a : values()) map.put(a.value, a);
        }
        
        private String value;
        
        private AwardStatus(String value){
            this.value = value;
        }
        public String getValue() {
            return this.value;
        }
        
        public static AwardStatus of(String status) {
            AwardStatus result = map.get(status);
            if (result == null) {
              throw new IllegalArgumentException("Invalid Award Status: " + status);
            }
            return result;
        }
        
        
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
     * @return the localKey
     */
    public String getLocalKey() {
        return localKey;
    }

    
    /**
     * @param localKey the localKey to set
     */
    public void setLocalKey(String localKey) {
        this.localKey = localKey;
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
     * @return the primaryFunder
     */
    public URI getPrimaryFunder() {
        return primaryFunder;
    }

    
    /**
     * @param primaryFunder the primaryFunder to set
     */
    public void setPrimaryFunder(URI primaryFunder) {
        this.primaryFunder = primaryFunder;
    }

    
    /**
     * @return the directFunder
     */
    public URI getDirectFunder() {
        return directFunder;
    }

    
    /**
     * @param directFunder the directFunder to set
     */
    public void setDirectFunder(URI directFunder) {
        this.directFunder = directFunder;
    }

    
    /**
     * @return the pi
     */
    public URI getPi() {
        return pi;
    }

    
    /**
     * @param pi the pi to set
     */
    public void setPi(URI pi) {
        this.pi = pi;
    }

    
    /**
     * @return the coPis
     */
    public List<URI> getCoPis() {
        return coPis;
    }

    
    /**
     * @param coPis the coPis to set
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Grant that = (Grant) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (awardNumber != null ? !awardNumber.equals(that.awardNumber) : that.awardNumber != null) return false;
        if (awardStatus != null ? !awardStatus.equals(that.awardStatus) : that.awardStatus != null) return false;
        if (localKey != null ? !localKey.equals(that.localKey) : that.localKey != null) return false;
        if (projectName != null ? !projectName.equals(that.projectName) : that.projectName != null) return false;
        if (primaryFunder != null ? !primaryFunder.equals(that.primaryFunder) : that.primaryFunder != null) return false;
        if (directFunder != null ? !directFunder.equals(that.directFunder) : that.directFunder != null) return false;
        if (pi != null ? !pi.equals(that.pi) : that.pi != null) return false;
        if (coPis != null ? !coPis.equals(that.coPis) : that.coPis != null) return false;
        if (awardDate != null ? !awardDate.equals(that.awardDate) : that.awardDate != null) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
        return true;

    }

    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (awardNumber != null ? awardNumber.hashCode() : 0);
        result = 31 * result + (awardStatus != null ? awardStatus.hashCode() : 0);
        result = 31 * result + (localKey != null ? localKey.hashCode() : 0);
        result = 31 * result + (projectName != null ? projectName.hashCode() : 0);
        result = 31 * result + (primaryFunder != null ? primaryFunder.hashCode() : 0);
        result = 31 * result + (directFunder != null ? directFunder.hashCode() : 0);
        result = 31 * result + (pi != null ? pi.hashCode() : 0);
        result = 31 * result + (coPis != null ? coPis.hashCode() : 0);
        result = 31 * result + (awardDate != null ? awardDate.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
    }
    
}
